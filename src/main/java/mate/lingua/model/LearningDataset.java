package mate.lingua.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "learning_dataset")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class LearningDataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "learningDataset")
    private List<TranslationUnit> translationUnits;
}
