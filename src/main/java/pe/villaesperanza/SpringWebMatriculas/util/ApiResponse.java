package pe.villaesperanza.SpringWebMatriculas.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private Boolean success;
    private Integer code;
    private Integer status;
    private String message;
    private String debugMessage;
    private String timestamp;
    private String timezone;
    private T data;

    public ApiResponse() {
        this.timestamp = new Date().toString();
        this.timezone = new Date().toString();
    }

    public ApiResponse<T> toSuccess(T object) {
        this.success = true;
        this.code = 0;
        this.status = HttpStatus.OK.value();
        this.message = "Operation performed";
        this.data = object;
        return this;
    }
}
