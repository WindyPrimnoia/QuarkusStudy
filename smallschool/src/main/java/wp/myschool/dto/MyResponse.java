package wp.myschool.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;


@Data
public class MyResponse {
    private Object data;
    private ResponseStatus status;
    private Integer httpCode;
    private List<String> message;

    public MyResponse(){
        this.message = new ArrayList<>();
    }

    public void setDefaultSuccess(){
        this.status = ResponseStatus.SUCCESS;
        this.httpCode = 200;
    }

    public void setDefaultError(){
        this.status = ResponseStatus.ERROR;
        this.httpCode = 500;
    }

    public void setDefaultFailedNotFound(){
        this.status = ResponseStatus.FAILED;
        this.httpCode = 404;
    }
}
