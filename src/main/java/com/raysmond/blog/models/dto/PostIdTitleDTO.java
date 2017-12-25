package com.raysmond.blog.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by bvn13 on 25.12.2017.
 */
@Data
@AllArgsConstructor
public class PostIdTitleDTO {
    private Long id;
    private String title;
}
