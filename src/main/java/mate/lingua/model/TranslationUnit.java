package mate.lingua.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "translation_unit")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranslationUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private String translation;
    @ManyToOne
    @JoinColumn(name = "learning_dataset_id", nullable = false)
    @JsonIgnore
    @Setter
    private LearningDataset learningDataset;
}
