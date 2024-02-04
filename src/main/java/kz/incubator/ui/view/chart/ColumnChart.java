package kz.incubator.ui.view.chart;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Crosshair;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.Tooltip;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;

import java.util.Map;

public class ColumnChart implements BiChart {
    private final String title;
    private final String subTitle;
    private final String yaxisTitle;
    private final Map<String, Number> chartData;

    public ColumnChart(String title, String subTitle, String yaxisTitle,
                       Map<String, Number> chartData) {
        this.title = title;
        this.subTitle = subTitle;
        this.yaxisTitle = yaxisTitle;
//        chartData.forEach((k, v) -> Assert.isTrue(v.size() == 12, "values size must be equal to 12"));
        this.chartData = chartData;
    }

    @Override
    public Chart getChart() {
        Chart chart = new Chart(ChartType.COLUMN);

        Configuration configuration = chart.getConfiguration();
        configuration.setTitle(title);
        configuration.setSubTitle(subTitle);

        chartData.forEach((k, v) -> configuration.addSeries(new ListSeries(k, v)));

        XAxis x = new XAxis();
        x.setCrosshair(new Crosshair());
        configuration.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle(yaxisTitle);
        configuration.addyAxis(y);

        Tooltip tooltip = new Tooltip();
        tooltip.setShared(true);
        configuration.setTooltip(tooltip);

        return chart;
    }
}
