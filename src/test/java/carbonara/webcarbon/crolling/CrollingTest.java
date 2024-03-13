package carbonara.webcarbon.crolling;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
/*
*
*
* */



@Slf4j
public class CrollingTest {

    public static void main(String[] args) throws IOException {
        String url = "https://www.naver.com";
        try {
            Document doc = Jsoup.connect(url).get(); // 연결 및 문서 가져오기 // Connect and Get document

            // html
            long totalHtmlSize = doc.html().getBytes().length;
            totalHtmlSize = totalHtmlSize/ 1024; // kb;

            // JS
            Elements jsElements = doc.select("script[src]"); // 모든 JS 태그 선택// Select All JS Tag
            long totalJsSize = 0;
            for (Element element : jsElements) {
                String src = element.absUrl("src"); // JS 파일의 절대 경로 얻기 // Get the absolute URL of the JS file
                long size = getResourceSize(src); // 리소스 크기 가져오기 // Get Size of Resource
                System.out.println("JS File: " + src + " Size: " + size + " bytes");
                totalJsSize += size;
            }

            // CSS Files
            Elements cssElements = doc.select("link[rel='stylesheet']");
            long totalCssSize = 0;
            for (Element element : cssElements) {
                String href = element.absUrl("href");
                long size = getResourceSize(href);
                System.out.println("CSS File: " + href + " Size: " + size + " bytes");
                totalCssSize += size;
            }

            System.out.println("Total JS Size: " + totalJsSize/1024 + " kb");
            System.out.println("Total CSS Size: " + totalCssSize/1024 + " kb");
            System.out.println("Total HTML Size: "+ String.valueOf(totalHtmlSize) + " kb");
            System.out.println("ALL SIZE : " + (totalJsSize/1024 + totalCssSize/1024 + totalHtmlSize) +" kb");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 리소스의 길이를 받아오는 메서드입니다.
    private static long getResourceSize(String resourceUrl) {
        try {
            URL url = new URL(resourceUrl); // Connect;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // URL커넥션 반환 //Return URl Connection

            connection.setRequestProperty("User-Agent", "Mozilla/5.0"); // 유저 세팅을 해야 403 에러를 피할 수 있음.// Setting User-Agent is important to avoid HTTP 403 errors in some websites
            connection.setRequestMethod("HEAD"); // 문서의 헤더 정보만 요청함 // Request Header info
            connection.connect();
            return connection.getContentLengthLong(); // 콘텐츠 길이 리턴. 단, 헤더 길이에 정보가 없으면 -1이 리턴됨 // Returns the file size //
        } catch (IOException e) {
            System.err.println("Error fetching resource size for: " + resourceUrl);
            return 0;
        }
    }
}
