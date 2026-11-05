package M2fst.demo.dto.evaluation;


import M2fst.demo.dto.EvaluationDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder

public class CreateResponseEvaluationDTO {

    private Integer id;
    private String evaluateur;
    private String commentaire;
    private Integer note;
    private List<String> photo;

    public static CreateResponseEvaluationDTO fromEntity(Integer id, String evaluateur, String commentaire, Integer note, List<String> photo) {
        return builder()
                .id(id)
                .evaluateur(evaluateur)
                .commentaire(commentaire)
                .note(note)
                .photo(photo)
                .build();
    }
}
