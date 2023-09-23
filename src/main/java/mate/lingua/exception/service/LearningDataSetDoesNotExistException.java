package mate.lingua.exception.service;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LearningDataSetDoesNotExistException extends Exception {
    private long learningDataSetId;
}
