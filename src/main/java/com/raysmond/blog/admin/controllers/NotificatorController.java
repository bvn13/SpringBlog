package com.raysmond.blog.admin.controllers;

import com.raysmond.blog.models.Post;
import com.raysmond.blog.models.dto.PostAnnouncementDTO;
import com.raysmond.blog.models.support.PostStatus;
import com.raysmond.blog.notificators.Notificator;
import com.raysmond.blog.services.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Created by bvn13 on 22.12.2017.
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/notify")
public class NotificatorController {

    @Autowired
    private Notificator notificator;


    @Autowired
    private PostService postService;



    @PostMapping(value = "/{postId:[0-9]+}/telegram", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody PostAnnouncementDTO sendTelegramAnnounce(@PathVariable Long postId) {

        Post post = postService.getPost(postId);
        if (post.getPostStatus().equals(PostStatus.PUBLISHED)) {
            try {
                notificator.announcePost(post);
                return new PostAnnouncementDTO(false);
            } catch (Exception e) {
                log.error("Error", e);
                return new PostAnnouncementDTO(true, ""+e.getMessage());
            }
        } else {
            return new PostAnnouncementDTO(true, "Post is not published!");
        }
    }

}
