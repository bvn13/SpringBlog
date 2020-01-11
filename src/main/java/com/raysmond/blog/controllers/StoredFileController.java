package com.raysmond.blog.controllers;

import com.raysmond.blog.models.StoredFile;
import com.raysmond.blog.services.FileStorageService;
import com.raysmond.blog.services.UserService;
import com.raysmond.blog.support.web.HttpContentTypeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/files")
public class StoredFileController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService storageService;

    @GetMapping(value = "/{fileName:.+}"/*, produces = APPLICATION_OCTET_STREAM_VALUE*/)
    @ExceptionHandler(value = FileNotFoundException.class)
    public @ResponseBody
    HttpEntity<byte[]> getFileById(@PathVariable String fileName, final HttpServletResponse response) throws IOException {

        StoredFile file = this.storageService.getFileByName(fileName);
        if (file == null) {
            response.sendError(404, String.format("File %s not found", fileName));
            return null;
        }
        byte[] content;
        try {
            content = this.storageService.getFileContent(file);
        } catch (IOException e) {
            log.error("Error", e);
            if (this.userService.isCurrentUserAdmin()) {
                response.sendError(404, String.format("File %s (%s) not found", file.getName(), file.getPath()));
            } else {
                response.sendError(404, String.format("File %s not found", file.getName()));
            }
            return null;
        }

        HttpHeaders header = new HttpHeaders();
        //header.setContentType(APPLICATION_OCTET_STREAM);
        header.set("Content-Type", HttpContentTypeSerializer.getContentType(file.getName()));
        header.set("Content-Disposition", "inline; filename=" + file.getName());
        header.setContentLength(file.getSize());

        return new HttpEntity<byte[]>(content, header);
    }

}
