package com.example.incubator.ui.view.data;

import com.example.incubator.backend.service.ClientsService;
import com.example.incubator.backend.service.IncubatorProjectService;
import com.example.incubator.backend.service.dto.ServiceResult;
import com.example.incubator.backend.service.dto.ClientsDto;
import com.example.incubator.ui.MainLayout;
import com.example.incubator.ui.form.ClientsForm;
import com.example.incubator.ui.form.dto.EditClientsDto;
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
@Route(value = "data/clients", layout = MainLayout.class)
@PageTitle("Countries")
public class ClientsView extends VerticalLayout {
    private final ClientsService clientsService;
    private final IncubatorProjectService incubatorProjectService;

    private ClientsForm clientsForm;
    private Grid<EditClientsDto> clientsDtoGrid;
    private TextField filterText;

    public ClientsView(AuthenticationContext authenticationContext,
                       ClientsService clientsService,
                       IncubatorProjectService incubatorProjectService) {
        this.clientsService = clientsService;
        this.incubatorProjectService = incubatorProjectService;

        UserDetails userDetails = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .orElseThrow(() -> new RuntimeException("Error"));

        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureClientsForm(userDetails);

        add(getToolBar(), getContent());

        closeEditor();
        updateClientsList();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(clientsDtoGrid, clientsForm);
        content.setFlexGrow(2, clientsDtoGrid);
        content.setFlexGrow(1, clientsForm);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private HorizontalLayout getToolBar() {
        filterText = new TextField();
        filterText.setPlaceholder("Filter clients info by incubator project name ...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateClientsList());

        var addButton = new Button("Add clients info");
        addButton.addClickListener(e -> addClientsDto());

        var toolbar = new HorizontalLayout(filterText, addButton);
        toolbar.addClassNames("toolbar");
        return toolbar;
    }

    private void addClientsDto() {
        clientsDtoGrid.asSingleSelect().clear();
        editClientsDto(new EditClientsDto());
    }

    private void configureClientsForm(UserDetails userDetails) {
        clientsForm = new ClientsForm(userDetails, incubatorProjectService);

        clientsForm.setWidth("25em");
        clientsForm.addSaveListener(this::saveClientsDto);
        clientsForm.addDeleteListener(this::deleteClientsDto);
        clientsForm.addCloseEditorListener(e -> closeEditor());
    }

    private void saveClientsDto(ClientsForm.SaveEvent event) {
        EditClientsDto editClientsDto = event.getEditClientsDto();
        ServiceResult serviceResult;
        if (editClientsDto.getId() == null) {
            serviceResult = clientsService.createClientsDto(convertEditClients(editClientsDto));
        } else {
            serviceResult = clientsService.updateClientsDto(convertEditClients(editClientsDto));
        }

        if (!serviceResult.isSuccess()) {
            clientsForm.getErrorMessageLabel().setEnabled(!serviceResult.isSuccess());
            clientsForm.getErrorMessageLabel().setText(serviceResult.getErrorMessage());
        } else {
            closeEditor();
            updateClientsList();
        }
    }

    private void deleteClientsDto(ClientsForm.DeleteEvent event) {
        clientsService.deleteClientsInfo(convertEditClients(event.getEditClientsDto()));
        updateClientsList();
        closeEditor();
    }

    private void updateClientsList() {
        clientsDtoGrid.setItems(clientsService.findByIncubatorFilterText(filterText.getValue()).stream()
                .map(this::convertClientsDto)
                .collect(Collectors.toList()));
    }

    private void configureGrid() {
        clientsDtoGrid = new Grid<>(EditClientsDto.class);

        clientsDtoGrid.addClassName("contact-grid");
        clientsDtoGrid.setSizeFull();
        clientsDtoGrid.setColumns("applications", "accepted", "graduated", "failed");
        clientsDtoGrid.addColumn(editClientsDto -> editClientsDto.getProject().getName()).setHeader("Incubator Project");

        clientsDtoGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        clientsDtoGrid.asSingleSelect().addValueChangeListener(event -> editClientsDto(event.getValue()));
    }

    private void editClientsDto(EditClientsDto editClientsDto) {
        if (editClientsDto == null) {
            closeEditor();
        } else {
            clientsForm.setEditClientsDto(editClientsDto);
            clientsForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        clientsForm.setEditClientsDto(null);
        clientsForm.setVisible(false);
        clientsForm.getErrorMessageLabel().setText(null);
        clientsForm.getErrorMessageLabel().setEnabled(false);
        removeClassName("editing");
    }

    private EditClientsDto convertClientsDto(ClientsDto clientsDto) {
        EditClientsDto editClientsDto = new EditClientsDto();
        editClientsDto.setId(clientsDto.getId());
        editClientsDto.setProject(clientsDto.getIncubatorProjectDto());
        editClientsDto.setApplications(clientsDto.getApplications());
        editClientsDto.setAccepted(clientsDto.getAccepted());
        editClientsDto.setGraduated(clientsDto.getGraduated());
        editClientsDto.setFailed(clientsDto.getFailed());

        return editClientsDto;
    }

    private ClientsDto convertEditClients(EditClientsDto editClientsDto) {
        return new ClientsDto()
                .setId(editClientsDto.getId())
                .setIncubatorProjectDto(editClientsDto.getProject())
                .setApplications(editClientsDto.getApplications())
                .setAccepted(editClientsDto.getAccepted())
                .setGraduated(editClientsDto.getGraduated())
                .setFailed(editClientsDto.getFailed());
    }
}
