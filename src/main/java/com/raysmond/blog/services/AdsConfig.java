package com.raysmond.blog.services;

import com.domingosuarez.boot.autoconfigure.jade4j.JadeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

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
    private String adsenceHeadCode = null;

    public String getAdsenceHeadCode() {
        if (adsenceHeadCode == null) {
            adsenceHeadCode = readFile(adsenceHeadCodePath);
        }
        return adsenceHeadCode;
    }

    @Value("${app.ads.adsence.top}")
    private String adsenceTopCodePath;
    private String adsenceTopCode = null;

    public String getAdsenceTopCode() {
        if (adsenceTopCode == null) {
            adsenceTopCode = readFile(adsenceTopCodePath);
        }
        return adsenceTopCode;
    }

    @Value("${app.ads.adsence.bottom}")
    private String adsenceBottomCodePath;
    private String adsenceBottomCode = null;

    public String getAdsenceBottomCode() {
        if (adsenceBottomCode == null) {
            adsenceBottomCode = readFile(adsenceBottomCodePath);
        }
        return adsenceBottomCode;
    }

    @Value("${app.ads.yandex.bottom}")
    private String yandexBottomCodePath;
    private String yandexBottomCode = null;

    public String getYandexBottomCode() {
        if (yandexBottomCode == null) {
            yandexBottomCode = readFile(yandexBottomCodePath);
        }
        return yandexBottomCode;
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
