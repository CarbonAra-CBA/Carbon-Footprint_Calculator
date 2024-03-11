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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainPage {

    private final UrlRepository urlRepository;

    @GetMapping()
    public String main(@ModelAttribute("Url") Url url, Model model) {
        model.addAttribute("url", url);
        return "main";
    }

    private void calculate(String urldomain,Url url) throws IOException {
        crolling(urldomain,url);
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

    private void crolling(String urldomain,Url url) throws IOException {

        try {
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

            System.out.println("Total JS Size: " + totalJsSize + " bytes");
            System.out.println("Total CSS Size: " + totalCssSize + " bytes");
            System.out.println("Total HTML Size: " + totalHtmlSize + " bytes");

            urlRepository.save(url, urldomain ,totalHtmlSize, totalJsSize, totalCssSize);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @PostMapping("/submit")
    public String processUrl(Model model, @ModelAttribute("Url") Url url, RedirectAttributes redirectAttributes,
                             HttpSession session) {
        // 1. 로딩중과 계산&크롤링을 비동기처리
        CompletableFuture<Integer> futureResult = CompletableFuture.supplyAsync(() -> {
            try {
                return calculate(url.getUrl(),url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        futureResult.thenApply(result -> {
                model.addAttribute("result", result);

            // 2. 결과 페이지로 리다이렉션
            String savedUrl = url.getUrl();
            return "redirect:/result";
        });
        return "loading";

        // 1. Url 객체 생성 및 저장
        // save 전에 이미 있는 지 check
//        urlCheck()
        // 로딩중..

//        Url savedUrl = urlRepository.save();

        // Crolling

        // Model 에 담아서 Result에 보낸다.

    }

    @GetMapping("/loading")
    public String loading() {
        return "loading";
    }

    @GetMapping("/result")
    public String result(ModelAttribute("Url") Url url, Model model) {

    }


//    @GetMapping("/result/${url}")
//    public String result(ModelAttribute("Url") Url url) {
//
//        // Sass
////        return "result";
//    }


//    @PostMapping("/result")
//    public String result2() {
//
//    }
}
