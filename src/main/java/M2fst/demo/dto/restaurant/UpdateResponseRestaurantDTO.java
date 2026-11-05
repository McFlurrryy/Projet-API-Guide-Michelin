package M2fst.demo.dto.restaurant;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResponseRestaurantDTO {
    private String nom;
    private String adresse;

    public static UpdateResponseRestaurantDTO fromEntity(String nom, String adresse) {
        return builder()
                .nom(nom)
                .adresse(adresse)
                .build();

    }
}
