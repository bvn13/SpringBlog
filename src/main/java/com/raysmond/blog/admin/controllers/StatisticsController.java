package com.raysmond.blog.admin.controllers;

import com.raysmond.blog.models.dto.PostsIdListDTO;
import com.raysmond.blog.models.dto.VisitsStatsChartDTO;
import com.raysmond.blog.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by bvn13 on 20.12.2017.
 */
@Controller
@RequestMapping(value = "/admin/stats")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;


    @GetMapping(value = "/visits", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody VisitsStatsChartDTO getVisitsChart() {
        VisitsStatsChartDTO chart = statisticsService.getFullVisitsStatsChartData();
        return chart;
    }

    @PostMapping(value = "/visits", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody VisitsStatsChartDTO getVisitsChartByPeriodAndPostsIdList(
            @RequestParam @DateTimeFormat(pattern="dd-MM-yyyy") Date start,
            @RequestParam @DateTimeFormat(pattern="dd-MM-yyyy") Date end,
            @RequestBody PostsIdListDTO postsDto)
    {
        VisitsStatsChartDTO chart = statisticsService.getChartDataByPeriodAndPostsList(start, end, postsDto.getIds());
        return chart;
    }

}
