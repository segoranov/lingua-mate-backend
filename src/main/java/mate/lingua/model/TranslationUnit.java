package mate.lingua.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "translation_unit")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TranslationUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private String translation;
    @ManyToOne
    @JoinColumn(name = "learning_dataset_id", nullable = false)
    @JsonIgnore
    private LearningDataset learningDataset;
}
