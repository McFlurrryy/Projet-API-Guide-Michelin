package M2fst.demo.controller;

import M2fst.demo.dto.evaluation.UploadPhotoResponseDTO;
import M2fst.demo.repository.RestaurantRepository;
import M2fst.demo.repository.S3Repository;
import M2fst.demo.service.EvaluationService;
import M2fst.demo.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FichierController {

    private final S3Repository s3Repository;
    private final String bucketRestaurant = "restaurant";
    private final String bucketEvaluation = "evaluation";
    private  final RestaurantService  restaurantService;
    private final EvaluationService evaluationService;
    private final RestaurantRepository  restaurantRepository;


    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/restaurant/{id}/image")
    public ResponseEntity<String> getUrlImageRestaurant(@PathVariable Integer id, @RequestParam String fichier, Authentication authentication) throws IOException {
        String nomFichier = STR."\{id}/\{UUID.randomUUID()}-\{fichier}";
        this.restaurantService.setImageRestaurant(id, nomFichier);
        String url = s3Repository.getUploadFileUrl(nomFichier, bucketRestaurant);
        return ResponseEntity.ok(url);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/restaurant/{id}/image")
    public ResponseEntity<String> getImageRestaurant(@PathVariable Integer id) {
        String image = this.restaurantService.getImageRestaurant(id);
        String url = this.s3Repository.getDownloadFileUrl(image, bucketRestaurant);
        return ResponseEntity.ok(url);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping("/evaluation/{id}/photo")
    public ResponseEntity<List<UploadPhotoResponseDTO>> getUrlPhotoPlat(@PathVariable Integer id, @RequestBody List<String> fichierPhotos, Authentication authentication) throws IOException {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String subEvaluateur = jwt.getClaimAsString("sub");
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        List<UploadPhotoResponseDTO> listeUploadPhotoResponseDTO = fichierPhotos.stream().
                map(fichier -> {
                    String nomFichier = STR."\{id}/\{UUID.randomUUID()}-\{fichier}";
                    String url = s3Repository.getUploadFileUrl(nomFichier, bucketEvaluation);
                    return UploadPhotoResponseDTO.fromEntity(nomFichier, url);
                }).toList();
        List<String> cles = listeUploadPhotoResponseDTO.stream()
                .map(UploadPhotoResponseDTO::getCle)
                .toList();
        this.evaluationService.addPhotoEvaluation(id, cles, subEvaluateur, isAdmin);
        return ResponseEntity.ok(listeUploadPhotoResponseDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("evaluation/{id}/photo")
    public ResponseEntity<List<String>> getUrlPhotoPlat(@PathVariable Integer id) {
        List<String> listPhoto = this.evaluationService.getPhotoEvaluation(id);
        if (listPhoto.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        List<String> listUrl = listPhoto.stream()
                .map(cle -> this.s3Repository.getDownloadFileUrl(cle, bucketEvaluation))
                .toList();
        return ResponseEntity.ok(listUrl);
    }
}
