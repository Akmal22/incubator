package com.example.incubator.ui.view.data;

import com.example.incubator.backend.service.ConsumedResourcesService;
import com.example.incubator.backend.service.IncubatorProjectService;
import com.example.incubator.backend.service.dto.ConsumedResourcesDto;
import com.example.incubator.backend.service.dto.ServiceResult;
import com.example.incubator.ui.MainLayout;
import com.example.incubator.ui.form.ConsumedResourcesForm;
import com.example.incubator.ui.form.dto.EditConsumedResourcesDto;
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
@Route(value = "data/consumedResources", layout = MainLayout.class)
@PageTitle("Consumed Resources")
public class ConsumedResourcesView extends VerticalLayout {
    private final ConsumedResourcesService consumedResourcesService;
    private final IncubatorProjectService incubatorProjectService;

    private ConsumedResourcesForm consumedResourcesForm;
    private Grid<EditConsumedResourcesDto> editConsumedResourcesDtoGrid;
    private TextField filterText;

    public ConsumedResourcesView(AuthenticationContext authenticationContext,
                                 ConsumedResourcesService consumedResourcesService,
                                 IncubatorProjectService incubatorProjectService) {
        this.consumedResourcesService = consumedResourcesService;
        this.incubatorProjectService = incubatorProjectService;

        UserDetails userDetails = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .orElseThrow(() -> new RuntimeException("Error"));

        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureConsumedResourcesForm(userDetails);

        add(getToolBar(), getContent());

        closeEditor();
        updateConsumedResourcesList();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(editConsumedResourcesDtoGrid, consumedResourcesForm);
        content.setFlexGrow(2, editConsumedResourcesDtoGrid);
        content.setFlexGrow(1, consumedResourcesForm);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private HorizontalLayout getToolBar() {
        filterText = new TextField();
        filterText.setPlaceholder("Filter consumed resources info by incubator project name ...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateConsumedResourcesList());

        var addButton = new Button("Add consumed resrouces info");
        addButton.addClickListener(e -> addCosnumedResources());

        var toolbar = new HorizontalLayout(filterText, addButton);
        toolbar.addClassNames("toolbar");
        return toolbar;
    }

    private void addCosnumedResources() {
        editConsumedResourcesDtoGrid.asSingleSelect().clear();
        editConsumedResources(new EditConsumedResourcesDto());
    }

    private void configureConsumedResourcesForm(UserDetails userDetails) {
        consumedResourcesForm = new ConsumedResourcesForm(userDetails, incubatorProjectService);

        consumedResourcesForm.setWidth("25em");
        consumedResourcesForm.addSaveListener(this::saveConsumedResources);
        consumedResourcesForm.addDeleteListener(this::deleteConsumedResources);
        consumedResourcesForm.addCloseEditorListener(e -> closeEditor());
    }

    private void saveConsumedResources(ConsumedResourcesForm.SaveEvent event) {
        EditConsumedResourcesDto editConsumedResourcesEvent = event.getEditConsumedResourcesEvent();
        ServiceResult serviceResult;
        if (editConsumedResourcesEvent.getId() == null) {
            serviceResult = consumedResourcesService.createConsumedResources(convertEditConsumedResourcesDto(editConsumedResourcesEvent));
        } else {
            serviceResult = consumedResourcesService.updateConsumedResources(convertEditConsumedResourcesDto(editConsumedResourcesEvent));
        }

        if (!serviceResult.isSuccess()) {
            consumedResourcesForm.getErrorMessageLabel().setEnabled(!serviceResult.isSuccess());
            consumedResourcesForm.getErrorMessageLabel().setText(serviceResult.getErrorMessage());
        } else {
            closeEditor();
            updateConsumedResourcesList();
        }
    }

    private void deleteConsumedResources(ConsumedResourcesForm.DeleteEvent event) {
        consumedResourcesService.deleteConsumedResources(convertEditConsumedResourcesDto(event.getEditConsumedResourcesEvent()));
        updateConsumedResourcesList();
        closeEditor();
    }

    private void configureGrid() {
        editConsumedResourcesDtoGrid = new Grid<>(EditConsumedResourcesDto.class);

        editConsumedResourcesDtoGrid.addClassName("contact-grid");
        editConsumedResourcesDtoGrid.setSizeFull();
        editConsumedResourcesDtoGrid.setColumns("involvedManagers", "involvedCoaches", "involvedMentors", "usedServices", "rentSpace");
        editConsumedResourcesDtoGrid.addColumn(editConsumedResourcesDto -> editConsumedResourcesDto.getProject().getName()).setHeader("Incubator Project");

        editConsumedResourcesDtoGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        editConsumedResourcesDtoGrid.asSingleSelect().addValueChangeListener(event -> editConsumedResources(event.getValue()));
    }

    private void editConsumedResources(EditConsumedResourcesDto editConsumedResourcesDto) {
        if (editConsumedResourcesDto == null) {
            closeEditor();
        } else {
            consumedResourcesForm.setEditConsumedResourcesDto(editConsumedResourcesDto);
            consumedResourcesForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        consumedResourcesForm.setEditConsumedResourcesDto(null);
        consumedResourcesForm.setVisible(false);
        consumedResourcesForm.getErrorMessageLabel().setText(null);
        consumedResourcesForm.getErrorMessageLabel().setEnabled(false);
        removeClassName("editing");
    }

    private void updateConsumedResourcesList() {
        editConsumedResourcesDtoGrid.setItems(consumedResourcesService.getAllConsumedResourcesByFilterText(filterText.getValue()).stream()
                .map(this::convertConsumedResourcesDto)
                .collect(Collectors.toList()));
    }

    private ConsumedResourcesDto convertEditConsumedResourcesDto(EditConsumedResourcesDto editConsumedResourcesDto) {
        return new ConsumedResourcesDto()
                .setId(editConsumedResourcesDto.getId())
                .setProject(editConsumedResourcesDto.getProject())
                .setInvolvedManagers(editConsumedResourcesDto.getInvolvedManagers())
                .setInvolvedMentors(editConsumedResourcesDto.getInvolvedMentors())
                .setInvolvedCoaches(editConsumedResourcesDto.getInvolvedCoaches())
                .setUsedServices(editConsumedResourcesDto.getUsedServices())
                .setRentSpace(editConsumedResourcesDto.getRentSpace());
    }

    private EditConsumedResourcesDto convertConsumedResourcesDto(ConsumedResourcesDto consumedResourcesDto) {
        EditConsumedResourcesDto editConsumedResourcesDto = new EditConsumedResourcesDto();
        editConsumedResourcesDto.setId(consumedResourcesDto.getId());
        editConsumedResourcesDto.setProject(consumedResourcesDto.getProject());
        editConsumedResourcesDto.setInvolvedManagers(consumedResourcesDto.getInvolvedManagers());
        editConsumedResourcesDto.setInvolvedMentors(consumedResourcesDto.getInvolvedMentors());
        editConsumedResourcesDto.setInvolvedCoaches(consumedResourcesDto.getInvolvedCoaches());
        editConsumedResourcesDto.setUsedServices(consumedResourcesDto.getUsedServices());
        editConsumedResourcesDto.setRentSpace(consumedResourcesDto.getRentSpace());

        return editConsumedResourcesDto;
    }
}
