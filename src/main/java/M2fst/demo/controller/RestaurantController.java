package M2fst.demo.controller;

import M2fst.demo.dto.RestaurantDTO;
import M2fst.demo.dto.restaurant.CreateResponseRestaurantDTO;
import M2fst.demo.dto.restaurant.CreateRestaurantDTO;
import M2fst.demo.dto.restaurant.UpdateResponseRestaurantDTO;
import M2fst.demo.dto.restaurant.UpdateRestaurantDTO;
import M2fst.demo.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final String bucket = "restaurant";

    public  RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping("/restaurant")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CreateResponseRestaurantDTO> createRestaurant(@Valid @RequestBody CreateRestaurantDTO restaurantDTO, Authentication authentication) {
        CreateResponseRestaurantDTO createResponseRestaurantDTO = this.restaurantService.createRestaurant(restaurantDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createResponseRestaurantDTO);
    }


    @GetMapping("/restaurants")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RestaurantDTO>> getRestaurants(Authentication authentication) {
        List<RestaurantDTO> restaurantDTOList = this.restaurantService.getRestaurants();
        return ResponseEntity.ok(restaurantDTOList);
    }


    @GetMapping("/restaurant/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestaurantDTO> getRestaurantByID(@PathVariable Integer id, Authentication authentication) {
        RestaurantDTO restaurantDTO = this.restaurantService.getRestaurants(id);
        return ResponseEntity.ok(restaurantDTO);
    }


    @PatchMapping("/restaurant/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UpdateResponseRestaurantDTO> updateRestaurant(@PathVariable Integer id, @Valid @RequestBody UpdateRestaurantDTO updateRestaurantDTO, Authentication authentication) {
        UpdateResponseRestaurantDTO updateRestaurant = this.restaurantService.updateRestaurant(id, updateRestaurantDTO);
        return ResponseEntity.ok(updateRestaurant);
    }



}
