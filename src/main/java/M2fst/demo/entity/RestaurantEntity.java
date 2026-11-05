package M2fst.demo.entity;


import M2fst.demo.dto.EvaluationDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Restaurant")
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "adresse")
    private String adresse;

    //liste d'évaluation a ajouter
    @OneToMany(mappedBy="restaurant")
    private List<EvaluationEntity> evaluations = new ArrayList<>();

    @Column(name = "image")
    private String image;


}
