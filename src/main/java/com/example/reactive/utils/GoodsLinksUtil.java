package com.example.reactive.utils;

import com.example.reactive.domain.ArmatekGoodLink;
import com.example.reactive.repository.ArmGoodsLinksRepo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoodsLinksUtil {
    private final ArmGoodsLinksRepo armGoodsLinksRepo;

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2";

    /**
     * Получает все ссылки на товары на страницах из списка, переданного как параметр
     * @param url список страниц
     */
    @SneakyThrows
    public int getAllGoodsLinks(String url) {
        log.info("String url = {}", url);
        List<ArmatekGoodLink> urlsList = new ArrayList<>();
        List<Element> elements = new ArrayList<>();
        while (elements.isEmpty()) {
            try {
                Document doc = Jsoup.connect(url).timeout(1000).userAgent(USER_AGENT).get();
                elements = doc.select("a[class=title]");
                if (elements.isEmpty()) {
                    elements = doc.select("a[class=card-mobile ng-star-inserted]");
                }
            } catch (Exception e) {
                log.error("Ошибка парсинга урла {} \n {}", url, e.getMessage());
                if (e.getMessage().replaceAll("\\s", "").equals(" HTTP error fetching URL".replaceAll("\\s", ""))) {
                    log.error("thread {}", Thread.currentThread().getName());
                }
            }
        }
            for (Element element : elements) {
                String hrefValue = element.attr("href");
                if (!hrefValue.equals("")) {
                    urlsList.add(new ArmatekGoodLink("https://armtek.ru" + hrefValue, url));
                }
            }
        Flux<ArmatekGoodLink> urlsListFlux = Flux.fromIterable(urlsList);
        try {
            armGoodsLinksRepo.saveAll(urlsListFlux).subscribe();
        } catch (Exception e) {
            log.error("Ошибка при сохранении данных в базу данных", e);
        }

        log.info("urlsList.size() = {}", urlsList.size());
        return  urlsList.size();

    }
}
