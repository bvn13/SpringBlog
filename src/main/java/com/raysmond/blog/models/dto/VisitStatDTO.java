package com.raysmond.blog.models.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bvn13 on 20.12.2017.
 */
@Data
public class VisitStatDTO implements Serializable {

    private Date dt;
    private Long post_id;
    private String title;
    private Long count;

}
