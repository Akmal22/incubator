package com.example.incubator.ui.view;

import com.example.incubator.back.service.CountriesService;
import com.example.incubator.back.service.ReportService;
import com.example.incubator.back.service.dto.incubator.IncubatorDto;
import com.example.incubator.back.service.dto.incubator.IncubatorProjectDto;
import com.example.incubator.ui.MainLayout;
import com.example.incubator.ui.view.dto.ShortReport;
import com.example.incubator.ui.view.util.ReportPdfWriter;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.example.incubator.ui.view.util.ReportUtil.getIncomeChart;
import static com.example.incubator.ui.view.util.ReportUtil.getResidentsChart;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RolesAllowed({"ADMIN", "USER", "BI_MANAGER"})
@Route(value = "", layout = MainLayout.class)
@PageTitle("Report")
public class ReportView extends VerticalLayout {
    private static final String SHORT_REPORT_FILENAME = "shortReport.pdf";
    private final ReportService reportService;
    private final CountriesService countriesService;

    private final ComboBox<String> country = new ComboBox<>("Country");
    private final ComboBox<String> incubator = new ComboBox<>("Incubator");
    private final ComboBox<String> incubatorProject = new ComboBox<>("Incubator project");
    private final VerticalLayout reportLayout = new VerticalLayout();
    private final Button downloadFileWrapper = new Button("Download report");

    private ShortReport shortReport;

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

        searchBar.add(country, incubator, incubatorProject, searchButton, getDownloadReportButtonWrapper());

        return searchBar;
    }

    private void report(ClickEvent<Button> clickEvent) {
        reportLayout.removeAll();
        prepareShortReportData();

        VerticalLayout basicInfoLayout = new VerticalLayout();
        Span founded = new Span("Founded: " + shortReport.getFounded());
        Span founder = new Span("Founder: " + shortReport.getFounder());
        basicInfoLayout.add(founded, founder);

        if (isNotBlank(shortReport.getProjectName())) {
            basicInfoLayout.add(new Span("Project name: " + shortReport.getProjectName()));
        }

        HorizontalLayout chartsLayout = new HorizontalLayout();
        chartsLayout.add(shortReport.getIncomeChart(), shortReport.getApplicationChart());
        chartsLayout.setWidth("100%");

        reportLayout.add(basicInfoLayout, chartsLayout);
        add(reportLayout);
        downloadFileWrapper.setEnabled(true);
    }

    private FileDownloadWrapper getDownloadReportButtonWrapper() {
        downloadFileWrapper.setEnabled(false);
        StreamResource streamResource = new StreamResource(SHORT_REPORT_FILENAME, () -> {
            try {
                return new FileInputStream(ReportPdfWriter.exportShortReport(shortReport, SHORT_REPORT_FILENAME));
            } catch (FileNotFoundException exc) {
                throw new RuntimeException(exc);
            }
        });
        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(streamResource);
        buttonWrapper.wrapComponent(downloadFileWrapper);
        return buttonWrapper;
    }

    private void prepareShortReportData() {
        String incubatorName = incubator.getValue();
        String incubatorProjectName = incubatorProject.getValue();
        IncubatorDto incubatorDto = reportService.getIncubator(incubatorName);

        String projectName = null;
        Double incomeAmount;
        Double expenseAmount;
        long applications;
        long accepted;
        long graduated;

        if (isNotBlank(incubatorProjectName)) {
            IncubatorProjectDto incubatorProjectDto = reportService.getIncubatorProject(incubatorProjectName);
            projectName = incubatorProjectDto.getName();
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

        shortReport = new ShortReport(getIncomeChart(incomeAmount, expenseAmount), getResidentsChart(applications, accepted, graduated),
                incubatorDto.getFounded().toString(), incubatorDto.getFounder(), projectName);
    }
}
