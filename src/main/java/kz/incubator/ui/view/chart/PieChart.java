package kz.incubator.ui.view.chart;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Cursor;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.PlotOptionsPie;
import com.vaadin.flow.component.charts.model.Tooltip;

import java.util.Map;

public class PieChart implements BiChart {
    private final String title;
    private final Map<String, Number> chartData;
    private final Number valueDecimal;

    public PieChart(String title, Map<String, Number> chartData, Number valueDecimals) {
        this.title = title;
        this.chartData = chartData;
        this.valueDecimal = valueDecimals;
    }

    @Override
    public Chart getChart() {
        Chart chart = new Chart(ChartType.PIE);
        Configuration configuration = chart.getConfiguration();
        configuration.setTitle(title);

        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(valueDecimal);
        configuration.setTooltip(tooltip);

        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
        plotOptionsPie.setAllowPointSelect(true);
        plotOptionsPie.setCursor(Cursor.POINTER);
        plotOptionsPie.setShowInLegend(true);
        configuration.setPlotOptions(plotOptionsPie);

        DataSeries dataSeries = new DataSeries();
        chartData.forEach((k, v) -> {
            dataSeries.add(new DataSeriesItem(k, v));
        });

        configuration.setSeries(dataSeries);
        chart.setVisibilityTogglingDisabled(true);

        return chart;
    }
}
