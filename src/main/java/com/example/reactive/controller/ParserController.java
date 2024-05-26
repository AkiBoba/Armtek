package com.example.reactive.controller;

import com.example.reactive.domain.ArmGoodInfo;
import com.example.reactive.domain.ArmatekGoodLink;
import com.example.reactive.domain.ArmatekLink;
import com.example.reactive.repository.ArmGoodsLinksRepo;
import com.example.reactive.repository.ArmLinksRepo;
import com.example.reactive.repository.ArmtekGoodInfoRepository;
import com.example.reactive.utils.ArmtekParserErrorsUtil;
import com.example.reactive.utils.GoodInfoUtil;
import com.example.reactive.utils.GoodsLinksUtil;
import com.example.reactive.utils.ParseExcelFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParserController {
    private final ArmtekParserErrorsUtil armtekParserErrorUtil;
    private final ArmLinksRepo armLinksRepo;
    private final ArmGoodsLinksRepo armGoodsLinksRepo;
    private final ParseExcelFile parseExcelFile;
    private final GoodsLinksUtil goodsLinksUtil;
    private final ArmtekGoodInfoRepository goodInfoRepository;
    private final GoodInfoUtil goodInfoUtil;

    /**
     * Метод получает адрес, где находятся ссылки на все каталоги сайта
     */
    @GetMapping("/parser")
    public void getList() {
        log.info("Поступил запрос на парсинг сайта");

        /**
         * Получаем список адресов из файла
         */
        List<String> urlsList = parseExcelFile.getUrles();

        urlsList = urlsList.stream().parallel().map(s->s.split("\\?")[0]).collect(Collectors.toList());
        /**
         * Полученный список преобразуем в реактивный поток и отправляем из него адреса в обработку для получения всех адресов пагинации
         */
        Flux<String> urlsFlux = Flux.fromIterable(urlsList);
        getListNotSavedUrls(urlsFlux);

    }

    /**
     * Метод получает списки адресов из репозитория и из файла, список из файла уменьшает
     * на список из репозитория, и повторно парсит адреса для получения ссылок на пагинаты
     * @throws InterruptedException
     */
    @GetMapping("/re_parser")
    public void checkAndRePArsing() throws InterruptedException {

        /**
         * Получаем список адресов из файла
         */
        List<String> urlsList = parseExcelFile.getUrles();
        //проверяем и удаляем лишнее в адресе, остается только адрес до знака ?
        urlsList = urlsList.stream().parallel().map(s->s.split("\\?")[0]).collect(Collectors.toList());

        Flux<ArmatekLink> flux = armLinksRepo.findAll();
        flux
                .parallel()
                .runOn(Schedulers.parallel())
                .map(ArmatekLink::getParentLink)
                .doOnNext(urlsList::remove)
                .subscribe();
        Thread.sleep(2000);

        getListNotSavedUrls(Flux.fromIterable(urlsList));

    }

    @GetMapping("/good_links_parser")
    public void getGoodLinks() throws InterruptedException {

        List<String> urlsList = new ArrayList<>();
        List<String> urlsForParseList = new ArrayList<>();

        Flux<ArmatekGoodLink> urlsFlux = armGoodsLinksRepo.findAll();
        urlsFlux
                .parallel()
                .runOn(Schedulers.parallel())
                .map(ArmatekGoodLink::getParentLink)
                .doOnNext(urlsList::add)
                .subscribe();
        Thread.sleep(5000);

        urlsList = urlsList.stream().parallel().distinct().collect(Collectors.toList());
        log.info("list is done");

        Flux<ArmatekLink> armatekLinkFlux = armLinksRepo.findAll();
        armatekLinkFlux
                .parallel()
                .runOn(Schedulers.parallel())
                .map(ArmatekLink::getLink)
                .doOnNext(urlsForParseList::add)
                .subscribe();
        Thread.sleep(2000);

        log.info("urlsList size = {}", urlsList.size());

        urlsForParseList.removeAll(urlsList);

        log.info("urlsForParseList size = {}", urlsForParseList.size());

        Flux<String> flux = Flux.fromIterable(urlsForParseList);
        flux
                .parallel()
                .runOn(Schedulers.parallel())
                .doOnNext(goodsLinksUtil::getAllGoodsLinks)
                .subscribe();
    }

    /**
     * Загружает из файла адреса, загружает адреса из БД, получает не загруженные адреса в БД, и эти адреса записывает в БД
     * @throws InterruptedException
     */
    @GetMapping("/saveUrl")
    public void saveUrl() throws InterruptedException {

        List<String> urlsList = parseExcelFile.getUrles();

        urlsList = urlsList.stream().parallel().map(s->s.split("\\?")[0]).collect(Collectors.toList());

        Flux<ArmatekLink> flux = armLinksRepo.findAll();
        flux
                .parallel()
                .runOn(Schedulers.parallel())
                .map(ArmatekLink::getParentLink)
                .doOnNext(urlsList::remove)
                .subscribe();
        Thread.sleep(2000);

        urlsList.forEach(url -> {
        Flux<ArmatekLink> armatekLinkFlux = Flux.just(new ArmatekLink(url, url));
        armLinksRepo.saveAll(armatekLinkFlux).subscribe();
            log.info("url - {}", url);
            }
        );
    }

    /**
     * Загружает из файла адреса, загружает адреса из БД, получает не загруженные адреса в БД, и эти адреса записывает в БД
     * @throws InterruptedException
     */
    @GetMapping("/saveGoodsInfo")
    public void saveGoodInfo() throws InterruptedException {
        log.info("starting parsing goods info");
        List<String> urlsInDb = new ArrayList<>();
        List<String> urlsForParsing = new ArrayList<>();
        Flux<ArmGoodInfo> armatekLinkFlux = goodInfoRepository.findAll();
        Flux<ArmatekGoodLink> goodLinkFlux = armGoodsLinksRepo.findAll();

        armatekLinkFlux
                .parallel()
                .runOn(Schedulers.parallel())
                .map(ArmGoodInfo::getUrl)
                .doOnNext(urlsInDb::add)
                .subscribe();
        Thread.sleep(10000);

        goodLinkFlux
                .parallel()
                .runOn(Schedulers.parallel())
                .map(ArmatekGoodLink::getLink)
                .doOnNext(urlsForParsing::add)
                .subscribe();
        Thread.sleep(10000);

        urlsForParsing.removeAll(urlsInDb);

        log.info("urlsForParsing size = {}", urlsForParsing.size());

        urlsForParsing.stream().parallel().forEach(goodInfoUtil::getGoodsInfo);
//        goodInfoUtil.getGoodsInfo("https://armtek.ru/product/nabor-klyuchey-kombinirovannyh-treshchetochnyh-8sht-total-40885684");

    }

    private void getListNotSavedUrls(Flux<String> urlsFlux) {
        urlsFlux
                .parallel()
                .runOn(Schedulers.parallel())
                .doOnNext(armtekParserErrorUtil::getAllPaginationLinksGPTV1)
                .subscribe();
    }

}
