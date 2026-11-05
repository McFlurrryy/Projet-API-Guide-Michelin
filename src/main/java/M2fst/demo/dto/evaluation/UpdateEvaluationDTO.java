package M2fst.demo.dto.evaluation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEvaluationDTO {

    @Size(max = 255, message = "Un commentaire doit contenir au plus 255 caractères")
    String commentaire;

    @Min(value = 0, message = "La note doit être 0, 1, 2 ou 3")
    @Max(value = 3, message = "La note doit être 0, 1, 2 ou 3")
    Integer note;
}
