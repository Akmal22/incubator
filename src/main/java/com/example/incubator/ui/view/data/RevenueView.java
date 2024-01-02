package com.example.incubator.ui.view.data;

import com.example.incubator.backend.service.IncubatorProjectService;
import com.example.incubator.backend.service.RevenueService;
import com.example.incubator.backend.service.dto.RevenueDto;
import com.example.incubator.backend.service.dto.ServiceResult;
import com.example.incubator.ui.MainLayout;
import com.example.incubator.ui.form.RevenueForm;
import com.example.incubator.ui.form.dto.EditRevenueDto;
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
import org.springframework.security.core.userdetails.UserDetails;

import java.util.stream.Collectors;

@RolesAllowed({"ADMIN", "BI_MANAGER"})
@Route(value = "data/revenue", layout = MainLayout.class)
@PageTitle("Revenue")
public class RevenueView extends VerticalLayout {
    private final RevenueService revenueService;
    private final IncubatorProjectService incubatorProjectService;

    private RevenueForm revenueForm;
    private Grid<EditRevenueDto> revenueDtoGrid;
    private TextField filterText;

    public RevenueView(AuthenticationContext authenticationContext,
                       RevenueService revenueService,
                       IncubatorProjectService incubatorProjectService) {
        this.revenueService = revenueService;
        this.incubatorProjectService = incubatorProjectService;

        UserDetails userDetails = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .orElseThrow(() -> new RuntimeException("Error"));

        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureRevenueForm(userDetails);

        add(getToolBar(), getContent());

        closeEditor();
        updateClientsList();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(revenueDtoGrid, revenueForm);
        content.setFlexGrow(2, revenueDtoGrid);
        content.setFlexGrow(1, revenueForm);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private HorizontalLayout getToolBar() {
        filterText = new TextField();
        filterText.setPlaceholder("Filter revenue info by incubator project name ...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateClientsList());

        var addButton = new Button("Add revenue info");
        addButton.addClickListener(e -> addRevenueInfo());

        var toolbar = new HorizontalLayout(filterText, addButton);
        toolbar.addClassNames("toolbar");
        return toolbar;
    }

    private void addRevenueInfo() {
        revenueDtoGrid.asSingleSelect().clear();
        editRevenueDto(new EditRevenueDto());
    }

    private void configureRevenueForm(UserDetails userDetails) {
        revenueForm = new RevenueForm(userDetails, incubatorProjectService);

        revenueForm.setWidth("25em");
        revenueForm.addSaveListener(this::saveRevenue);
        revenueForm.addDeleteListener(this::deleteClientsDto);
        revenueForm.addCloseEditorListener(e -> closeEditor());
    }

    private void saveRevenue(RevenueForm.SaveEvent event) {
        EditRevenueDto editRevenueDto = event.getEditRevenueDto();
        ServiceResult serviceResult;
        if (editRevenueDto.getId() == null) {
            serviceResult = revenueService.createRevenue(convertEditRevenueDto(editRevenueDto));
        } else {
            serviceResult = revenueService.updateRevenue(convertEditRevenueDto(editRevenueDto));
        }

        if (!serviceResult.isSuccess()) {
            revenueForm.getErrorMessageLabel().setEnabled(!serviceResult.isSuccess());
            revenueForm.getErrorMessageLabel().setText(serviceResult.getErrorMessage());
        } else {
            closeEditor();
            updateClientsList();
        }
    }

    private void deleteClientsDto(RevenueForm.DeleteEvent event) {
        revenueService.deleteRevenue(convertEditRevenueDto(event.getEditRevenueDto()));
        updateClientsList();
        closeEditor();
    }

    private void updateClientsList() {
        revenueDtoGrid.setItems(revenueService.getAllRevenueDtoByFilterText(filterText.getValue()).stream()
                .map(this::convertRevenueDto)
                .collect(Collectors.toList()));
    }

    private void configureGrid() {
        revenueDtoGrid = new Grid<>(EditRevenueDto.class);

        revenueDtoGrid.addClassName("contact-grid");
        revenueDtoGrid.setSizeFull();
        revenueDtoGrid.setColumns("leaseRevenue", "serviceRevenue", "sponsorshipRevenue", "grantRevenue");
        revenueDtoGrid.addColumn(editRevenueDto -> editRevenueDto.getProject().getName()).setHeader("Incubator Project");

        revenueDtoGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        revenueDtoGrid.asSingleSelect().addValueChangeListener(event -> editRevenueDto(event.getValue()));
    }

    private void editRevenueDto(EditRevenueDto editRevenueDto) {
        if (editRevenueDto == null) {
            closeEditor();
        } else {
            revenueForm.setEditRevenueDto(editRevenueDto);
            revenueForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        revenueForm.setEditRevenueDto(null);
        revenueForm.setVisible(false);
        revenueForm.getErrorMessageLabel().setText(null);
        revenueForm.getErrorMessageLabel().setEnabled(false);
        removeClassName("editing");
    }

    private EditRevenueDto convertRevenueDto(RevenueDto revenueDto) {
        EditRevenueDto editRevenueDto = new EditRevenueDto();
        editRevenueDto.setId(revenueDto.getId());
        editRevenueDto.setProject(revenueDto.getIncubatorProjectDto());
        editRevenueDto.setLeaseRevenue(revenueDto.getLeaseRevenue());
        editRevenueDto.setServiceRevenue(revenueDto.getServiceRevenue());
        editRevenueDto.setSponsorshipRevenue(revenueDto.getSponsorshipRevenue());
        editRevenueDto.setGrantRevenue(revenueDto.getGrantRevenue());

        return editRevenueDto;
    }

    private RevenueDto convertEditRevenueDto(EditRevenueDto editRevenueDto) {
        return new RevenueDto()
                .setId(editRevenueDto.getId())
                .setIncubatorProjectDto(editRevenueDto.getProject())
                .setLeaseRevenue(editRevenueDto.getLeaseRevenue())
                .setServiceRevenue(editRevenueDto.getServiceRevenue())
                .setSponsorshipRevenue(editRevenueDto.getSponsorshipRevenue())
                .setGrantRevenue(editRevenueDto.getGrantRevenue());
    }
}
