package M2fst.demo.repository;

import M2fst.demo.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<EvaluationEntity, Integer> {
    List<EvaluationEntity> findBySubEvaluateur(String commentaire);

    Optional<EvaluationEntity> findByIdAndSubEvaluateur(Integer id, String subEvaluateur);

}
