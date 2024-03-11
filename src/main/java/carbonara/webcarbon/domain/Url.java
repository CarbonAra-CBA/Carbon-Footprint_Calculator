package carbonara.webcarbon.domain;

import lombok.Data;

@Data
public class Url {
    private Long id;
    private String url;
    private long initialSize;
    private long htmlSize;
    private long cssSize;
    private long jsSize;
    private String date;
    private long quantity; // Carbon quantity.
}
