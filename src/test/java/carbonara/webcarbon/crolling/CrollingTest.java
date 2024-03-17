package carbonara.webcarbon.crolling;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Statement;
/*
*
*
* */



@Slf4j
public class CrollingTest {

    public static void main(String[] args) throws IOException {
//        String url = "https://ko.wikipedia.org/wiki/%EC%9C%84%ED%82%A4%EB%B0%B1%EA%B3%BC:%EB%8C%80%EB%AC%B8";
        String url = "https://www.youtube.com/watch?v=KpI1rpp8l6Y&ab_channel=simone%EC%8B%9C%EB%AA%A8%EB%84%A4";
        analyzeWebPage(url);
    }

    // 계산 로직을 코드 재사용성 증가를 위해 함수화했음.
    // getTotalResourceSize 함수를 추가하였음.
    public static void analyzeWebPage(String url) throws IOException {
        try {
            Document doc = Jsoup.connect(url).get();
            long totalHtmlSize = doc.html().getBytes().length;
            long totalJsSize = getTotalResourceSize(doc, "script[src]", "src");
            long totalCssSize = getTotalResourceSize(doc, "link[rel='stylesheet']", "href");
            long totalImageSize = getTotalResourceSize(doc, "img", "src");
            long totalVideoSize = getTotalResourceSize(doc, "video", "src");

            System.out.println("Total JS Size: " + totalJsSize / 1024 + " kb");
            System.out.println("Total CSS Size: " + totalCssSize / 1024 + " kb");
            System.out.println("Total HTML Size: " + totalHtmlSize / 1024 + " kb");
            System.out.println("Total Image Size: " + totalImageSize / 1024 + " kb");
            System.out.println("Total Video Size: " + totalVideoSize / 1024 + " kb");

            long allSize = (totalJsSize / 1024) + (totalCssSize / 1024) + (totalHtmlSize / 1024) + (totalImageSize / 1024) + (totalVideoSize / 1024);
            System.out.println("ALL SIZE : " + allSize + " kb");
            System.out.println("Carbon Emission Grade: " + getCarbonGrade(allSize));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long getTotalResourceSize(Document doc, String cssQuery, String attributeKey) {
        Elements elements = doc.select(cssQuery);
        long totalSize = 0;
        for (Element element : elements) {
            String url = element.absUrl(attributeKey);
            if (url.startsWith("//")) {
                url = "https:" + url;
            }
            long size = getResourceSize(url);
            System.out.println("Resource: " + url + " Size: " + size + " bytes");
            totalSize += size;
        }
        return totalSize;
    }

    private static String getCarbonGrade(long totalSize) {
        // Your grading logic
        if (totalSize <= 272.51) return "A+";
        else if (totalSize <= 531.15) return "A";
        else if (totalSize <= 975.85) return "B";
        else if (totalSize <= 1410.39) return "C";
        else if (totalSize <= 1875.01) return "D";
        else if (totalSize <= 2419.56) return "E";
        else if (totalSize >= 2419.57) return "F";
        return "Unknown"; // Fallback for unexpected cases
    }

    private static long getResourceSize(String resourceUrl) {
        // Your resource size fetching logic
        try {
            URL url = new URL(resourceUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestMethod("HEAD");
            connection.connect();
            return connection.getContentLengthLong();
        } catch (IOException e) {
            System.err.println("Error fetching resource size for: " + resourceUrl);
            return 0;
        }
    }
}
