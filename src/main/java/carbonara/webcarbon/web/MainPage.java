package carbonara.webcarbon.web;

import carbonara.webcarbon.domain.Url;
import carbonara.webcarbon.domain.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MainPage {

    private final UrlRepository urlRepository;

    @GetMapping()
    public String main(@ModelAttribute("Url") Url url , Model model) {
        model.addAttribute("url", url);
        return "main";
    }

    @PostMapping()
    public String processUrl(Url url, Model model) {
        // 1. Url 객체 생성 및 저장
        // save 전에 이미 있는 지 check
//        urlCheck()

        Url savedUrl = urlRepository.save(url);

        // Crolling

        // Model 에 담아서 Result에 보낸다.

        // 2. 결과 페이지로 리다이렉션
        return "redirect:/result/" + savedUrl.getUrl();
    }

    @GetMapping("/result")
    public String result(Model model) {

        return "result";
    }



//    @PostMapping("/result")
//    public String result2() {
//
//    }
}
