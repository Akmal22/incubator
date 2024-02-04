package kz.incubator.ui.view;

import kz.incubator.backend.service.CountriesService;
import kz.incubator.backend.service.ReportService;
import kz.incubator.backend.service.dto.ClientsDto;
import kz.incubator.backend.service.dto.incubator.IncubatorDto;
import kz.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import kz.incubator.ui.MainLayout;
import kz.incubator.ui.view.chart.ColumnChart;
import kz.incubator.ui.view.chart.PieChart;
import kz.incubator.ui.view.dto.Report;
import kz.incubator.ui.view.dto.ShortReport;
import kz.incubator.ui.view.util.ReportPdfWriter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        Report report = getReportData();

        VerticalLayout basicInfoLayout = new VerticalLayout();
        if (isNotBlank(report.getFounded())) {
            basicInfoLayout.add(new Span("Founded: " + report.getFounded()));
        }
        if (isNotBlank(report.getFounder())) {
            basicInfoLayout.add(new Span("Founder: " + report.getFounder()));
        }

        if (isNotBlank(report.getProjectName())) {
            basicInfoLayout.add(new Span("Project name: " + report.getProjectName()));
        }

        HorizontalLayout chartsLayout = new HorizontalLayout();
        chartsLayout.add(report.getClientsCharts());
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

    private Report getReportData() {
        String countryName = country.getValue();
        String incubatorName = incubator.getValue();
        String incubatorProjectName = incubatorProject.getValue();

        String founder = null;
        String founded = null;

        int overallApplications;
        int overallAccepted;
        int overallGraduated;
        int overallFailed;


        if (isNotBlank(incubatorProjectName)) {
            IncubatorProjectDto incubatorProjectDto = reportService.getIncubatorProject(incubatorProjectName);
            IncubatorDto incubatorDto = incubatorProjectDto.getIncubatorDto();
            founded = incubatorDto.getFounded().toString();
            founder = incubatorDto.getFounder();
            overallApplications = incubatorProjectDto.getClientsDto().getApplications();
            overallAccepted = incubatorProjectDto.getClientsDto().getAccepted();
            overallFailed = incubatorProjectDto.getClientsDto().getFailed();
            overallGraduated = incubatorProjectDto.getClientsDto().getGraduated();
        } else if (isNotBlank(incubatorName)) {
            IncubatorDto incubatorDto = reportService.getIncubator(incubatorName);
            founded = incubatorDto.getFounded().toString();
            founder = incubatorDto.getFounder();
            overallApplications = incubatorDto.getIncubatorProjects().stream()
                    .map(IncubatorProjectDto::getClientsDto)
                    .mapToInt(ClientsDto::getApplications)
                    .sum();
            overallAccepted = incubatorDto.getIncubatorProjects().stream()
                    .map(IncubatorProjectDto::getClientsDto)
                    .mapToInt(ClientsDto::getAccepted)
                    .sum();
            overallFailed = incubatorDto.getIncubatorProjects().stream()
                    .map(IncubatorProjectDto::getClientsDto)
                    .mapToInt(ClientsDto::getFailed)
                    .sum();
            overallGraduated = incubatorDto.getIncubatorProjects().stream()
                    .map(IncubatorProjectDto::getClientsDto)
                    .mapToInt(ClientsDto::getGraduated)
                    .sum();
        } else {
            List<IncubatorDto> incubators = reportService.getIncubatorsByCountry(countryName);
            List<IncubatorProjectDto> projects = incubators.stream()
                    .flatMap(i -> i.getIncubatorProjects().stream()).toList();

            overallApplications = projects.stream()
                    .map(IncubatorProjectDto::getClientsDto)
                    .mapToInt(ClientsDto::getApplications)
                    .sum();
            overallAccepted = projects.stream()
                    .map(IncubatorProjectDto::getClientsDto)
                    .mapToInt(ClientsDto::getAccepted)
                    .sum();
            overallFailed = projects.stream()
                    .map(IncubatorProjectDto::getClientsDto)
                    .mapToInt(ClientsDto::getFailed)
                    .sum();
            overallGraduated = projects.stream()
                    .map(IncubatorProjectDto::getClientsDto)
                    .mapToInt(ClientsDto::getGraduated)
                    .sum();
        }


        Map<String, Number> clientsChartData = new HashMap<>();
        clientsChartData.put("Applications", overallApplications);
        clientsChartData.put("Accepted", overallAccepted);
        clientsChartData.put("Graduated", overallGraduated);
        clientsChartData.put("Failed", overallFailed);
        PieChart clientsPiChart = new PieChart("Percentage of incubator participants", clientsChartData, 2);
        ColumnChart clientsColumnChart = new ColumnChart("Incubator participants information", null, "Number of participants ", clientsChartData);

        return Report.builder()
                .founder(founder)
                .founded(founded)
                .projectName(incubatorProjectName)
                .clientsCharts(Set.of(clientsPiChart.getChart(), clientsColumnChart.getChart()))
                .build();
    }
}
