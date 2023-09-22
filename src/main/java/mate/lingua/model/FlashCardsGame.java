package mate.lingua.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;


@Entity(name = "flash_cards_game")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlashCardsGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "learning_dataset_id", referencedColumnName = "id")
    @JsonIgnore
    private LearningDataset learningDataset;

    @Column(columnDefinition = "jsonb", nullable = false, name = "state")
    @JsonProperty("state")
    @Type(JsonBinaryType.class)
    private FlashCardsGameState flashCardsGameState;
}
