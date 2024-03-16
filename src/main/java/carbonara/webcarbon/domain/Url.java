package carbonara.webcarbon.domain;

import lombok.Data;

@Data
public class Url {
    private Long id;
    private String url; // 도메인 주소
    private long htmlSize;
    private long cssSize;
    private long jsSize;
    private long totalSize;
    private String grade;

    /* 추가해야할 요소들 */
    private long imageSize;
    private long vidioSize;
    private long quantity;                      // 탄소의 총량 //Carbon quantity.

    /* DB가 생기면 추가해야만 하는 요소 */
    private String date;                        // 최근 3개월 이내에 계산한 기록이 있으면, 계산하지 않고 DB를 조회해서 가져옵니다.

    // 해결해야할 문제)
    // 나중에 url을 DB 조회를 위해 이름을 붙여야한다. ex) 한국-전력공사 웹페이지 -> korea_electronic, 탄소중립위원회 웹페이지 -> korea_carbon ..

    /*
    * 만들어야할 DB
    * # 다른 나라의 국가기관 웹페이지는 어떤가? -> 이는 통계로 보여주면서 우리나라 웹 부하의 문제를 부각시킬 수 있음. !
    * (대체로 친환경적인 국가를.. 골라야합니다.)
    * Korea
    * America
    * swiss
    */
}
