package carbonara.webcarbon.crolling;

import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;

import java.time.Duration;
import java.util.List;


@Slf4j
public class CrollingTest_Selenium {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        try {
            // 웹 페이지 로드
            driver.get("https://www.naver.com");

            // JavaScript 실행기
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // 페이지 내 모든 리소스의 크기를 계산하는 JavaScript 코드 실행
            long totalBytes = (Long) js.executeScript(
                    "var totalBytes = 0, resources = window.performance.getEntriesByType('resource');" +
                            "resources.forEach(function(resource) {" +
                            "   totalBytes += resource.transferSize;" +
                            "});" +
                            "return totalBytes;");

            System.out.println("Total size of all resources: " + totalBytes + " bytes");
        }
        finally {
            // WebDriver 종료
            driver.quit();
        }
    }
}
