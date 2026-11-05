package M2fst.demo.service;


import M2fst.demo.dto.EvaluationDTO;
import org.apache.coyote.BadRequestException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndexService {

    private DirectoryReader directoryReader;
    private IndexSearcher indexSearcher;
    private final IndexWriter indexWriter;
    private final QueryParser commentQueryParser;

    /**
     * Méthode permettant d'initialisé le service d'indexation Apache Lucene.
     * @throws IOException Si une erreur d'entrée/sortie survient lors de l'accès au répertoire,
     *                     de l'écriture ou de l'ouverture de l'index Lucene.
     */
    public IndexService() throws IOException {
        String    indexDirectoryPath = "index";
        Path path               = Paths.get(indexDirectoryPath);
        Directory index              = FSDirectory.open(path);

        StandardAnalyzer analyzer = new StandardAnalyzer();
        this.commentQueryParser = new QueryParser("commentaire", analyzer);

        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        this.indexWriter = new IndexWriter(index, indexWriterConfig);

        if (DirectoryReader.indexExists(index)) {
            this.directoryReader = DirectoryReader.open(index);
            this.indexSearcher = new IndexSearcher(directoryReader);
        } else {
            // si l'index n'existe pas on le creé
            this.indexWriter.commit();
            this.directoryReader = DirectoryReader.open(index);
            this.indexSearcher = new IndexSearcher(directoryReader);
        }
    }

    /**
     * Méthode permettant d'indexer une nouvelle évaluation dans l'index Lucene et rafraîchit le lecteur d'index.
     * @param idEvaluation l'identifiant unique de l'évaluation
     * @param commentaireEvaluation le contenu textuel du commentaire à indexer
     * @param evaluateur le nom ou l'identifiant de l'auteur de l'évaluation
     * @param note la note attribuée
     * @param idRestaurant l'identifiant du restaurant associé
     * @param photos la liste des 'KEYS' (noms) de fichiers des photos
     * @throws RuntimeException si une IOException survient lors de l'écriture ou du rafraîchissement de l'index
     */
    public void indexEvaluation(Integer idEvaluation, String commentaireEvaluation, String evaluateur, Integer note, Integer idRestaurant, List<String> photos) {
        try {
            Document document = new Document();
            document.add(new StringField("evaluation", String.valueOf(idEvaluation), Field.Store.YES));
            document.add(new TextField("commentaire", commentaireEvaluation, Field.Store.YES));
            document.add(new StringField("evaluateur", evaluateur, Field.Store.YES));
            document.add(new StoredField("note", note));
            document.add(new StoredField("idRestaurant", idRestaurant));
            if(photos != null && !photos.isEmpty()) {
                document.add(new StoredField("photos", String.join(";", photos)));
            }

            this.indexWriter.addDocument(document);
            this.indexWriter.commit();

            DirectoryReader newDirectoryReader = DirectoryReader.openIfChanged(this.directoryReader);
            if (newDirectoryReader != null) {
                this.directoryReader.close();
                this.directoryReader = newDirectoryReader;
                this.indexSearcher = new IndexSearcher(this.directoryReader);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la recherche avec indexation de l'évaluation");
        }
    }


    /**
     * Méthode permettant de rechercher des évaluations dans l'index Lucene à partir d'une requête textuelle.
     * @param texteRecherche l'expression ou les mots-clés de recherche.
     * @return la liste des EvaluationDTO correspondants, ordonnée par score de pertinence.
     * @throws IOException si une erreur survient lors de la lecture des documents dans l'index.
     * @throws BadRequestException si la syntaxe de la requête fournie est invalide pour le parseur Lucene.
     */
    public List<EvaluationDTO> searchEvaluation(String texteRecherche) throws IOException, ParseException {
        if (texteRecherche == null) {
            return List.of();
        }
        try {
            Query query = this.commentQueryParser.parse(texteRecherche);

            // Max 10 évaluations
            int maxHits = 10;
            TopDocs topDocs = this.indexSearcher.search(query, maxHits);

            List<EvaluationDTO> resultats = new ArrayList<>();

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = this.indexSearcher.doc(scoreDoc.doc);

                String idEvaluation = document.get("evaluation");
                String commentaire = document.get("commentaire");
                String evaluateur = document.get("evaluateur");
                Integer note = Integer.parseInt(document.get("note"));
                Integer idRestaurant = Integer.parseInt(document.get("idRestaurant"));
                String photos = document.get("photos");
                List<String> listePhotos = (photos != null && !photos.isBlank())
                        ? List.of(photos.split(";"))
                        : List.of();

                EvaluationDTO evaluation = new EvaluationDTO();

                evaluation.setId(Integer.parseInt(idEvaluation));
                evaluation.setCommentaire(commentaire);
                evaluation.setEvaluateur(evaluateur);
                evaluation.setNote(note);
                evaluation.setIdRestaurant(idRestaurant);
                if(listePhotos != null && !listePhotos.isEmpty()) {
                    evaluation.setPhoto(listePhotos);
                }
                resultats.add(evaluation);
            }
            return resultats;
        }
        catch (ParseException e) {
            throw new BadRequestException(STR."Syntaxe de recherche invalide : \{e.getMessage()}");
        }
    }

    /**
     * Méthode permettant de supprimer l'index des évaluations.
     * @param idEvaluation identifiant unique de l'évaluation.
     * @throws IOException si si une erreur survient lors de la suppression des documents.
     */
    public void DeleteIndexEvaluation(Integer idEvaluation) throws IOException {
        indexWriter.deleteDocuments(new Term("evaluation", String.valueOf(idEvaluation)));
        indexWriter.commit();
        RefreshIndexEvaluation();
    }

    /**
     * Méthode permettant de mettre à jour l'index des évaluations.
     * @param idEvaluation l'identifiant unique de l'évaluation
     * @param commentaireEvaluation le contenu textuel du commentaire à indexer
     * @param evaluateur le nom ou l'identifiant de l'auteur de l'évaluation
     * @param note la note attribuée
     * @param idRestaurant l'identifiant du restaurant associé
     * @param photos la liste des 'KEYS' (noms) de fichiers des photos
     * @throws IOException si si une erreur survient lors de la mise à jour des documents.
     */
    public void UpdateIndexEvaluation(Integer idEvaluation, String commentaireEvaluation, String evaluateur, Integer note, Integer idRestaurant, List<String> photos) throws IOException {
        Document document = new Document();
        document.add(new StringField("evaluation", String.valueOf(idEvaluation), Field.Store.YES));
        document.add(new TextField("commentaire", commentaireEvaluation, Field.Store.YES));
        document.add(new StringField("evaluateur", evaluateur, Field.Store.YES));
        document.add(new StoredField("note", note));
        document.add(new StoredField("idRestaurant", idRestaurant));
        if(photos != null && !photos.isEmpty()) {
            document.add(new StoredField("photos", String.join(";", photos)));
        }
        this.indexWriter.updateDocument(new Term("evaluation", String.valueOf(idEvaluation)), document);
        this.indexWriter.commit();
        RefreshIndexEvaluation();
    }

    /**
     * Rafraîchit le lecteur et le moteur de recherche de l'index Lucene.
     * @throws IOException si une erreur survient lors de la réouverture ou de la fermeture du lecteur d'index
     */
    public void RefreshIndexEvaluation() throws IOException {
        DirectoryReader newDirectoryReader = DirectoryReader.openIfChanged(this.directoryReader, this.indexWriter);
        if  (newDirectoryReader != null) {
            directoryReader.close();
            directoryReader = newDirectoryReader;
            indexSearcher = new IndexSearcher(directoryReader);
        }
    }

}
