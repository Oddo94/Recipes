package recipes.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DetailedResponse {
    private String resultCode;
    private HttpStatus httpStatus;
    private Object responseMessage;
}
