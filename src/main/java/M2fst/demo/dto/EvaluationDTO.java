package M2fst.demo.dto;

import M2fst.demo.entity.EvaluationEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EvaluationDTO {

    private Integer id;

    private String evaluateur;

    private String commentaire;

    private Integer note;

    private List<String> photo;

    private Integer idRestaurant;

    public static EvaluationDTO fromEntity(EvaluationEntity entity) {
        return new EvaluationDTO(entity.getId(), entity.getEvaluateur(), entity.getCommentaire(), entity.getNote(), entity.getPhoto(), entity.getRestaurant().getId());
    }

}
