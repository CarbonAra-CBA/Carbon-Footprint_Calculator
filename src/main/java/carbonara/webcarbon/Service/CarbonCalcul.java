package carbonara.webcarbon.Service;

import carbonara.webcarbon.domain.Url;
import carbonara.webcarbon.domain.UrlRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


// 나중에 계산로직만 따로 여기다 놓을까 생각중
@Service
@RequiredArgsConstructor
@Slf4j
public class CarbonCalcul {

}
