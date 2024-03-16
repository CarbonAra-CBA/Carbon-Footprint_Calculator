package carbonara.webcarbon.domain;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class UrlRepository {

    private static final Map<String, Url> store = new HashMap<>();
    private static long sequence = 0L;
    private static final long THREE_MONTHS_IN_MILLIS = 7779456000L;

    public Url save(Url url,String domain, long htmlsize, long jssize,long csssize, long totalSize, String grade) {
        url.setId(++sequence);
        url.setUrl(domain);
        url.setHtmlSize(htmlsize);
        url.setJsSize(jssize);
        url.setCssSize(csssize);
        url.setTotalSize(totalSize);
        url.setGrade(grade);
        store.put(url.getUrl(), url);
        return url;
    }

    public Url findById(String url) {
        return store.get(url);
    }

    public Url findByUrl(String url) {
        return store.get(url);
    }

    public List<Url> findAll() {
        return new ArrayList<>(store.values());
    }

    public void dateUpdate(Url url) throws ParseException {
        // late 는 이미 있다고 가정하겠음.!  update() 함수는 초기화함수에 넣겠음.
        String late = url.getDate();
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String changedDate = sdf.format(calender.getTime());

        // 날짜 비교 후 3 개월 이후면 업데이트하겠다.
        // 밀리세컨드 계산
        Date parse = sdf.parse(late);
        long time = parse.getTime();

        long past = time / 1000;
        long now = calender.getTimeInMillis();

        if(now - past > THREE_MONTHS_IN_MILLIS) { // 3개월 이상이상 변경된 이력이 없다면 업데이트 하겠다.
            url.setDate(changedDate);
        }

    }

    public void clearStore() {
        store.clear();
    }


}
