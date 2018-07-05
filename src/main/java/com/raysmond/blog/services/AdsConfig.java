package com.raysmond.blog.services;

import com.domingosuarez.boot.autoconfigure.jade4j.JadeHelper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by bvn13 on 05.07.2018.
 */
@JadeHelper("Ads")
@Configuration
public class AdsConfig {

    private static final Logger logger = LoggerFactory.getLogger(AdsConfig.class);

    @Value("${app.ads.adsence.head}")
    private String adsenceHeadCodePath;
    @Getter
    private String adsenceHeadCode = "";

    @Value("${app.ads.adsence.top}")
    private String adsenceTopCodePath;
    @Getter
    private String adsenceTopCode = "";

    @Value("${app.ads.adsence.bottom}")
    private String adsenceBottomCodePath;
    @Getter
    private String adsenceBottomCode = "";

    @Value("${app.ads.yandex.bottom}")
    private String yandexBottomCodePath;
    @Getter
    private String yandexBottomCode = "";

    @Value("${app.ads.yandex.top}")
    private String yandexTopCodePath;
    @Getter
    private String yandexTopCode = "";

    @Value("${app.ads.yandex.main_page}")
    private String yandexMainPageCodePath;
    @Getter
    private String yandexMainPageCode = "";


    @PostConstruct
    private void init() {
        adsenceHeadCode = readFile(adsenceHeadCodePath);

        adsenceTopCode = readFile(adsenceTopCodePath);
        adsenceBottomCode = readFile(adsenceBottomCodePath);

        yandexTopCode = readFile(yandexTopCodePath);
        yandexBottomCode = readFile(yandexBottomCodePath);

        yandexMainPageCode = readFile(yandexMainPageCodePath);
    }


    private String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader((new ClassPathResource(filePath)).getInputStream())
        )) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error("ERROR loading file: "+filePath, e);
            return "";
        }
    }

}
