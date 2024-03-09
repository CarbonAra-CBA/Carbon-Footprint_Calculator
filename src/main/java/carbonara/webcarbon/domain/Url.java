package carbonara.webcarbon.domain;

import lombok.Data;

@Data
public class Url {
    private Long id;
    private String url;
    private Integer initialSize;
    private Integer htmlSize;
    private String date;
    private Long quantity; // Carbon quantity.
}
