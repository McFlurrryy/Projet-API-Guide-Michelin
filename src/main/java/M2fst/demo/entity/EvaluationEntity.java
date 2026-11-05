package M2fst.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Evaluation")
public class EvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name= "sub_evaluateur", nullable = false)
    private String subEvaluateur;

    @Column(name = "evaluateur", nullable = false, length = 50)
    private String evaluateur;

    @Column(name = "commentaire", length = 255)
    private String commentaire;

    @Column(name = "note", nullable = false)
    private Integer note;

    @ElementCollection
    @CollectionTable(name = "plats_photos",  joinColumns = @JoinColumn(name = "id_evaluation"))
    @Column(name = "photo_url")
    private List<String> photo;

    @ManyToOne
    @JoinColumn(name = "id_restaurant")
    private RestaurantEntity restaurant;

}
