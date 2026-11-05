package M2fst.demo.controller;

import M2fst.demo.dto.evaluation.CreateEvaluationDTO;
import M2fst.demo.dto.evaluation.CreateResponseEvaluationDTO;
import M2fst.demo.dto.evaluation.UpdateEvaluationDTO;
import M2fst.demo.dto.evaluation.UpdateResponseEvaluationDTO;
import M2fst.demo.dto.restaurant.CreateResponseRestaurantDTO;
import M2fst.demo.dto.restaurant.CreateRestaurantDTO;
import M2fst.demo.dto.restaurant.UpdateRestaurantDTO;
import M2fst.demo.entity.EvaluationEntity;
import M2fst.demo.entity.RestaurantEntity;
import M2fst.demo.exception.ForbiddenException;
import M2fst.demo.exception.NotFoundException;
import M2fst.demo.repository.EvaluationRepository;
import M2fst.demo.repository.RestaurantRepository;
import M2fst.demo.service.EvaluationService;
import M2fst.demo.service.IndexService;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestEvaluationService {


    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private IndexService  indexService;

    @InjectMocks
    private EvaluationService evaluationService;

    @Test
    public void creer_et_retourne_evaluation_si_champs_valident() {
        CreateEvaluationDTO createEvaluationDTO = new CreateEvaluationDTO();
        createEvaluationDTO.setCommentaire("Très bon");
        createEvaluationDTO.setNote(3);

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(1);
        restaurantEntity.setAdresse("Adresse");
        restaurantEntity.setNom("Test");

        EvaluationEntity evaluationEntity = new EvaluationEntity();
        evaluationEntity.setId(1);
        evaluationEntity.setCommentaire("Très bon");
        evaluationEntity.setNote(3);
        evaluationEntity.setRestaurant(restaurantEntity);

        when(this.restaurantRepository.findById(1)).thenReturn(Optional.of(restaurantEntity));
        when(this.evaluationRepository.save(any(EvaluationEntity.class))).thenReturn(evaluationEntity);

        CreateResponseEvaluationDTO createResponseEvaluationDTO = this.evaluationService.createEvaluation(1, createEvaluationDTO, "", "Bob");

        assertThat(createResponseEvaluationDTO).isNotNull();
        assertThat(createResponseEvaluationDTO.getCommentaire()).isEqualTo("Très bon");
        assertThat(createResponseEvaluationDTO.getNote()).isEqualTo(3);
        assertThat(createResponseEvaluationDTO.getId()).isEqualTo(1);

        verify(this.restaurantRepository).findById(1);
        verify(this.indexService).indexEvaluation(
                eq(1),
                eq("Très bon"),
                eq(null),
                eq(3),
                eq(1),
                eq(null)
        );

        verify(this.evaluationRepository).save(any(EvaluationEntity.class));
    }

    @Test
    public void doit_echouer_si_restaurant_invalide() {
        when(this.restaurantRepository.findById(100000)).thenReturn(Optional.empty());

        CreateEvaluationDTO createEvaluationDTO = new CreateEvaluationDTO();

        Assertions.assertThrows(NotFoundException.class, () -> this.evaluationService.createEvaluation(100000, createEvaluationDTO, "", "Bob"));

        verify(this.restaurantRepository, Mockito.never()).save(any());
        verify(this.indexService, Mockito.never()).indexEvaluation(any(Integer.class),
                any(String.class),
                any(String.class),
                any(Integer.class),
                any(Integer.class),
                any());
    }

    @Test
    public void suppression_evaluation_admin(){
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(1);
        restaurantEntity.setAdresse("Adresse");
        restaurantEntity.setNom("Test");

        EvaluationEntity evaluationEntity = new EvaluationEntity();
        evaluationEntity.setId(1);
        evaluationEntity.setEvaluateur("Bob");
        evaluationEntity.setCommentaire("Très bon");
        evaluationEntity.setSubEvaluateur("1234-1234");
        evaluationEntity.setRestaurant(restaurantEntity);

        when(this.evaluationRepository.findById(1)).thenReturn(Optional.of(evaluationEntity));

        this.evaluationService.deleteUtilisateurEvaluation(1, "1234-1234", true);

        assertThat(restaurantEntity.getEvaluations().size()).isEqualTo(0);
        verify(this.evaluationRepository).deleteById(1);

    }

    @Test
    public void suppression_evaluation_user(){
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(1);
        restaurantEntity.setAdresse("Adresse");
        restaurantEntity.setNom("Test");

        EvaluationEntity evaluationEntity = new EvaluationEntity();
        evaluationEntity.setId(1);
        evaluationEntity.setEvaluateur("Bob");
        evaluationEntity.setSubEvaluateur("1234-1234");
        evaluationEntity.setRestaurant(restaurantEntity);

        when(this.evaluationRepository.findByIdAndSubEvaluateur(1, "1234-1234")).thenReturn(Optional.of(evaluationEntity));
        this.evaluationService.deleteUtilisateurEvaluation(1, "1234-1234", false);

        assertThat(restaurantEntity.getEvaluations().size()).isEqualTo(0);
        verify(this.evaluationRepository).deleteById(1);
    }

    @Test
    public void suppression_evaluation_invalide(){
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(1);
        restaurantEntity.setAdresse("Adresse");
        restaurantEntity.setNom("Test");

        EvaluationEntity evaluationEntity = new EvaluationEntity();
        evaluationEntity.setId(1);
        evaluationEntity.setEvaluateur("Bob");
        evaluationEntity.setCommentaire("Très bon");
        evaluationEntity.setSubEvaluateur("1234-1234");
        evaluationEntity.setNote(3);
        evaluationEntity.setRestaurant(restaurantEntity);

        Assertions.assertThrows(NotFoundException.class, () -> this.evaluationService.deleteUtilisateurEvaluation(100000, evaluationEntity.getSubEvaluateur(), true));

        verify(this.evaluationRepository, Mockito.never()).deleteById(any());
    }

    @Test
    public void ajout_photo_evaluation_admin() throws IOException {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(1);
        restaurantEntity.setAdresse("Adresse");
        restaurantEntity.setNom("Test");

        EvaluationEntity evaluationEntity = new EvaluationEntity();
        evaluationEntity.setId(1);
        evaluationEntity.setEvaluateur("Bob");
        evaluationEntity.setCommentaire("Très bon");
        evaluationEntity.setNote(3);
        evaluationEntity.setRestaurant(restaurantEntity);

        when(evaluationRepository.findById(1)).thenReturn(Optional.of(evaluationEntity));

        List<String> listeUrl = List.of("https://www.google.com", "https://www.brave.com");
        this.evaluationService.addPhotoEvaluation(1, listeUrl, "sub", true);

        assertThat(evaluationEntity.getPhoto().size()).isEqualTo(2);
        assertThat(evaluationEntity.getPhoto().get(0)).isEqualTo("https://www.google.com");
        assertThat(evaluationEntity.getPhoto().get(1)).isEqualTo("https://www.brave.com");

        verify(evaluationRepository).findById(1);
        verify(this.indexService).UpdateIndexEvaluation(
                eq(1),
                eq("Très bon"),
                eq("Bob"),
                eq(3),
                eq(1),
                eq(listeUrl)
        );
    }

    @Test
    public void ajout_photo_evaluation_invalide(){

        when(evaluationRepository.findById(1000000)).thenReturn(Optional.empty());

        List<String> listeUrl = List.of("https://www.google.com", "https://www.brave.com");

        Assertions.assertThrows(NotFoundException.class, () -> this.evaluationService.addPhotoEvaluation(1000000, listeUrl, "sub", true));

        verify(evaluationRepository).findById(1000000);
    }

    @Test
    public void ajout_photo_evaluation_non_cree_par_utilisateur() throws IOException {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(1);
        restaurantEntity.setAdresse("Adresse");
        restaurantEntity.setNom("Test");

        EvaluationEntity evaluationEntity = new EvaluationEntity();
        evaluationEntity.setId(1);
        evaluationEntity.setEvaluateur("Bob");
        evaluationEntity.setSubEvaluateur("1234-1234");
        evaluationEntity.setRestaurant(restaurantEntity);

        EvaluationEntity evaluationEntity2 = new EvaluationEntity();
        evaluationEntity2.setId(2);
        evaluationEntity2.setEvaluateur("Alice");
        evaluationEntity2.setSubEvaluateur("9876-9876");
        evaluationEntity2.setRestaurant(restaurantEntity);

        List<String> listeUrl = List.of("https://www.google.com", "https://www.brave.com");

        when(evaluationRepository.findById(2)).thenReturn(Optional.of(evaluationEntity2));


        Assertions.assertThrows(ForbiddenException.class, () -> this.evaluationService.addPhotoEvaluation(2, listeUrl, "1234-1234", false));

        verify(evaluationRepository).findById(2);

    }


    @Test
    public void retourne_photos_evaluation(){
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(1);
        restaurantEntity.setAdresse("Adresse");
        restaurantEntity.setNom("Test");

        EvaluationEntity evaluationEntity = new EvaluationEntity();
        evaluationEntity.setId(1);
        evaluationEntity.setEvaluateur("Bob");
        evaluationEntity.setCommentaire("Très bon");
        evaluationEntity.setNote(3);
        evaluationEntity.setPhoto(List.of("https://www.google.com", "https://www.brave.com"));
        evaluationEntity.setRestaurant(restaurantEntity);

        when(evaluationRepository.findById(1)).thenReturn(Optional.of(evaluationEntity));

        List<String> listeUrl = this.evaluationService.getPhotoEvaluation(1);

        assertThat(listeUrl).isNotNull();
        assertThat(listeUrl.size()).isEqualTo(2);
        assertThat(listeUrl.get(0)).isEqualTo("https://www.google.com");
        assertThat(listeUrl.get(1)).isEqualTo("https://www.brave.com");

        verify(evaluationRepository).findById(1);
    }

    @Test
    public void retourne_photos_evaluation_invalide(){

        when(evaluationRepository.findById(1000000)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> this.evaluationService.getPhotoEvaluation(1000000));

        verify(evaluationRepository).findById(1000000);
    }

    @Test
    public void modification_reussit_si_auteur() throws IOException {
        EvaluationEntity evaluationEntity = new EvaluationEntity();
        evaluationEntity.setId(1);
        evaluationEntity.setEvaluateur("Bob");
        evaluationEntity.setSubEvaluateur("1234-1234");
        evaluationEntity.setCommentaire("Très bon");
        evaluationEntity.setNote(3);

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(1);
        restaurantEntity.setAdresse("Adresse");
        restaurantEntity.setNom("Test");

        evaluationEntity.setRestaurant(restaurantEntity);

        UpdateEvaluationDTO updateEvaluationDTO = new UpdateEvaluationDTO();
        updateEvaluationDTO.setCommentaire("Pas très bon");
        updateEvaluationDTO.setNote(1);

        when(evaluationRepository.findById(1)).thenReturn(Optional.of(evaluationEntity));

        when(evaluationRepository.save(evaluationEntity)).thenReturn(evaluationEntity);

        UpdateResponseEvaluationDTO updateResponseEvaluationDTO = this.evaluationService.updateEvaluation(1, "1234-1234", false, updateEvaluationDTO);

        assertThat(updateResponseEvaluationDTO).isNotNull();
        assertThat(updateResponseEvaluationDTO.getCommentaire()).isEqualTo("Pas très bon");
        assertThat(updateResponseEvaluationDTO.getNote()).isEqualTo(1);
        verify(evaluationRepository).findById(1);
        verify(evaluationRepository).save(evaluationEntity);
        verify(indexService).UpdateIndexEvaluation(eq(1), eq("Pas très bon"), any(), eq(1), eq(1), any());
    }

    @Test
    public void modification_echoue_si_pas_auteur_pas_admin() throws IOException {
        EvaluationEntity evaluationEntity = new EvaluationEntity();
        evaluationEntity.setId(1);
        evaluationEntity.setEvaluateur("Bob");
        evaluationEntity.setSubEvaluateur("1234-1234");
        evaluationEntity.setCommentaire("Très bon");
        evaluationEntity.setNote(3);

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(1);
        restaurantEntity.setAdresse("Adresse");
        restaurantEntity.setNom("Test");

        evaluationEntity.setRestaurant(restaurantEntity);

        UpdateEvaluationDTO updateEvaluationDTO = new UpdateEvaluationDTO();
        updateEvaluationDTO.setCommentaire("Pas très bon");
        updateEvaluationDTO.setNote(1);

        when(evaluationRepository.findById(1)).thenReturn(Optional.of(evaluationEntity));

        Assertions.assertThrows(ForbiddenException.class, () -> this.evaluationService.updateEvaluation(1, "9876-9876", false, updateEvaluationDTO));
    }

}
