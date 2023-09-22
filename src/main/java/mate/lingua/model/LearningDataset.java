package mate.lingua.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LearningDataset {
    // TODO validation

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @OneToOne(mappedBy = "learningDataset")
    @JsonIgnore
    private FlashCardsGame flashCardsGame;

    @OneToMany(mappedBy = "learningDataset", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TranslationUnit> translationUnits;
}
