package M2fst.demo.controller;

import M2fst.demo.dto.EvaluationDTO;
import M2fst.demo.dto.evaluation.CreateEvaluationDTO;
import M2fst.demo.dto.evaluation.CreateResponseEvaluationDTO;
import M2fst.demo.dto.evaluation.UpdateEvaluationDTO;
import M2fst.demo.dto.evaluation.UpdateResponseEvaluationDTO;
import M2fst.demo.entity.EvaluationEntity;
import M2fst.demo.repository.EvaluationRepository;
import M2fst.demo.service.EvaluationService;
import M2fst.demo.service.IndexService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController

public class EvaluationController {

    private final EvaluationService evaluationService;
    private final IndexService  indexService;

    public EvaluationController(EvaluationService evaluationService, IndexService indexService) {
        this.evaluationService = evaluationService;
        this.indexService = indexService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/restaurant/{id}/evaluation")
    public ResponseEntity<CreateResponseEvaluationDTO> createEvaluation(@PathVariable Integer id, @Valid @RequestBody CreateEvaluationDTO evaluationDTO, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String subEvaluateur = jwt.getClaimAsString("sub");
        String nomEvaluateur = jwt.getClaimAsString("name");
        if(nomEvaluateur != null && nomEvaluateur.length() > 50) {
            nomEvaluateur = nomEvaluateur.substring(0, 50);
        }
        CreateResponseEvaluationDTO createResponseEvaluationDTO = this.evaluationService.createEvaluation(id, evaluationDTO,  subEvaluateur, nomEvaluateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(createResponseEvaluationDTO);
    }


    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("/evaluation/{id}")
    public ResponseEntity<Map<String, String>> deleteUtilisateurEvaluation(@PathVariable Integer id, Authentication authentication) throws IOException {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String subEvaluateur = jwt.getClaimAsString("sub");
        this.evaluationService.deleteUtilisateurEvaluation(id, subEvaluateur, isAdmin);
        this.indexService.DeleteIndexEvaluation(id);
        return ResponseEntity.ok(Map.of("message", "Evaluation supprimée avec succès"));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PatchMapping("/evaluation/{id}")
    public ResponseEntity<UpdateResponseEvaluationDTO> updateEvaluation(@PathVariable Integer id, @Valid @RequestBody UpdateEvaluationDTO evaluationDTO, Authentication authentication) throws IOException {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String subEvaluateur = jwt.getClaimAsString("sub");
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        UpdateResponseEvaluationDTO updateResponseEvaluationDTO = this.evaluationService.updateEvaluation(id, subEvaluateur, isAdmin, evaluationDTO);
        return ResponseEntity.ok(updateResponseEvaluationDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/evaluation/recherche")
    public ResponseEntity<List<EvaluationDTO>> searchEvaluation(@RequestParam String motCle) throws IOException, ParseException {
        List<EvaluationDTO> evaluationDTOList = this.indexService.searchEvaluation(motCle);
        return ResponseEntity.ok(evaluationDTOList);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/utilisateur/evaluations")
    public ResponseEntity<List<EvaluationDTO>> getEvaluationsByEvaluateur(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String subEvaluateur = jwt.getClaimAsString("sub");
        List<EvaluationDTO> evaluationDTOList = this.evaluationService.getEvaluationByEvaluateur(subEvaluateur);
        return ResponseEntity.ok(evaluationDTOList);
    }


}
