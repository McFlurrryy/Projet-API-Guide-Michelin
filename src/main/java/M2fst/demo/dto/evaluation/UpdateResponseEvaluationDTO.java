package M2fst.demo.dto.evaluation;

import M2fst.demo.entity.EvaluationEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResponseEvaluationDTO {
    private Integer id;
    private String evaluateur;
    private String commentaire;
    private Integer note;
    private List<String> photo;

    public static UpdateResponseEvaluationDTO fromEntity(Integer id, String evaluateur, String commentaire, Integer note,  List<String> photo) {
        return builder()
                .id(id)
                .evaluateur(evaluateur)
                .commentaire(commentaire)
                .note(note)
                .photo(photo)
                .build();
    }
}
