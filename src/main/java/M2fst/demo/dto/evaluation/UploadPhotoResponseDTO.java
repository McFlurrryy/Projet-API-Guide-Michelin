package M2fst.demo.dto.evaluation;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder

public class UploadPhotoResponseDTO {

    private String cle;
    private String url;

    public static UploadPhotoResponseDTO fromEntity(String cle, String url) {
        return builder()
                .cle(cle)
                .url(url)
                .build();
    }

}
