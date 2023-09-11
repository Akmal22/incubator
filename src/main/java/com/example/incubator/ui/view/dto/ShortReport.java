package com.example.incubator.ui.view.dto;

import com.vaadin.flow.component.charts.Chart;
import lombok.Getter;

@Getter
public class ShortReport {
    private final Chart incomeChart;
    private final Chart applicationChart;
    private final String founded;
    private final String founder;
    private final String projectName;

    public ShortReport(Chart incomeChart, Chart applicationsChart,
                       String founded, String founder, String projectName) {
        this.incomeChart = incomeChart;
        this.applicationChart = applicationsChart;
        this.founded = founded;
        this.founder = founder;
        this.projectName = projectName;
    }
}
