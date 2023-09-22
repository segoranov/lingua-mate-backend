package mate.lingua.errorhandling;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class ApiError {
    private String error;
    private Date timestamp;
}
