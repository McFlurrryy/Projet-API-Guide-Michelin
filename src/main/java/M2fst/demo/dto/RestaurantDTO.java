package M2fst.demo.dto;

import M2fst.demo.entity.EvaluationEntity;
import M2fst.demo.entity.RestaurantEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RestaurantDTO {

    private Integer id;

    private String nom;

    private String adresse;

    private List<EvaluationDTO> evaluations;

    private String image;

    private Double moyenne;

    public static RestaurantDTO fromEntity(RestaurantEntity entity) {
        if (entity == null) return null;

        List<EvaluationDTO> evaluations = entity.getEvaluations().stream()
                .map(EvaluationDTO::fromEntity)
                .toList();

        // Si le restaurant n'a pas d'évaluation, sa moyenne vaut -1.
        Double moyenne = -1.0;
        Integer somme = 0;
        // Sinon, on calcule la moyenne avec les notes des évaluations.
        if (!evaluations.isEmpty()){
            for (EvaluationDTO evaluationDTO : evaluations) {
                somme += evaluationDTO.getNote();
            }
            moyenne = (double) somme/ evaluations.size();
        }

        return new RestaurantDTO(entity.getId(), entity.getNom(), entity.getAdresse(), evaluations, entity.getImage(), moyenne);
    }

}
