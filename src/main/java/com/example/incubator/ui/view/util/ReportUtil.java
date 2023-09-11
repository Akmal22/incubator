package com.example.incubator.ui.view.util;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.AxisType;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Cursor;
import com.vaadin.flow.component.charts.model.DataLabels;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.PlotOptionsColumn;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportUtil {
    public static Chart getResidentsChart(long applications, long acceptedResidents, long graduatedResidents) {
        Chart residentsChart = new Chart(ChartType.COLUMN);
        Configuration configuration = residentsChart.getConfiguration();
        configuration.getLegend().setEnabled(false);

        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setCursor(Cursor.POINTER);
        plotOptionsColumn.setColorByPoint(true);
        plotOptionsColumn.setDataLabels(new DataLabels(true));
        configuration.setPlotOptions(plotOptionsColumn);

        XAxis xAxis = configuration.getxAxis();
        xAxis.setType(AxisType.CATEGORY);

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle("Residents number");

        DataSeries dataSeries = new DataSeries("Expenses");
        dataSeries.add(new DataSeriesItem("Applications", applications));
        dataSeries.add(new DataSeriesItem("Accepted residents", acceptedResidents));
        dataSeries.add(new DataSeriesItem("Graduated residents", graduatedResidents));

        residentsChart.getConfiguration().setSeries(dataSeries);

        return residentsChart;
    }

    public static Chart getIncomeChart(Double incomeAmount, Double expenseAmount) {
        Chart incomeChart = new Chart(ChartType.COLUMN);
        Configuration configuration = incomeChart.getConfiguration();
        configuration.getLegend().setEnabled(false);

        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setCursor(Cursor.POINTER);
        plotOptionsColumn.setColorByPoint(true);
        plotOptionsColumn.setDataLabels(new DataLabels(true));
        configuration.setPlotOptions(plotOptionsColumn);

        XAxis xAxis = configuration.getxAxis();
        xAxis.setType(AxisType.CATEGORY);

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle("Amount");

        DataSeries dataSeries = new DataSeries("Expenses");
        dataSeries.add(new DataSeriesItem("Income", incomeAmount));
        dataSeries.add(new DataSeriesItem("Expenses", expenseAmount));

        incomeChart.getConfiguration().setSeries(dataSeries);

        return incomeChart;
    }
}
