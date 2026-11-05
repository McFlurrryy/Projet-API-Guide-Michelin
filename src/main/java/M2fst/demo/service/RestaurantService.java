package M2fst.demo.service;

import M2fst.demo.dto.RestaurantDTO;
import M2fst.demo.dto.restaurant.CreateResponseRestaurantDTO;
import M2fst.demo.dto.restaurant.CreateRestaurantDTO;
import M2fst.demo.dto.restaurant.UpdateResponseRestaurantDTO;
import M2fst.demo.dto.restaurant.UpdateRestaurantDTO;
import M2fst.demo.entity.RestaurantEntity;
import M2fst.demo.exception.NotFoundException;
import M2fst.demo.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    /**
     * Méthode permettant de créer un restaurant.
     * @param restaurantDTO custom DTO qui prend les informations nécéssaires pour la création.
     * @return CreateResponseRestaurantDTO avec l'id, le nom et l'adresse du restaurant.
     */
    public CreateResponseRestaurantDTO createRestaurant(CreateRestaurantDTO restaurantDTO) {
        RestaurantEntity restaurant = new  RestaurantEntity();
        restaurant.setNom(restaurantDTO.getNom());
        restaurant.setAdresse(restaurantDTO.getAdresse());

        RestaurantEntity restaurantBDD = this.restaurantRepository.save(restaurant);

        return CreateResponseRestaurantDTO.fromEntity(
                restaurantBDD.getId(),
                restaurantBDD.getNom(),
                restaurantBDD.getAdresse()
        );
    }

    /**
     * Méthode permettant de retourner la liste de tous les restaurants.
     * @return Liste de RestaurantDTO
     */
    public List<RestaurantDTO> getRestaurants() {
        List<RestaurantEntity> listeRestaurant = this.restaurantRepository.findAll();
        return listeRestaurant.stream()
                .map(RestaurantDTO::fromEntity)
                .toList();
    }

    /**
     * Méthode permettant de retourner un seul restaurant grâce à son id.
     * @param id identifiant unique du restaurant.
     * @return un RestaurantDTO
     */
    public RestaurantDTO getRestaurants(int id) {
        RestaurantEntity restaurant = this.restaurantRepository.findById(id).orElseThrow(() -> new NotFoundException(STR."Restaurant avec l'id \{id} est introuvable")) ;
        return RestaurantDTO.fromEntity(restaurant);
    }

    /**
     * Méthode perméttant de mettre à jour un restaurant.
     * @param id identifiant unique du restaurant.
     * @param updateRestaurantDTO qui possède les informations à modifier, soit le nom, soit l'adresse ou les deux.
     * @return UpdateResponseRestaurantDTO avec le nouveau nom et la nouvelle adresse.
     */
    public UpdateResponseRestaurantDTO updateRestaurant(int id, UpdateRestaurantDTO updateRestaurantDTO) {
        RestaurantEntity restaurant = this.restaurantRepository.findById(id).orElseThrow(() -> new NotFoundException(STR."Restaurant avec l'id \{id} est introuvable"));

        if (updateRestaurantDTO.getNom() != null && !updateRestaurantDTO.getNom().isEmpty()) {
            restaurant.setNom(updateRestaurantDTO.getNom());
        }

        if (updateRestaurantDTO.getAdresse() != null && !updateRestaurantDTO.getAdresse().isEmpty()) {
            restaurant.setAdresse(updateRestaurantDTO.getAdresse());
        }
        RestaurantEntity restaurantBDD = this.restaurantRepository.save(restaurant);

        return UpdateResponseRestaurantDTO.fromEntity(restaurantBDD.getNom(),  restaurantBDD.getAdresse());
    }

    /**
     * Méthode perméttant d'ajouter une image à un restaurant.
     * @param id identifiant unique du restaurant.
     * @param image string de la 'KEY' (nom) de l'image.
     */
    public void setImageRestaurant(Integer id, String image) {
        RestaurantEntity restaurant = this.restaurantRepository.findById(id).orElseThrow(() -> new NotFoundException(STR."Restaurant avec l'id \{id} est introuvable"));
        restaurant.setImage(image);
        this.restaurantRepository.save(restaurant);
    }

    /**
     * Méthode perméttant de retourner la 'KEY' (nom) de l'image en BDD.
     * @param id identifiant unique du restaurant.
     * @return un string de la 'KEY' (nom) de l'image
     */
    public String getImageRestaurant(Integer id) {
        RestaurantEntity restaurantEntity = this.restaurantRepository.findById(id).orElseThrow(() -> new NotFoundException(STR."Restaurant avec l'id \{id} est introuvable"));
        return restaurantEntity.getImage();
    }


}
