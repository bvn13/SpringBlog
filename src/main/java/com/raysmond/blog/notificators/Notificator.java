package com.raysmond.blog.notificators;

import com.raysmond.blog.models.Post;
import com.raysmond.blog.notificators.telegram.TelegramBotManager;
import com.raysmond.blog.services.AppSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Created by bvn13 on 21.12.2017.
 */
@Component
public class Notificator {

    private static final Logger logger = LoggerFactory.getLogger(Notificator.class);

    @Autowired
    private AppSetting appSetting;

    @Autowired
    private TelegramBotManager telegramBot;


    public void announcePost(Post post) throws Exception {
        if (post == null || post.getAnnouncement().isEmpty()) {
            throw new IllegalArgumentException("Nothing to announce");
        }
        String postUrl = appSetting.getMainUriStripped()+"/posts/"+(post.getPermalink().isEmpty() ? post.getId() : post.getPermalink());
        String message = String.format(
                "*%s*\r\n\n" +
                "[%s](%s)\r\n\r\n"+
                "%s\r\n\r\n" +
                "",
                post.getTitle(),
                postUrl, postUrl,
                post.getAnnouncement() != null ? post.getAnnouncement() : ""
        );
        try {
            telegramBot.sendMessageToChannel(message);
        } catch (Exception e) {
            logger.error(String.format("Could not send message <%s>", message), e);
            throw e;
        }
    }

}
