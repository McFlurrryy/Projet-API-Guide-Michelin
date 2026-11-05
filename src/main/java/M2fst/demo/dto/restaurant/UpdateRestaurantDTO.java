package M2fst.demo.dto.restaurant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRestaurantDTO {

    @Size(min = 1, max = 90, message = "Le nom d'un restaurant doit contenir entre 1 et 90 caractères")
    private String nom;

    @Size(min = 1, max = 255, message = "L'adresse d'un restaurant doit contenir entre 1 et 255 caractères")
    private String adresse;
}
