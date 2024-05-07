package wp.myschool.dto;

import java.util.ArrayList;
import java.util.List;

public class Response {
    public Object data;
    public ResponseStatus status;
    public Integer httpCode;
    public List<String> message;

    public Response(){
        this.message = new ArrayList<>();
    }
}
