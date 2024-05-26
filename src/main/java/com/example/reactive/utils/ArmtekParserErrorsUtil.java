package com.example.reactive.utils;

import com.example.reactive.domain.ArmGoodInfo;
import com.example.reactive.domain.ArmatekLink;
import com.example.reactive.domain.GoodsInfoError;
import com.example.reactive.repository.ArmLinksRepo;
import com.example.reactive.repository.ArmtekGoodInfoRepository;
import com.example.reactive.repository.GoodsInfoErrorRepo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArmtekParserErrorsUtil {
    private final GoodsInfoErrorRepo goodsInfoErrorRepo;
    private final ArmtekGoodInfoRepository armtekGoodInfoRepository;
    private final ArmLinksRepo armLinksRepo;

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2";

    /**
     * Метод парсит страницу и получает адреса для пагинации этой страницы
     * @param url адрес страницы
     * @return количество пагинатов
     */
    public int getAllPaginationLinksGPTV1(String url) {
        log.info("String url = {}", url);
        Set<String> allPaginationUrlsInPage = new HashSet<>();
        String prefUrl = url + "?page=";
        Elements paginationLinks = new Elements();
        while (paginationLinks.isEmpty()) {
            try {
                Document doc = Jsoup.connect(url).timeout(50000).userAgent(USER_AGENT).get();
                paginationLinks = doc.select("a[queryparamshandling=\"merge\"]");
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        for (int i = 1; i <= Integer.parseInt(paginationLinks.get(paginationLinks.size()-1).text()); i++) {
            String newUrl = prefUrl + (i);
                        allPaginationUrlsInPage.add((newUrl));

        }
                List<ArmatekLink> urlsList = allPaginationUrlsInPage.stream().map(u -> new ArmatekLink(u, url)).collect(Collectors.toList());
        Flux<ArmatekLink> urlsListFlux = Flux.fromIterable(urlsList);
        try {
            armLinksRepo.saveAll(urlsListFlux).subscribe();
        } catch (Exception e) {
            log.error("Ошибка при сохранении данных в базу данных", e);
        }
        return  urlsList.size();
    }

}
