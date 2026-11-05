package M2fst.demo.dto.evaluation;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEvaluationDTO {
    @NotBlank(message = "Le commentaire est obligatoire")
    @Size(max = 255, message = "Un commentaire doit contenir au plus 255 caractères")
    private String commentaire;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 0, message = "La note doit être 0, 1, 2 ou 3")
    @Max(value = 3, message = "La note doit être 0, 1, 2 ou 3")
    private Integer note;

}
