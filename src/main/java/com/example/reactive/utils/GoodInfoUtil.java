package com.example.reactive.utils;

import com.example.reactive.domain.ArmGoodInfo;
import com.example.reactive.repository.ArmGoodsLinksRepo;
import com.example.reactive.repository.ArmtekGoodInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoodInfoUtil {

    private final ArmtekGoodInfoRepository goodInfoRepository;
    private final ArmGoodsLinksRepo armGoodsLinksRepo;

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2";

    @SneakyThrows
    public int getGoodsInfo(String url) {
        log.info("Start parsing url {}", url);
        Elements elements = new Elements();
        String name = new String();
        String descriptions = new String();
        while (elements.isEmpty()) {
            try {
                Document doc = Jsoup.connect(url).timeout(1000).userAgent(USER_AGENT).get();
                name = doc.getElementsByAttributeValueStarting("class", "font__headline4").text();
                    descriptions = doc.getElementsByAttributeValueStarting("class", "font__body2 product-card-info__body-block-content").text();
                    elements = doc.getElementsByAttributeValue("class", "product-key-values__column ng-star-inserted");

            } catch (Exception e) {
                log.error("Ошибка парсинга урла {} \n {}", url, e.getMessage());
                if (e.getMessage().replaceAll("\\s", "").equals(" HTTP error fetching URL".replaceAll("\\s", ""))) {
                    armGoodsLinksRepo.deleteByLink(url);
                    return 0;
                }
            }
        }
        Map<String, String> leftRight = new TreeMap<>();
        String finalName = name;
        String finalDescriptions = descriptions;
        elements.forEach(element -> {
            List<Element> rightParts = element.getElementsByAttributeValueStarting("class", "product-key-values__item__right-side");
            List<Element> leftParts = element.getElementsByAttributeValueStarting("class", "font__body2 color-black_36");
            try {
                for (int i = 0; i < leftParts.size(); i++) {
                    String left = getTextFromElementLeft(leftParts.get(i));
                    String right = getTextFromElementRight(rightParts.get(i));
                    leftRight.put(left, right);

                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
        saveNewArmtekGood(leftRight, finalName, finalDescriptions, url);
        return 1;

    }

    private String getTextFromElementRight(Element part) {
        return part.getElementsByAttributeValueStarting("class", "font__body2 color-black_87 ng-star-inserted")
                .parents()
                .get(0)
                .text();
    }

    private String getTextFromElementLeft(Element part) {
        return part.getElementsByAttributeValueStarting("class", "font__body2 color-black_36")
                .parents()
                .get(0)
                .text();
    }

    private void saveNewArmtekGood(Map<String, String> map, String name, String description, String url) {
        String article = "";
        String brand = "";
        String weight = "";
        String length = "";
        String height = "";
        String width = "";
        StringBuilder other = new StringBuilder();

        for (Map.Entry<String, String> entry: map.entrySet()) {
            switch (entry.getKey()) {
                case "Артикул" -> article = entry.getValue();
                case "Бренд" -> brand = entry.getValue();
                case "Ширина" -> width = entry.getValue();
                case "Высота" -> height = entry.getValue();
                case "Длина" -> length = entry.getValue();
                case "Вес в инд. упак." -> weight = entry.getValue();
                default -> other.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
            }
        }

        ArmGoodInfo goodInfo = ArmGoodInfo.builder()
                .code("")
                .name(name)
                .article(article)
                .brand(brand)
                .height(height)
                .length(length)
                .weight(weight)
                .width(width)
                .description(description)
                .other(other.toString())
                .url(url)
                .build();

        log.info(goodInfo.toString());
        if (!goodInfo.getArticle().isBlank()) {
            Flux<ArmGoodInfo> armGoodInfoFlux = Flux.just(goodInfo);
            goodInfoRepository.saveAll(armGoodInfoFlux).subscribe();
        }

    }
}
