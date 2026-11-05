package M2fst.demo.dto.restaurant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CreateRestaurantDTO {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 1, max = 90, message = "Le nom d'un restaurant doit contenir entre 1 et 90 caractères")
    private String nom;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(min = 1, max = 255, message = "L'adresse d'un restaurant doit contenir entre 1 et 255 caractères")
    private String adresse;
}
