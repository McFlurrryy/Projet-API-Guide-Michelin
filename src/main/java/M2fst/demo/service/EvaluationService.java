package M2fst.demo.service;

import M2fst.demo.dto.EvaluationDTO;
import M2fst.demo.dto.evaluation.CreateEvaluationDTO;
import M2fst.demo.dto.evaluation.CreateResponseEvaluationDTO;
import M2fst.demo.dto.evaluation.UpdateEvaluationDTO;
import M2fst.demo.dto.evaluation.UpdateResponseEvaluationDTO;
import M2fst.demo.entity.EvaluationEntity;
import M2fst.demo.entity.RestaurantEntity;
import M2fst.demo.exception.ForbiddenException;
import M2fst.demo.exception.NotFoundException;
import M2fst.demo.repository.EvaluationRepository;
import M2fst.demo.repository.RestaurantRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final RestaurantRepository restaurantRepository;
    private final IndexService indexService;



    /**
     * Méthode permettant de créer une Evaluation
     *
     * @param idRestaurant Identifiant unique du restaurant.
     * @param evaluationDTO Objet contenant les champs permettant d'instancier l'évaluation.
     * @param evaluateur String permettant de récupérer le nom de l'évaluateur passé la récupération dans le JWT.
     * @return CreateResponseEvaluationDTO avec les données.
     * @throws NotFoundException Si aucun restaurant ne correspond à l'identifiant fourni.
     */
    public CreateResponseEvaluationDTO createEvaluation(Integer idRestaurant, CreateEvaluationDTO evaluationDTO, String subEvaluateur, String evaluateur) {
        RestaurantEntity restaurantEntity = this.restaurantRepository.findById(idRestaurant).orElseThrow(() -> new NotFoundException("Restaurant introuvable"));

        EvaluationEntity evaluation = new EvaluationEntity();

        evaluation.setEvaluateur(evaluateur);
        evaluation.setSubEvaluateur(subEvaluateur);
        evaluation.setCommentaire(evaluationDTO.getCommentaire());
        evaluation.setNote(evaluationDTO.getNote());
        evaluation.setRestaurant(restaurantEntity);

        EvaluationEntity evaluationBDD = evaluationRepository.save(evaluation);

        this.indexService.indexEvaluation(
                evaluationBDD.getId(),
                evaluationBDD.getCommentaire(),
                evaluationBDD.getEvaluateur(),
                evaluationBDD.getNote(),
                evaluationBDD.getRestaurant().getId(),
                evaluationBDD.getPhoto()
        );

        return CreateResponseEvaluationDTO.fromEntity(
                evaluationBDD.getId(),
                evaluationBDD.getEvaluateur(),
                evaluationBDD.getCommentaire(),
                evaluationBDD.getNote(),
                evaluationBDD.getPhoto()
        );
    }


    /**
     * Méthode permettant de supprimer une évaluation que l'utilisateur a créé s'il a le role USER, sinon si le role est ADMIN permet à l'admin de supprimer une évaluation.
     *
     * @param idEvaluation Identifiant unique du de l'évaluation.
     * @param subEvaluateur String du sub de l'évaluateur.
     * @param isAdmin Booléen pour dire si l'utilisateur est un admin ou non.
     * @throws NotFoundException Si aucune évaluation ne correspond à l'identifiant fourni.
     * @throws ForbiddenException Si aucune évaluation ne correspond à l'identifiant et au sub fournis.
     */
    public void deleteUtilisateurEvaluation(Integer idEvaluation, String subEvaluateur, boolean isAdmin ) {
        EvaluationEntity evaluationEntity;
        if (isAdmin) {
            evaluationEntity = this.evaluationRepository.findById(idEvaluation).orElseThrow(() -> new NotFoundException(STR."Evaluation avec l'id \{idEvaluation} est introuvable"));
        } else {
            evaluationEntity = this.evaluationRepository.findByIdAndSubEvaluateur(idEvaluation, subEvaluateur).orElseThrow(() -> new ForbiddenException(STR."Evaluation avec l'id \{idEvaluation} est introuvable ou vous n'avez pas les autorisations pour la supprimer"));
        }
        this.evaluationRepository.deleteById(evaluationEntity.getId());
    }


    /**
     * Méthode permettant de mettre à jour une évaluation que l'utilisateur a créé s'il a le role USER, sinon si le role est ADMIN, permet à l'admin de mettre à jour une évaluation.
     *
     * @param idEvaluation Identifiant unique du de l'évaluation.
     * @param subEvaluateur String du sub de l'évaluateur.
     * @param isAdmin Booléen pour dire si l'utilisateur est un admin ou non.
     * @return UpdateResponseEvaluationDTO avec les nouvelles informations de l'évaluation
     * @throws NotFoundException Si aucune évaluation ne correspond à l'identifiant fourni.
     * @throws ForbiddenException Si aucune évaluation ne correspond à l'identifiant et au sub fournis.
     */
    public UpdateResponseEvaluationDTO updateEvaluation(Integer idEvaluation, String subEvaluateur, boolean isAdmin, UpdateEvaluationDTO evaluationDTO) throws IOException {
        EvaluationEntity evaluationEntity = this.evaluationRepository.findById(idEvaluation).orElseThrow(() -> new NotFoundException(STR."Evaluation avec l'id \{idEvaluation} est introuvable"));

        if(!isAdmin && !evaluationEntity.getSubEvaluateur().equals(subEvaluateur)) {
            throw new ForbiddenException("Vous n'avez pas l'autorisation de modifier cette évaluation");
        }

        if(evaluationDTO.getCommentaire() != null && !evaluationDTO.getCommentaire().isBlank()) {
            evaluationEntity.setCommentaire(evaluationDTO.getCommentaire());
        }
        if(evaluationDTO.getNote() != null){
            evaluationEntity.setNote(evaluationDTO.getNote());
        }
        EvaluationEntity evaluationBDD = evaluationRepository.save(evaluationEntity);
        this.indexService.UpdateIndexEvaluation(
                evaluationEntity.getId(),
                evaluationEntity.getCommentaire(),
                evaluationEntity.getEvaluateur(),
                evaluationEntity.getNote(),
                evaluationEntity.getRestaurant().getId(),
                evaluationEntity.getPhoto());

        return UpdateResponseEvaluationDTO.fromEntity(
                evaluationBDD.getId(),
                evaluationBDD.getEvaluateur(),
                evaluationBDD.getCommentaire(),
                evaluationBDD.getNote(),
                evaluationBDD.getPhoto());
    }


    /**
     * Méthode permettant d'ajouter une liste de photos (String) à une évaluation que l'utilisateur a créé s'il a le role USER.
     *
     * @param idEvaluation Identifiant unique du de l'évaluation.
     * @param subEvaluateur String du sub de l'évaluateur.
     * @param isAdmin Booléen pour dire si l'utilisateur est un admin ou non.
     * @param photo Liste de string qui correspondent aux 'KEYS' (noms) des photos.
     * @throws NotFoundException Si aucune évaluation ne correspond à l'identifiant fourni.
     * @throws ForbiddenException Si aucune évaluation ne correspond à l'identifiant et au sub fournis.
     */
    public void addPhotoEvaluation(Integer idEvaluation, List<String> photo,String subEvaluateur, boolean isAdmin) throws IOException {
        EvaluationEntity evaluationEntity = this.evaluationRepository.findById(idEvaluation).orElseThrow(() -> new NotFoundException(STR."Evaluation avec l'id \{idEvaluation} est introuvable"));

        if(!isAdmin && !evaluationEntity.getSubEvaluateur().equals(subEvaluateur)) {
            throw new ForbiddenException("Vous n'avez pas l'autorisation d'ajouter de photos à cette évaluation");
        }

        if(evaluationEntity.getPhoto() == null){
            evaluationEntity.setPhoto(new ArrayList<>());
        }
        evaluationEntity.getPhoto().addAll(photo);

        EvaluationEntity evaluationBDD = evaluationRepository.save(evaluationEntity);

        this.indexService.UpdateIndexEvaluation(evaluationEntity.getId(), evaluationEntity.getCommentaire(), evaluationEntity.getEvaluateur(), evaluationEntity.getNote(), evaluationEntity.getRestaurant().getId(), evaluationEntity.getPhoto());
    }

    /**
     * Méthode permettant de récupérer les 'KEYS' (noms) des photos.
     *
     * @param idEvaluation Identifiant unique du de l'évaluation.
     * @return Liste de string des 'KEYS' (noms) des photos de l'évaluation.
     * @throws NotFoundException Si aucune évaluation ne correspond à l'identifiant fourni.
     */
    public List<String> getPhotoEvaluation(Integer idEvaluation) {
        EvaluationEntity evaluationEntity = this.evaluationRepository.findById(idEvaluation).orElseThrow(() -> new NotFoundException(STR."Evaluation avec l'id \{idEvaluation} est introuvable"));
        return evaluationEntity.getPhoto();
    }

    /**
     * Méthode permettant de récupérer les évaluations de l'utilisateur qui est connecté.
     *
     * @param subEvaluateur Identifiant unique de l'utilisateur.
     * @return Liste d'évaluations.
     */
    public List<EvaluationDTO> getEvaluationByEvaluateur(String subEvaluateur) {
        List<EvaluationEntity> evaluations = this.evaluationRepository.findBySubEvaluateur(subEvaluateur);
        return evaluations.stream()
                .map(EvaluationDTO::fromEntity)
                .toList();
    }
}
