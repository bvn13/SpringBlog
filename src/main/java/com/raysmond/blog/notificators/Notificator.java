package com.raysmond.blog.notificators;

import com.raysmond.blog.models.Post;
import com.raysmond.blog.notificators.telegram.TelegramBotManager;
import com.raysmond.blog.services.AppSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by bvn13 on 21.12.2017.
 */
@Component
public class Notificator {

    @Autowired
    private AppSetting appSetting;

    @Autowired
    private TelegramBotManager telegramBot;


    public void announcePost(Post post) throws IllegalArgumentException, TelegramApiException {
        if (post == null || post.getAnnouncement().isEmpty()) {
            throw new IllegalArgumentException("Nothing to announce");
        }
        String postUrl = appSetting.getMainUriStripped()+"/posts/"+(post.getPermalink().isEmpty() ? post.getId() : post.getPermalink());
        String message = String.format(
                "*%s*\r\n\n" +
                "%s\r\n\r\n" +
                "[%s](%s)",
                post.getTitle(),
                post.getAnnouncement() != null ? post.getAnnouncement() : "",
                postUrl, postUrl
        );
        telegramBot.sendMessageToChannel(message);
    }

}
