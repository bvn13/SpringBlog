package com.raysmond.blog.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;

/**
 * Created by bvn13 on 21.12.2017.
 */
@Component
public class TelegramBotSettings {

    @Value("${telegram.bot.enabled}")
    public Boolean enabled;

    @Getter
    @Value("${telegram.bot.name}")
    public String botName;

    @Getter
    @Value("${telegram.bot.channel}")
    public String botChannel;

    @Getter
    @Value("${telegram.bot.token_file}")
    public String botTokenFile;

    @Getter
    @Value("${telegram.bot.master_id}")
    public String masterName;


    @Getter
    private Boolean inited = false;

    @Getter
    private String botToken = "";

    public Boolean isActive() {
        return inited && enabled;
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
            e.printStackTrace();
        }
    }

}
