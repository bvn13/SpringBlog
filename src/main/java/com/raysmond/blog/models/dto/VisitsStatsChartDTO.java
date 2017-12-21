package com.raysmond.blog.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bvn13 on 20.12.2017.
 */
@Data
public class VisitsStatsChartDTO implements Serializable{

    @Data
    @RequiredArgsConstructor
    public static class Type {
        private String type = "";
    }

    @Data
    @RequiredArgsConstructor
    public static class Text {
        private String text = "";
    }

    public static class XAxis {
        @Getter
        private List<String> categories = new ArrayList<>();
    }

    public static class YAxis {
        @Getter
        private Text title = new Text();
    }

    public static class PlotOptions {
        @Getter
        private Line line = new Line();
    }

    public static class Line {
        @Getter
        private DataLabels dataLabels = new DataLabels();

        @Getter
        @Setter
        private Boolean enableMouseTracking = false;
    }

    @Data
    @RequiredArgsConstructor
    public static class DataLabels {
        private Boolean enabled = false;
    }

    @Data
    public static class Series {
        private String name;
        private List<Long> data = new ArrayList<>();
    }

    private Type chart = new Type();
    private Text title = new Text();
    private Text subtitle = new Text();
    @JsonProperty("xAxis")
    private XAxis xAxis = new XAxis();
    @JsonProperty("yAxis")
    private YAxis yAxis = new YAxis();
    private Line plotOptions = new Line();

    private List<Series> series = new ArrayList<>();
}
