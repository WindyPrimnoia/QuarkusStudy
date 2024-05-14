package wp.myschool.dto;

import lombok.Data;

@Data
public class Pagination {
    private Object content;
    private Integer currentPage;
    private Integer totalPage;
}
