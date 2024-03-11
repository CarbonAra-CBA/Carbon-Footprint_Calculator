package carbonara.webcarbon.crolling;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.util.Elements;
import java.io.IOException;

@Slf4j
public class CrollingTest {

    /*
    * 1. 웹 주소
    * 2. html size
    * 3. js size
    * 4. css size
    * */

    public static void main(String[] args) throws IOException {

        String url = "https://www.naver.com";
        Document document = Jsoup.connect(String.valueOf(url)).get();

        // html Byte 길이를 나타낸다.
        long totalHtmlSize = document.html().getBytes().length;
//        totalHtmlSize = totalHtmlSize/ 1024; // kb;
        log.info("Total HTML Size: {}",String.valueOf(totalHtmlSize));

        // JS 파일 크기
        long totalJsSize = 0;

        for (Element element : document.select("script[src]")) {
            String src = element.attr("src");
            if (src.startsWith("http")) {
                totalJsSize += Jsoup.connect(src).execute().bodyAsBytes().length;
            }
        }

        // CSS 파일 크기
        long totalCssSize = 0;
        for (Element element : document.select("link[href][rel=stylesheet]")) {
            String href = element.attr("href");
            if (href.startsWith("http")) {
                totalCssSize += Jsoup.connect(href).execute().bodyAsBytes().length;
            }
        }

        log.info("Total JS Size: " + totalJsSize + " bytes");
        log.info("Total CSS Size: " + totalCssSize + " bytes");
    }

}
