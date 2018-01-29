package com.raysmond.blog.admin.controllers;

import com.raysmond.blog.forms.PostForm;
import com.raysmond.blog.forms.PostPreviewForm;
import com.raysmond.blog.models.Post;
import com.raysmond.blog.models.Tag;
import com.raysmond.blog.models.User;
import com.raysmond.blog.models.dto.PostPreviewDTO;
import com.raysmond.blog.models.support.*;
import com.raysmond.blog.repositories.PostRepository;
import com.raysmond.blog.repositories.UserRepository;
import com.raysmond.blog.services.AppSetting;
import com.raysmond.blog.services.PostService;
import com.raysmond.blog.services.TagService;
import com.raysmond.blog.support.web.FlexmarkMarkdownService;
import com.raysmond.blog.support.web.MarkdownService;
import com.raysmond.blog.utils.DTOUtil;
import com.raysmond.blog.utils.PaginatorUtil;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author Raysmond<i@raysmond.com>
 */
@Controller("adminPostController")
@RequestMapping("admin/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarkdownService markdownService;

    private static final int PAGE_SIZE = 20;

    @RequestMapping(value = "")
    public String index(@RequestParam(defaultValue = "0") int page, Model model){
        Page<Post> posts = postRepository.findAll(new PageRequest(page, PAGE_SIZE, Sort.Direction.DESC, "id"));

        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("posts", posts);
        model.addAttribute("pagesList", PaginatorUtil.createPagesList(0, posts.getTotalPages()-1));

        return "admin/posts/index";
    }

    private String makeFormPostCreation(Model model) {
        PostForm postForm = DTOUtil.map(new Post(), PostForm.class);
        return this.makeFormPostCreation(model, postForm);
    }

    private String makeFormPostCreation(Model model, PostForm postForm) {

        postForm.init();

        model.addAttribute("postForm", postForm);
        model.addAttribute("postFormats", PostFormat.values());
        model.addAttribute("postStatus", PostStatus.values());
        model.addAttribute("seoOgLocales", OgLocale.values());
        model.addAttribute("seoOgTypes", OgType.values());

        return "admin/posts/new";
    }

    @RequestMapping(value = "new")
    public String newPost(Model model){
        return this.makeFormPostCreation(model);
    }

    private String makeFormPostEdition(Long postId, Model model) {
        return this.makeFormPostEdition(postId, model, null);
    }

    private String makeFormPostEdition(Long postId, Model model, PostForm postForm) {
        Post post = postRepository.findOne(postId);

        if (postForm == null) {
            postForm = DTOUtil.map(post, PostForm.class);
        }

        postForm.init();
        DTOUtil.mapTo(post, postForm);
        postForm.initFromPost(post, postService.getTagNames(post.getTags()));

        model.addAttribute("post", post);
        model.addAttribute("postForm", postForm);
        model.addAttribute("postFormats", PostFormat.values());
        model.addAttribute("postStatus", PostStatus.values());
        model.addAttribute("seoOgLocales", OgLocale.values());
        model.addAttribute("seoOgTypes", OgType.values());

        return "admin/posts/edit";
    }

    @RequestMapping(value = "{postId:[0-9]+}/edit")
    public String editPost(@PathVariable Long postId, Model model){
        return this.makeFormPostEdition(postId, model);
    }

    @RequestMapping(value = "{postId:[0-9]+}/delete", method = {DELETE, POST})
    public String deletePost(@PathVariable Long postId){
        postService.deletePost(postRepository.findOne(postId));
        return "redirect:/admin/posts";
    }

    @RequestMapping(value = "", method = POST)
    public String create(Principal principal, @Valid PostForm postForm, Errors errors, Model model){
        if (errors.hasErrors()) {
            Map<String, WebError> webErrors = new HashMap<>();
            errors.getAllErrors().forEach(e -> {
                String field = ((FieldError)e).getField();
                webErrors.put(field, new WebError(field, e.getDefaultMessage()));
            });
            model.addAttribute("errors", webErrors);
            return this.makeFormPostCreation(model, postForm);
        } else {
            Post post = DTOUtil.map(postForm, Post.class);
            post.setUser(userRepository.findByEmail(principal.getName()));
            post.setTags(postService.parseTagNames(postForm.getPostTags()));
            postForm.fillOgFieldsInPost(post);

            postService.createPost(post);

            return "redirect:/admin/posts";
        }
    }

    @RequestMapping(value = "{postId:[0-9]+}", method = {PUT, POST})
    public String update(@PathVariable Long postId, @Valid PostForm postForm, Errors errors, Model model){
        if (errors.hasErrors()){
            Map<String, WebError> webErrors = new HashMap<>();
            errors.getAllErrors().forEach(e -> {
                String field = ((FieldError)e).getField();
                webErrors.put(field, new WebError(field, e.getDefaultMessage()));
            });
            model.addAttribute("errors", webErrors);
            return this.makeFormPostEdition(postId, model, postForm);
        } else {
            Post post = postRepository.findOne(postId);
            DTOUtil.mapTo(postForm, post);
            post.setTags(postService.parseTagNames(postForm.getPostTags()));
            postForm.fillOgFieldsInPost(post);

            postService.updatePost(post);

            return "redirect:/admin/posts";
        }
    }

    @RequestMapping(value = "/preview", method = {PUT, POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    PostPreviewDTO preview(@RequestBody @Valid PostPreviewForm postPreviewForm, Errors errors, Model model) throws Exception {

        if (errors.hasErrors()) {
            throw new Exception("Error occurred!");
        }

        return new PostPreviewDTO(markdownService.renderToHtml(postPreviewForm.getContent()));
    }


}
