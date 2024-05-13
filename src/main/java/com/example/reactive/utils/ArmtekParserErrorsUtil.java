package com.example.reactive.utils;

import com.example.reactive.domain.*;
import com.example.reactive.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArmtekParserErrorsUtil {
    private final ArmLinksRepo repo;
    private final AlfavitCatLinksErrorRepo alfavitCatLinksErrorRepo;
    private final CatLinksErrorsRepo catLinksErrorsRepo;
    private final GoodsInfoErrorRepo goodsInfoErrorRepo;
    private final PaginationLinksErrorRepo paginationLinksErrorRepo;
    private final GoodsLinksErrorRepo goodsLinksErrorRepo;
    private final ArmtekGoodInfoRepository armtekGoodInfoRepository;
    private final ArmLinksRepo armLinksRepo;
//    private final ArmatekGoodLink armatekGoodLink;
private final ParseExcelFile parseExcelFile;

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2";

    /**
     * Сет всех ссылок на страницы с товарами
     */
    Set<String> pageLinks = new HashSet<>();

    /**
     * Сет всех ссылок на страницы, где вышло сообщение об ошибке парсинга
     */
    Set<String> errorsPagesLinks = new HashSet<>();

    /**
     * Сет всех страниц пагинации
     */
    Set<String> allPaginationUrls = new TreeSet<>();

    public void printAllSavedUrls(String url) {
        List<String> firstList = parseExcelFile.getUrles();
        log.info("url = {}", url);
    }

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

    /** Метод парсит страницы каталогов и получает ссылки на каждый pagination__link
     * @param url адреса всех каталогов
     * @return список адресов страниц
     */
    @SneakyThrows
    public void getAllPaginationLinks(String url) {
        List<String> pageNumbers = new ArrayList<>();
        Set<String> allPaginationUrlsInPage = new HashSet<>();
            int sizeLinksOnPage = 0;
            int countOfTries  = 0;
            List<Element> elements = new ArrayList<>();

            Element doc = null;
            try {
                doc = Jsoup.connect(url).timeout(10000).userAgent(USER_AGENT).get();
                Thread.sleep(200);
            } catch (IOException e) {
                log.error("Ошибка парсинга урла {} \n {}", url, String.valueOf(e));
                paginationLinksErrorRepo.save(new PaginationLinksError(url));
            }
            if (doc != null) {
                while (sizeLinksOnPage == 0 && countOfTries < 15) {
                    allPaginationUrlsInPage.add(url);
                    countOfTries++;
                    log.info(String.valueOf(countOfTries));
                    elements = doc.getElementsByAttributeValue("class", "page-item-color-primary-size-md ng-star-inserted");
                    Thread.sleep(1000);
                    sizeLinksOnPage = elements.size();
                    log.info(String.valueOf(sizeLinksOnPage));
                }
                if (elements.isEmpty()) {
                    paginationLinksErrorRepo.save(new PaginationLinksError(url));
                } else {
                    elements.forEach(element -> {
                        Element aTag = element.select("a").last();
                        if (aTag != null) {
                            String pageNumber = aTag.text();
                            pageNumbers.add(pageNumber);
                        }
                    });
                }
                int psize = pageNumbers.size();
                if (psize > 1) {
                    String prefUrl = url + "?page=";
                    for (int i = 2; i <= Integer.parseInt(pageNumbers.get(psize - 1)); i++) {
                        String newUrl = prefUrl + (i);
//                        allPaginationUrlsInPage.add((newUrl));
                        armLinksRepo.save(new ArmatekLink(newUrl));
                    }
                }
            log.info("Список пагинатов урла {} состоит из {} строк", url, allPaginationUrlsInPage.size());
//        allPaginationUrls.addAll(allPaginationUrlsInPage);
        }
//        log.info("Список пагинатов состоит из {} строк", allPaginationUrls.size());

//        List<ArmatekLink> urlslist = allPaginationUrlsInPage.stream().map(ArmatekLink::new).collect(Collectors.toList());
//        log.info("size of list = {}", urlslist.size());
//        Flux<ArmatekLink> urlslist = Flux.fromIterable(allPaginationUrlsInPage)
//                .parallel()
//                .map(ArmatekLink::new)
//                .sequential();
//
//        armLinksRepo.saveAll(urlslist);
//
//        getAllGoodsLinks(allPaginationUrlsInPage);
    }

    /**
     * Получает все ссылки на товары на страницах из списка, переданного как параметр
     * @param url список страниц
     */
    @SneakyThrows
//    public void getAllGoodsLinks(Set<String> urls) {
    public void getAllGoodsLinks(String url) {
        log.info("String url = {}", url);
        List<String> allGoodsUrlsOnPages = new ArrayList<>();
        Set<String> allUrlsSet = new TreeSet<>();
//        for (String url : urls) {
            int sizeLinksOnPage = 0;
            int countOfTries  = 0;
            List<Element> elements = new ArrayList<>();
            Element doc = null;
            try {
                doc = Jsoup.parse(new URL(url), 10000);
            } catch (Exception e) {
                log.error("Ошибка парсинга урла {} \n {}", url, e.getMessage());
                goodsLinksErrorRepo.save(new GoodsLinksError(url));
            }
            if (doc != null) {
                while (sizeLinksOnPage == 0 && countOfTries < 15) {
                    countOfTries++;
                    log.info(String.valueOf(countOfTries));
                    elements = doc.select("a[class=row ng-star-inserted]");
                    Thread.sleep(1000);
                    sizeLinksOnPage = elements.size();
                    log.info("url = {} and sizeLinksOnPage = {} and countOfTries = {}", url, sizeLinksOnPage, countOfTries);
                }
                if (elements.isEmpty()) {
                    goodsLinksErrorRepo.save(new GoodsLinksError(url));
                } else {
                    for (Element element : elements) {
                        String hrefValue = element.attr("href");
                        if (!hrefValue.equals("")) {
                            allUrlsSet.add(("https://armtek.ru" + hrefValue));
                        }
                    }
                }
//            }
        }

        log.info("allUrlsSet.size() = {}", allUrlsSet.size());

    }



    List<String> allUrls = new ArrayList<>();



    List<String> goodsInfoErrors = new ArrayList<>();

    @SneakyThrows
//    public void getGoodsInfo(Set<String> urls) {
    public void getGoodsInfo(String url) {
        log.info("Start parsing goods");
//        for (String url : urls) {
            Element doc = null;
            try {
                doc = Jsoup.connect(url).timeout(10000).userAgent(USER_AGENT).get();

            } catch (Exception e) {
//                log.error("Ошибка парсинга урла {} \n {}", url, e.getMessage());
                goodsInfoErrors.add(url);
            }
            if (doc != null) {
                Map<String, String> leftRight = new TreeMap<>();
                String name = doc.getElementsByAttributeValueStarting("class", "font__headline4").text();
                String descriptions = doc.getElementsByAttributeValueStarting("class", "font__body2 product-card-info__body-block-content").text();
                List<Element> elements = doc.getElementsByAttributeValue("class", "product-key-values__column ng-star-inserted");
                Thread.sleep(1000);
                if (elements.isEmpty()) {
                    goodsInfoErrors.add(url);
                } else {
                    elements.forEach(element -> {
                                List<Element> rightParts = element.getElementsByAttributeValueStarting("class", "font__body2 color-black_87 ng-star-inserted");
                                List<Element> leftParts = element.getElementsByAttributeValueStarting("class", "font__body2 color-black_36");
                                try {
                                    for (int i = 0; i < rightParts.size(); i++) {
                                        String left = getTextFromElementLeft(leftParts.get(i));
                                        String right = getTextFromElementRight(rightParts.get(i));
                                        leftRight.put(left, right);

                                    }
                                } catch (Exception e) {
                                    goodsInfoErrors.add(url);
                                }
                            }
                    );
                }
                saveNewArmtekGood(leftRight, name, descriptions, url);
//            }

        }
        log.info("количество ошибок {}", goodsInfoErrors.size());
        log.info("количество товаров {}", goodInfoList.size());
        armtekGoodInfoRepository.saveAll(goodInfoList);
        List<GoodsInfoError> goodsErrors = goodsInfoErrors.stream().map(GoodsInfoError::new).toList();
        goodsInfoErrorRepo.saveAll(goodsErrors);

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

    List<ArmGoodInfo> goodInfoList = new ArrayList<>();

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

//        log.info(goodInfo.toString());
        if (!goodInfo.getArticle().isBlank()) {
            goodInfoList.add(goodInfo);
        }

    }

}
