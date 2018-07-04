package com.raysmond.blog.services;

import com.raysmond.blog.services.SettingService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.domingosuarez.boot.autoconfigure.jade4j.JadeHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Raysmond<i@raysmond.com>
 */
@JadeHelper("App")
@Service
public class AppSetting {

    private static final Logger logger = LoggerFactory.getLogger(AppSetting.class);

    private SettingService settingService;

    private String siteName = "SpringBlog";
    private String siteSlogan = "An interesting place to discover";
    private Integer pageSize = 5;
    private String storagePath = "/tmp";
    private String mainUri = "http://localhost/";
    private String telegramMasterChatId = "";
    private String subscriptionLink = "";

    public static final String SITE_NAME = "site_name";
    public static final String SITE_SLOGAN = "site_slogan";
    public static final String PAGE_SIZE = "page_size";
    public static final String STORAGE_PATH = "storage_path";
    public static final String MAIN_URI = "main_uri";
    public static final String TELEGRAM_MASTER_CHAT_ID = "telegram_master_chat_id";
    public static final String SUBSCRIPTION_LINK = "subscription_link";

    @Value("${app.adsence.head}")
    private String adsenceHeadCodePath;
    private String adsenceHeadCode = null;

    public String getAdsenceHeadCode() {
        if (adsenceHeadCode == null) {
            adsenceHeadCode = readFile(adsenceHeadCodePath);
        }
        return adsenceHeadCode;
    }

    @Value("${app.adsence.top")
    private String adsenceTopCodePath;
    private String adsenceTopCode = null;

    public String getAdsenceTopCode() {
        if (adsenceTopCode == null) {
            adsenceTopCode = readFile(adsenceTopCodePath);
        }
        return adsenceTopCode;
    }

    @Value("${app.adsence.bottom")
    private String adsenceBottomCodePath;
    private String adsenceBottomCode = null;

    public String getAdsenceBottomCode() {
        if (adsenceBottomCode == null) {
            adsenceBottomCode = readFile(adsenceBottomCodePath);
        }
        return adsenceBottomCode;
    }

    private String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader
                     = new BufferedReader(new InputStreamReader(
                (new ClassPathResource(filePath)).getInputStream()
        ))
        ) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error("ERROR loading file: "+filePath, e);
            return "";
        }
    }

    @Autowired
    public AppSetting(SettingService settingService){
        this.settingService = settingService;
    }

    public String getSiteName(){
        return (String) settingService.get(SITE_NAME, siteName);
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
        settingService.put(SITE_NAME, siteName);
    }

    public Integer getPageSize() {
        return (Integer) settingService.get(PAGE_SIZE, pageSize);
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        settingService.put(PAGE_SIZE, pageSize);
    }

    public String getSiteSlogan() {
        return (String) settingService.get(SITE_SLOGAN, siteSlogan);
    }

    public void setSiteSlogan(String siteSlogan) {
        this.siteSlogan = siteSlogan;
        settingService.put(SITE_SLOGAN, siteSlogan);
    }

    public String getStoragePath() {
        return (String) settingService.get(STORAGE_PATH, storagePath);
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
        settingService.put(STORAGE_PATH, storagePath);
    }

    public String getMainUri() {
        String uri = (String) settingService.get(MAIN_URI, mainUri);
        if (!uri.endsWith("/")) {
            uri += "/";
        }
        return uri;
    }

    public String getMainUriStripped() {
        String uri = (String) settingService.get(MAIN_URI, mainUri);
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length()-1);
        }
        return uri;
    }

    public void setMainUri(String mainUri) {
        this.mainUri = mainUri;
        settingService.put(MAIN_URI, mainUri);
    }

    public String getTelegramMasterChatId() {
        return (String) settingService.get(TELEGRAM_MASTER_CHAT_ID, telegramMasterChatId);
    }

    public void setTelegramMasterChatId(String id) {
        this.telegramMasterChatId = id;
        settingService.put(TELEGRAM_MASTER_CHAT_ID, id);
    }

    public String getSubscriptionLink() {
        return (String) settingService.get(SUBSCRIPTION_LINK, subscriptionLink);
    }

    public void setSubscriptionLink(String link) {
        this.subscriptionLink = link;
        settingService.put(SUBSCRIPTION_LINK, link);
    }

    public List<String> getOgLocales() {
        ArrayList<String> ogLocales = new ArrayList<>();
        ogLocales.add("en_EN");
        ogLocales.add("ru_RU");
        return ogLocales;
    }

    public List<String> getOgTypes() {
        ArrayList<String> ogTypes = new ArrayList<>();
        ogTypes.add("article");
        return ogTypes;
    }
}
