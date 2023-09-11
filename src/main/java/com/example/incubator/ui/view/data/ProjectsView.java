package com.example.incubator.ui.view.data;

import com.example.incubator.back.entity.user.Role;
import com.example.incubator.back.service.IncubatorProjectService;
import com.example.incubator.back.service.IncubatorService;
import com.example.incubator.back.service.dto.ServiceResult;
import com.example.incubator.back.service.dto.incubator.IncubatorDto;
import com.example.incubator.back.service.dto.incubator.IncubatorProjectDto;
import com.example.incubator.ui.MainLayout;
import com.example.incubator.ui.form.dto.EditProjectDto;
import com.example.incubator.ui.form.ProjectForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

@RolesAllowed({"ADMIN", "BI_MANAGER"})
@Route(value = "data/projects", layout = MainLayout.class)
@PageTitle("Incubators")
public class ProjectsView extends VerticalLayout {
    private final IncubatorProjectService incubatorProjectService;
    private final IncubatorService incubatorService;

    private ProjectForm projectForm;
    private Grid<EditProjectDto> projectsGrid;
    private TextField filterText;

    public ProjectsView(
            IncubatorProjectService incubatorProjectService,
            IncubatorService incubatorService,
            AuthenticationContext authenticationContext) {
        this.incubatorProjectService = incubatorProjectService;
        this.incubatorService = incubatorService;

        UserDetails userDetails = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .orElseThrow(() -> new RuntimeException("Error"));

        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureProjectForm(userDetails);

        add(getToolBar(userDetails), getContent());

        closeEditor();
        updateProjectsList();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(projectsGrid, projectForm);
        content.setFlexGrow(2, projectsGrid);
        content.setFlexGrow(1, projectForm);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private HorizontalLayout getToolBar(UserDetails userDetails) {
        filterText = new TextField();
        filterText.setPlaceholder("Filter incubator project by name or founder ...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateProjectsList());

        var addButton = new Button("Add project");
        addButton.addClickListener(e -> addProject());

        var toolbar = new HorizontalLayout(filterText, addButton);
        toolbar.addClassNames("toolbar");
        return toolbar;
    }

    private void configureProjectForm(UserDetails userDetails) {
        List<IncubatorDto> incubators;
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_BI_MANAGER.name()))) {
            incubators = incubatorService.getManagerIncubators(userDetails.getUsername());
        } else {
            incubators = incubatorService.getAllIncubators();
        }
        projectForm = new ProjectForm(incubators);

        projectForm.setWidth("25em");
        projectForm.addSaveListener(this::saveProject);
        projectForm.addDeleteListener(this::deleteProject);
        projectForm.addCloseEditorListener(e -> closeEditor());
    }

    private void deleteProject(ProjectForm.DeleteEvent deleteEvent) {
        incubatorProjectService.deleteIncubatorProject(convertEditProjectDto(deleteEvent.getEditProjectDto()));
        updateProjectsList();
        closeEditor();
    }

    private void saveProject(ProjectForm.SaveEvent event) {
        EditProjectDto editProjectDto = event.getEditProjectDto();
        ServiceResult serviceResult;
        if (editProjectDto.getId() == null) {
            serviceResult = incubatorProjectService.createIncubatorProject(convertEditProjectDto(editProjectDto));
        } else {
            serviceResult = incubatorProjectService.updateIncubatorProject(convertEditProjectDto(editProjectDto));
        }

        if (!serviceResult.isSuccess()) {
            projectForm.getErrorMessageLabel().setEnabled(!serviceResult.isSuccess());
            projectForm.getErrorMessageLabel().setText(serviceResult.getErrorMessage());
        } else {
            closeEditor();
            updateProjectsList();
        }
    }

    private void addProject() {
        projectsGrid.asSingleSelect().clear();
        editProject(new EditProjectDto());
    }

    private void updateProjectsList() {
        projectsGrid.setItems(incubatorProjectService.findAllByFilterText(filterText.getValue()).stream()
                .map(this::convertIncubatorProjectDto)
                .collect(Collectors.toList()));
    }

    private void configureGrid() {
        projectsGrid = new Grid<>(EditProjectDto.class);

        projectsGrid.addClassName("contact-grid");
        projectsGrid.setSizeFull();
        projectsGrid.setColumns("name", "income", "expenses", "residentApplications", "acceptedResidentApplications",
                "graduatedResidentsCount", "startDate", "endDate");
        projectsGrid.addColumn(editProjectDto -> editProjectDto.getIncubator().getIncubatorName()).setHeader("Incubator");

        projectsGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        projectsGrid.asSingleSelect().addValueChangeListener(event -> editProject(event.getValue()));
    }

    private void editProject(EditProjectDto editProjectDto) {
        if (editProjectDto == null) {
            closeEditor();
        } else {
            projectForm.setEditProjectDto(editProjectDto);
            projectForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        projectForm.setEditProjectDto(null);
        projectForm.setVisible(false);
        projectForm.getErrorMessageLabel().setText(null);
        projectForm.getErrorMessageLabel().setEnabled(false);
        removeClassName("editing");
    }

    private EditProjectDto convertIncubatorProjectDto(IncubatorProjectDto projectDto) {
        EditProjectDto editProjectDto = new EditProjectDto();
        editProjectDto.setId(projectDto.getId());
        editProjectDto.setName(projectDto.getName());
        editProjectDto.setIncubator(projectDto.getIncubatorDto());
        editProjectDto.setIncome(projectDto.getIncome());
        editProjectDto.setExpenses(projectDto.getExpenses());
        editProjectDto.setResidentApplications(projectDto.getResidentApplications());
        editProjectDto.setAcceptedResidentApplications(projectDto.getAcceptedResidents());
        editProjectDto.setGraduatedResidentsCount(projectDto.getGraduatedResidents());
        editProjectDto.setStartDate(projectDto.getStartDate());
        editProjectDto.setEndDate(projectDto.getEndDate());

        return editProjectDto;
    }

    private IncubatorProjectDto convertEditProjectDto(EditProjectDto editProjectDto) {
        return new IncubatorProjectDto()
                .setId(editProjectDto.getId())
                .setName(editProjectDto.getName())
                .setIncubatorDto(editProjectDto.getIncubator())
                .setIncome(editProjectDto.getIncome())
                .setExpenses(editProjectDto.getExpenses())
                .setResidentApplications(editProjectDto.getResidentApplications())
                .setAcceptedResidents(editProjectDto.getAcceptedResidentApplications())
                .setGraduatedResidents(editProjectDto.getGraduatedResidentsCount())
                .setStartDate(editProjectDto.getStartDate())
                .setEndDate(editProjectDto.getEndDate());
    }
}
