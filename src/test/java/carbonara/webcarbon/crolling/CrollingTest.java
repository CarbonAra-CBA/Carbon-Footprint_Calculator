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
        try {
            Document doc = Jsoup.connect(url).get(); // 연결 및 문서 가져오기 // Connect and Get document

            // html Test
            long totalHtmlSize = doc.html().getBytes().length;
             // kb;

            // JS Test
            Elements jsElements = doc.select("script[src]"); // 모든 JS 태그 선택// Select All JS Tag
            long totalJsSize = 0;
            for (Element element : jsElements) {
                String src = element.absUrl("src"); // JS 파일의 절대 경로 얻기 // Get the absolute URL of the JS file
                long size = getResourceSize(src); // 리소스 크기 가져오기 // Get Size of Resource
                System.out.println("JS File: " + src + " Size: " + size + " bytes");
                totalJsSize += size;
            }

            // CSS Test
            Elements cssElements = doc.select("link[rel='stylesheet']");
            long totalCssSize = 0;
            for (Element element : cssElements) {
                String href = element.absUrl("href");
                long size = getResourceSize(href);
                System.out.println("CSS File: " + href + " Size: " + size + " bytes");
                totalCssSize += size;
            }

            // image Test
            Elements imageElement = doc.select("img");
            long totalImageSize = 0;
            for (Element image : imageElement) {
                String src = image.attr("src");
//            for (Element image : imageElement) {
//                String src = image.attr("src");
//                long size = getResourceSize(src);
//                System.out.println("image File: " + src + "Size: " + size + "bytes");
//                totalImageSize += size;
//            }

            // image v2 //  (이미지 소스를 받기 위해서) 상대경로 -> 절대경로 사용.
            for (Element image : imageElement) {
                String src = image.absUrl("src");
                if (src.startsWith("//")) {
                    src = "https:" + src;
                }
                long size = getResourceSize(src);
                System.out.println("image File: " + src + "Size: " + size + "bytes");
                totalImageSize += size;
            }

            System.out.println("Total JS Size: " + totalJsSize/1024 + " kb");
            System.out.println("Total CSS Size: " + totalCssSize/1024 + " kb");
            System.out.println("Total HTML Size: "+ totalHtmlSize/1024 + " kb");
            System.out.println("Total Image Size: " + totalImageSize/1024 + " kb");
            System.out.println("ALL SIZE : " + (totalJsSize/1024 + totalCssSize/1024 + totalHtmlSize) +" kb");

            // Video Test
            Elements videoElement = doc.select("video");
            long totalVideoSize = 0;
//
//            for (Element video : videoElement) {
//                String src = video.attr("src");
//                long size = getResourceSize(src);
//                System.out.println("Video File: " + src + "Size: " + size + "bytes");
//                totalVideoSize += size;
//            }

            for (Element video : videoElement) {
                String src = video.attr("src");
                if (src.startsWith("//")) {
                    src = "https:" + src;
                }
                long size = getResourceSize(src);
                System.out.println("Video File: " + src + "Size: " + size + "bytes");
                totalVideoSize += size;
            }

            System.out.println("Total JS Size: " + totalJsSize/1024 + " kb");
            System.out.println("Total CSS Size: " + totalCssSize/1024 + " kb");
            System.out.println("Total HTML Size: "+ String.valueOf(totalHtmlSize) + " kb");
            System.out.println("Total Image Size: " + totalImageSize/1024 + " kb");
            System.out.println("Total Video Size: " + totalVideoSize/1024 + " kb");

            long allSize = (totalJsSize / 1024) + (totalCssSize / 1024) + (totalHtmlSize/1024);
            System.out.println("ALL SIZE : " + allSize +" kb");

            System.out.println("Carbon Emission Grade: " + getCarbonGrade(allSize) );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // carbon 배출 등급
    private static String getCarbonGrade(long totalsize) {
        if (totalsize <= 272.51) return "A+";
        else if (totalsize <=531.15) return "A";
        else if (totalsize <=975.85) return "B";
        else if (totalsize <= 1410.39) return "C";
        else if (totalsize <= 1875.01) return "D";
        else if (totalsize <= 2419.56) return "E";
        else if (totalsize >= 2419.57) return "F";

        return null; // 예외처리.. -> 실제 controller 에서는 throw exception.
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
