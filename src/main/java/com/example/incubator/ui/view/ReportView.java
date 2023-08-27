package com.example.incubator.ui.view;

import com.example.incubator.back.service.CountriesService;
import com.example.incubator.back.service.ReportService;
import com.example.incubator.back.service.dto.incubator.IncubatorDto;
import com.example.incubator.back.service.dto.incubator.IncubatorProjectDto;
import com.example.incubator.ui.MainLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
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
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RolesAllowed({"ADMIN", "USER", "BI_MANAGER"})
@Route(value = "", layout = MainLayout.class)
@PageTitle("Report")
public class ReportView extends VerticalLayout {
    private final ReportService reportService;
    private final CountriesService countriesService;

    private final ComboBox<String> country = new ComboBox<>("Country");
    private final ComboBox<String> incubator = new ComboBox<>("Incubator");
    private final ComboBox<String> incubatorProject = new ComboBox<>("Incubator project");
    private final VerticalLayout reportLayout = new VerticalLayout();

    public ReportView(ReportService reportService, CountriesService countriesService) {
        this.reportService = reportService;
        this.countriesService = countriesService;
        add(getSearchToolBar());
    }

    private HorizontalLayout getSearchToolBar() {
        HorizontalLayout searchBar = new HorizontalLayout();
        searchBar.setDefaultVerticalComponentAlignment(Alignment.END);

        country.setItems(countriesService.getCountryNames());
        country.addValueChangeListener(v -> incubator.setItems(reportService.getIncubatorNamesByCountry(v.getValue())));
        incubator.addValueChangeListener(v -> incubatorProject.setItems(reportService.getIncubatorProjectsByIncubator(v.getValue())));

        var searchButton = new Button("Report");
        searchButton.addClickListener(this::report);
        searchBar.add(country, incubator, incubatorProject, searchButton);

        return searchBar;
    }

    private void report(ClickEvent clickEvent) {
        reportLayout.removeAll();
        String incubatorName = incubator.getValue();
        String incubatorProjectName = incubatorProject.getValue();
        IncubatorDto incubatorDto = reportService.getIncubator(incubatorName);

        VerticalLayout basicInfoLayout = new VerticalLayout();
        Span founded = new Span("Founded: " + incubatorDto.getFounded());
        Span founder = new Span("Founder: " + incubatorDto.getFounder());
        basicInfoLayout.add(founded, founder);

        Double incomeAmount;
        Double expenseAmount;
        long applications;
        long accepted;
        long graduated;

        if (isNotBlank(incubatorProjectName)) {
            IncubatorProjectDto incubatorProjectDto = reportService.getIncubatorProject(incubatorProjectName);
            basicInfoLayout.add(new Span("Project name: " + incubatorProjectDto.getName()));
            incomeAmount = incubatorProjectDto.getIncome();
            expenseAmount = incubatorProjectDto.getExpenses();
            applications = incubatorProjectDto.getResidentApplications();
            accepted = incubatorProjectDto.getAcceptedResidents();
            graduated = incubatorProjectDto.getGraduatedResidents();
        } else {
            incomeAmount = incubatorDto.getIncubatorProjects().stream().mapToDouble(IncubatorProjectDto::getIncome).sum();
            expenseAmount = incubatorDto.getIncubatorProjects().stream().mapToDouble(IncubatorProjectDto::getExpenses).sum();
            applications = incubatorDto.getIncubatorProjects().stream().mapToLong(IncubatorProjectDto::getResidentApplications).sum();
            accepted = incubatorDto.getIncubatorProjects().stream().mapToLong(IncubatorProjectDto::getAcceptedResidents).sum();
            graduated = incubatorDto.getIncubatorProjects().stream().mapToLong(IncubatorProjectDto::getGraduatedResidents).sum();
        }

        HorizontalLayout chartsLayout = new HorizontalLayout();
        chartsLayout.add(getIncomeChart(incomeAmount, expenseAmount), getResidentsChart(applications, accepted, graduated));
        chartsLayout.setWidth("100%");

        reportLayout.add(basicInfoLayout, chartsLayout);
        add(reportLayout);
    }

    private Chart getResidentsChart(long applications, long acceptedResidents, long graduatedResidents) {
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

    private Chart getIncomeChart(Double incomeAmount, Double expenseAmount) {
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
