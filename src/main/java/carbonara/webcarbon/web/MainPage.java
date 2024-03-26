package carbonara.webcarbon.web;

import carbonara.webcarbon.domain.Url;
import carbonara.webcarbon.domain.UrlRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainPage {

    private final UrlRepository urlRepository;

    @GetMapping()                                                     // 현재 입력은 http://www.naver.com 으로 해야합니다.
    public String main(@ModelAttribute("Url") Url url, Model model) {
        model.addAttribute("url", url);                     // TO do) http://www.naver.com 입력을 naver.com 으로 해도 되게 해야합니다.!..
        return "main";
    }

    private int calculate(String urldomain,Url url) throws IOException {

        try {
            log.info("log ={}",urldomain);
            Document doc = Jsoup.connect(String.valueOf(urldomain)).get(); // 연결 및 문서 가져오기 // Connect and Get document

            // Html Bytes
            long totalHtmlSize = doc.html().getBytes().length;
            totalHtmlSize = totalHtmlSize / 1024; // kb;

            // JS Files
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

            Elements imageElement = doc.select("img");
            long totalImageSize = 0;
            for (Element image : imageElement) {
                String src = image.absUrl("src");
                if (src.startsWith("//")) {
                    src = "https:" + src;
                }
                long size = getResourceSize(src);
                System.out.println("image File: " + src + "Size: " + size + "bytes");
                totalImageSize += size;
            }

            System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");

            WebDriver driver = new ChromeDriver();

            long selenium_totalSize = 0;
            try {
                // 웹 페이지 로드
                driver.get("https://www.naver.com");

                // JavaScript 실행기
                JavascriptExecutor js = (JavascriptExecutor) driver;

                // 페이지 내 모든 리소스의 크기를 계산하는 JavaScript 코드 실행
                selenium_totalSize = (Long) js.executeScript(
                        "var totalBytes = 0, resources = window.performance.getEntriesByType('resource');" +
                                "resources.forEach(function(resource) {" +
                                "   totalBytes += resource.transferSize;" +
                                "});" +
                                "return totalBytes;");

                System.out.println("[Selenium] Total size of all resources: " + selenium_totalSize + " bytes");

            }
            finally {
                // WebDriver 종료
                driver.quit();
            }

            // 추가해야함 : 이미지
            // 추가 해야함 : 동영상

            System.out.println("[jsoup] Total JS Size: " + totalJsSize/1024 + " kb");
            System.out.println("[jsoup] Total CSS Size: " + totalCssSize/1024 + " kb");
            System.out.println("[jsoup] Total HTML Size: " + totalHtmlSize/1024 + " kb");

            long totalSize = (totalHtmlSize / 1024) + (totalJsSize / 1024) + (totalCssSize / 1024) + (totalImageSize/1024);

            System.out.println("[jsoup] TotalSize: " + totalSize + " kb");
            System.out.println("[selenium] TotalSize: " + selenium_totalSize/1024 +"kb");
            // 등급 평가
            String jsoup_grade = setGrade(totalSize);
            String seleni_grade = setGrade(selenium_totalSize/1024);

            urlRepository.save(url, urldomain ,totalHtmlSize/1024, totalJsSize/1024, totalCssSize/1024, totalSize ,jsoup_grade);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }



    // 등급 평가 함수
    private String setGrade(long totalsize) {
        if (totalsize <= 272.51) return "A+";
        else if (totalsize <=531.15) return "A";
        else if (totalsize <=975.85) return "B";
        else if (totalsize <= 1410.39) return "C";
        else if (totalsize <= 1875.01) return "D";
        else if (totalsize <= 2419.56) return "E";
        else if (totalsize >= 2419.57) return "F";
        return null;
    }

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

    // url 형식을 갖추지 못했다면 https://www. 이 없다면 이를 맨 앞에 붙여주는 함수
    private String fittingDomain(String urlDomain) {
        if(urlDomain.contains("https://www.")) {
            return urlDomain;
        }
        else {
            if(urlDomain.contains("http://www.")) {
                return urlDomain;
            }
            urlDomain = "https://www." + urlDomain;
        }
        return urlDomain;
    }


    @PostMapping("/submit")
    public String processUrl(Model model, @ModelAttribute("Url") Url url, RedirectAttributes redirectAttributes,
                             HttpSession session) {

        String urlDomain = fittingDomain(url.getUrl());
        session.setAttribute("domain",urlDomain);

        try {
            calculate(urlDomain, url);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/result";

    }

    @GetMapping("/loading")
    public String loading() {
        return "loading";
    }

    @GetMapping("/result")
    public String result(Model model,HttpSession session) {

        // url 링크 세션 전송 및 모델 생성
        String domain = (String) session.getAttribute("domain");
        model.addAttribute("domain", session.getAttribute("domain"));
        log.info((String) session.getAttribute("domain"));

        Url byUrl = urlRepository.findByUrl(domain);
        // 계산
        String grade = byUrl.getGrade();
        log.info("grade={}", grade);
        model.addAttribute("grade", grade);
        return "result";
    }

}
