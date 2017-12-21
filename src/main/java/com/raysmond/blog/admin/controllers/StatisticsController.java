package com.raysmond.blog.admin.controllers;

import com.raysmond.blog.models.dto.VisitsStatsChartDTO;
import com.raysmond.blog.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by bvn13 on 20.12.2017.
 */
@Controller
@RequestMapping(value = "/admin/stats")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;


    @RequestMapping(value = "/visits", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody VisitsStatsChartDTO getVisitsChart() {

        VisitsStatsChartDTO chart = statisticsService.getVisitsStatsChartData();
        return chart;

    }

}
