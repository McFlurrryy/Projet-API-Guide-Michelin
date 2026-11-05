package M2fst.demo.controller;

import M2fst.demo.dto.RestaurantDTO;
import M2fst.demo.dto.restaurant.CreateResponseRestaurantDTO;
import M2fst.demo.dto.restaurant.CreateRestaurantDTO;
import M2fst.demo.dto.restaurant.UpdateResponseRestaurantDTO;
import M2fst.demo.dto.restaurant.UpdateRestaurantDTO;
import M2fst.demo.entity.EvaluationEntity;
import M2fst.demo.entity.RestaurantEntity;
import M2fst.demo.exception.NotFoundException;
import M2fst.demo.repository.RestaurantRepository;
import M2fst.demo.service.RestaurantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestRestaurantService {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    @Test
    public void creer_et_retourne_restaurant_si_champs_valident() {
        CreateRestaurantDTO createRestaurantDTO = new CreateRestaurantDTO();
        createRestaurantDTO.setAdresse("Adresse");
        createRestaurantDTO.setNom("Test");

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(1);
        restaurantEntity.setAdresse("Adresse");
        restaurantEntity.setNom("Test");


        when(this.restaurantRepository.save(any(RestaurantEntity.class))).thenReturn(restaurantEntity);

        CreateResponseRestaurantDTO  createResponseRestaurantDTO = this.restaurantService.createRestaurant(createRestaurantDTO);

        assertThat(createRestaurantDTO).isNotNull();
        assertThat(createResponseRestaurantDTO.getAdresse()).isEqualTo("Adresse");
        assertThat(createRestaurantDTO.getNom()).isEqualTo("Test");
        assertThat(restaurantEntity.getId()).isEqualTo(1);

        verify(restaurantRepository).save(any(RestaurantEntity.class));
    }



    @Test
    public void retourne_liste_restaurants() {
        RestaurantEntity restaurantEntity1 = new RestaurantEntity();
        restaurantEntity1.setId(1);
        restaurantEntity1.setAdresse("Adresse1");
        restaurantEntity1.setNom("Test1");

        RestaurantEntity restaurantEntity2 = new RestaurantEntity();
        restaurantEntity2.setId(2);
        restaurantEntity2.setAdresse("Adresse2");
        restaurantEntity2.setNom("Test2");

        List<RestaurantEntity> listeRestaurant = new ArrayList<>();
        listeRestaurant.add(restaurantEntity1);
        listeRestaurant.add(restaurantEntity2);

        when(this.restaurantRepository.findAll()).thenReturn(listeRestaurant);


        List<RestaurantDTO> listeRestaurants = this.restaurantService.getRestaurants();

        assertThat(listeRestaurants).isNotNull();
        assertThat(listeRestaurants.size()).isEqualTo(2);
        assertThat(listeRestaurants.get(0).getAdresse()).isEqualTo("Adresse1");
        assertThat(listeRestaurants.get(1).getAdresse()).isEqualTo("Adresse2");
        assertThat(listeRestaurants.get(0).getNom()).isEqualTo("Test1");
        assertThat(listeRestaurants.get(1).getNom()).isEqualTo("Test2");

        verify(restaurantRepository).findAll();
    }

    @Test
    public void retourne_restaurant_sans_evaluation_moyenne(){
        RestaurantEntity restaurantEntity1 = new RestaurantEntity();
        restaurantEntity1.setId(1);
        restaurantEntity1.setAdresse("Adresse1");
        restaurantEntity1.setNom("Test1");

        when(this.restaurantRepository.findById(any(Integer.class))).thenReturn(Optional.of(restaurantEntity1));

        RestaurantDTO restaurantDTO = this.restaurantService.getRestaurants(1);

        assertThat(restaurantDTO).isNotNull();
        assertThat(restaurantDTO.getAdresse()).isEqualTo("Adresse1");
        assertThat(restaurantDTO.getNom()).isEqualTo("Test1");
        assertThat(restaurantDTO.getId()).isEqualTo(1);
        assertThat(restaurantDTO.getMoyenne()).isEqualTo(-1.0);
        verify(restaurantRepository).findById(any(Integer.class));
    }

    @Test
    public void retourne_restaurant_avec_evaluation_moyenne(){
        RestaurantEntity restaurantEntity1 = new RestaurantEntity();
        restaurantEntity1.setId(1);
        restaurantEntity1.setAdresse("Adresse1");
        restaurantEntity1.setNom("Test1");

        EvaluationEntity evaluationEntity1 = new EvaluationEntity();
        evaluationEntity1.setId(1);
        evaluationEntity1.setNote(3);
        evaluationEntity1.setRestaurant(restaurantEntity1);

        EvaluationEntity evaluationEntity2 = new EvaluationEntity();
        evaluationEntity2.setId(2);
        evaluationEntity2.setNote(3);
        evaluationEntity2.setRestaurant(restaurantEntity1);

        EvaluationEntity evaluationEntity3 = new EvaluationEntity();
        evaluationEntity3.setId(3);
        evaluationEntity3.setNote(0);
        evaluationEntity3.setRestaurant(restaurantEntity1);

        List<EvaluationEntity> listeEvaluation = new ArrayList<>();
        listeEvaluation.add(evaluationEntity1);
        listeEvaluation.add(evaluationEntity2);
        listeEvaluation.add(evaluationEntity3);

        restaurantEntity1.setEvaluations(listeEvaluation);

        when(this.restaurantRepository.findById(any(Integer.class))).thenReturn(Optional.of(restaurantEntity1));

        RestaurantDTO restaurantDTO = this.restaurantService.getRestaurants(1);

        assertThat(restaurantDTO).isNotNull();
        assertThat(restaurantDTO.getAdresse()).isEqualTo("Adresse1");
        assertThat(restaurantDTO.getNom()).isEqualTo("Test1");
        assertThat(restaurantDTO.getId()).isEqualTo(1);
        assertThat(restaurantDTO.getMoyenne()).isEqualTo(2.0);
        verify(restaurantRepository).findById(any(Integer.class));
    }



    @Test
    public void retourne_restaurant_avec_id() {

        RestaurantEntity restaurantEntity1 = new RestaurantEntity();
        restaurantEntity1.setId(1);
        restaurantEntity1.setAdresse("Adresse1");
        restaurantEntity1.setNom("Test1");

        RestaurantEntity restaurantEntity2 = new RestaurantEntity();
        restaurantEntity2.setId(2);
        restaurantEntity2.setAdresse("Adresse2");
        restaurantEntity2.setNom("Test2");


        when(this.restaurantRepository.findById(1)).thenReturn(Optional.of(restaurantEntity1));

        RestaurantDTO restaurantDTO = this.restaurantService.getRestaurants(1);

        assertThat(restaurantDTO).isNotNull();
        assertThat(restaurantDTO.getAdresse()).isEqualTo("Adresse1");
        assertThat(restaurantDTO.getNom()).isEqualTo("Test1");
        assertThat(restaurantDTO.getId()).isEqualTo(1);

        verify(restaurantRepository).findById(1);

    }

    @Test
    public void doit_echouer_restaurant_avec_id_invalide() {

        when(this.restaurantRepository.findById(100000)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> this.restaurantService.getRestaurants(100000));
        verify(restaurantRepository).findById(100000);

    }

    @Test
    public void retourne_update_restaurant() {

        UpdateRestaurantDTO updateRestaurantDTO = new UpdateRestaurantDTO();
        updateRestaurantDTO.setAdresse("Nancy");
        updateRestaurantDTO.setNom("McDonald");

        RestaurantEntity restaurantEntity1 = new RestaurantEntity();
        restaurantEntity1.setId(1);
        restaurantEntity1.setAdresse("Adresse1");
        restaurantEntity1.setNom("Test1");

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurantEntity1));
        when(this.restaurantRepository.save(any(RestaurantEntity.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        UpdateResponseRestaurantDTO updateResponseRestaurantDTO = this.restaurantService.updateRestaurant(1, updateRestaurantDTO);

        assertThat(updateResponseRestaurantDTO.getAdresse()).isEqualTo("Nancy");
        assertThat(updateResponseRestaurantDTO.getNom()).isEqualTo("McDonald");

        verify(restaurantRepository).findById(1);
        verify(restaurantRepository).save(any(RestaurantEntity.class));

    }


    @Test
    public void doit_echouer_update_restaurant() {

        UpdateRestaurantDTO updateRestaurantDTO = new UpdateRestaurantDTO();
        updateRestaurantDTO.setAdresse("Nancy");
        updateRestaurantDTO.setNom("McDonald");

        when(restaurantRepository.findById(100000)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> this.restaurantService.updateRestaurant(100000, updateRestaurantDTO));

        verify(restaurantRepository).findById(100000);

    }



    @Test
    public void image_restaurant() {

        RestaurantEntity restaurantEntity1 = new RestaurantEntity();
        restaurantEntity1.setId(1);
        restaurantEntity1.setAdresse("Adresse1");
        restaurantEntity1.setNom("Test1");

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurantEntity1));
        when(this.restaurantRepository.save(any(RestaurantEntity.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        this.restaurantService.setImageRestaurant(1, "image");

        assertThat(restaurantEntity1.getImage()).isEqualTo("image");

        verify(restaurantRepository).findById(1);
        verify(restaurantRepository).save(any(RestaurantEntity.class));

    }

    @Test
    public void doit_echouer_image_restaurant_invalide() {

        when(restaurantRepository.findById(100000)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> this.restaurantService.setImageRestaurant(100000, "image"));

        verify(restaurantRepository).findById(100000);

    }

    @Test
    public void retourne_image_restaurant() {
        RestaurantEntity restaurantEntity1 = new RestaurantEntity();
        restaurantEntity1.setId(1);
        restaurantEntity1.setAdresse("Adresse1");
        restaurantEntity1.setNom("Test1");
        restaurantEntity1.setImage("image");
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurantEntity1));

        String image = this.restaurantService.getImageRestaurant(1);

        assertThat(image).isEqualTo("image");

        verify(restaurantRepository).findById(1);
    }

    @Test
    public void doit_echouer_get_image_restaurant_invalide() {
        when(restaurantRepository.findById(100000)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> this.restaurantService.getImageRestaurant(100000));
        verify(restaurantRepository).findById(100000);
    }






}
