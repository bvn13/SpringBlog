package com.raysmond.blog.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;

/**
 * Created by bvn13 on 21.12.2017.
 */
@Slf4j
@Component
public class TelegramBotSettings {

    @Getter
    @Value("${telegram.bot.enabled}")
    private Boolean enabled;

    @Getter
    @Value("${telegram.bot.name}")
    private String botName;

    @Getter
    @Value("${telegram.bot.channel}")
    private String botChannel;

    @Getter
    @Value("${telegram.bot.token_file}")
    private String botTokenFile;

    @Getter
    @Value("${telegram.bot.master_name}")
    private String masterName;

    @Getter
    @Value("${telegram.bot.proxy.host}")
    private String proxyHost;
    @Getter
    @Value("${telegram.bot.proxy.port}")
    private Integer proxyPort;
    @Getter
    @Value("${telegram.bot.proxy.username}")
    private String proxyUsername;
    @Getter
    @Value("${telegram.bot.proxy.password}")
    private String proxyPassword;
    @Getter
    @Value("${telegram.bot.proxy.type}")
    private String proxyType;

    @Getter
    private Boolean inited = false;

    @Getter
    private String botToken = "";

    public boolean isActive() {
        return inited && enabled;
    }

    public boolean isProxySet() {
        return proxyHost != null && !proxyHost.isEmpty();
    }

    public boolean isProxyAuthorized() {
        return proxyUsername != null && !proxyUsername.isEmpty();
    }

    @PostConstruct
    public void init() {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader
                     = new BufferedReader(new InputStreamReader(
                             (new ClassPathResource(botTokenFile)).getInputStream()
                     ))
        ) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            botToken = sb.toString();
            inited = botToken.length() > 0;
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

}
