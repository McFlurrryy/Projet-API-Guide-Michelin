package M2fst.demo.dto.restaurant;


import M2fst.demo.dto.EvaluationDTO;
import M2fst.demo.entity.EvaluationEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CreateResponseRestaurantDTO {

    private Integer id;
    private String nom;
    private String adresse;
    private String image;
    private List<EvaluationDTO> evaluations;
    private Double moyenne;

    public static CreateResponseRestaurantDTO fromEntity(Integer id, String nom, String adresse) {
        return builder()
                .id(id)
                .nom(nom)
                .adresse(adresse)
                .moyenne(-1.0)
                .build();
    }
}
