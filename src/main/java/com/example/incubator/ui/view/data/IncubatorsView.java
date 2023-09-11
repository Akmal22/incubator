package com.example.incubator.ui.view.data;

import com.example.incubator.back.entity.user.Role;
import com.example.incubator.back.service.CountriesService;
import com.example.incubator.back.service.IncubatorService;
import com.example.incubator.back.service.UserService;
import com.example.incubator.back.service.dto.ServiceResult;
import com.example.incubator.back.service.dto.incubator.IncubatorDto;
import com.example.incubator.back.service.dto.user.UserDto;
import com.example.incubator.ui.MainLayout;
import com.example.incubator.ui.form.dto.EditIncubatorDto;
import com.example.incubator.ui.form.IncubatorForm;
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
@Route(value = "data/incubators", layout = MainLayout.class)
@PageTitle("Incubators")
public class IncubatorsView extends VerticalLayout {
    private final UserService userService;
    private final CountriesService countriesService;
    private final IncubatorService incubatorService;

    private IncubatorForm incubatorForm;
    private Grid<EditIncubatorDto> incubatorsGrid;
    private TextField filterText;

    public IncubatorsView(IncubatorService incubatorService,
                          CountriesService countriesService,
                          UserService userService,
                          AuthenticationContext authenticationContext) {
        this.incubatorService = incubatorService;
        this.countriesService = countriesService;
        this.userService = userService;
        UserDetails userDetails = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .orElseThrow(() -> new RuntimeException("Error"));

        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureIncubatorForm(userDetails);

        add(getToolBar(userDetails), getContent());

        closeEditor();
        updateIncubatorList();
    }

    private void configureGrid() {
        incubatorsGrid = new Grid<>(EditIncubatorDto.class);

        incubatorsGrid.addClassName("contact-grid");
        incubatorsGrid.setSizeFull();
        incubatorsGrid.setColumns("name", "founder", "founded");
        incubatorsGrid.addColumn(editIncubatorDto -> editIncubatorDto.getCountry().getCountryName()).setHeader("Country");
        incubatorsGrid.addColumn(editIncubatorDto -> editIncubatorDto.getManager().getUsername()).setHeader("Manager");

        incubatorsGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        incubatorsGrid.asSingleSelect().addValueChangeListener(event -> editIncubator(event.getValue()));
    }

    private void configureIncubatorForm(UserDetails userDetails) {
        List<UserDto> managers;
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_BI_MANAGER.name()))) {
            managers = List.of(userService.getManager(userDetails.getUsername()));
        } else {
            managers = userService.getAllManagers();
        }
        incubatorForm = new IncubatorForm(countriesService.getAllCountries(""), managers);

        incubatorForm.setWidth("25em");
        incubatorForm.addSaveListener(this::saveIncubator);
        incubatorForm.addDeleteListener(this::deleteIncubator);
        incubatorForm.addCloseEditorListener(e -> closeEditor());
    }

    private HorizontalLayout getToolBar(UserDetails userDetails) {
        filterText = new TextField();
        filterText.setPlaceholder("Filter incubator by name or founder ...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateIncubatorList());

        var addButton = new Button("Add incubator");
        addButton.addClickListener(e -> addIncubator());

        var toolbar = new HorizontalLayout(filterText, addButton);
        toolbar.addClassNames("toolbar");
        return toolbar;
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(incubatorsGrid, incubatorForm);
        content.setFlexGrow(2, incubatorsGrid);
        content.setFlexGrow(1, incubatorForm);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private void deleteIncubator(IncubatorForm.DeleteEvent deleteEvent) {
        incubatorService.deleteIncubator(convertEditUserDto(deleteEvent.getEditIncubatorDto()));
        updateIncubatorList();
        closeEditor();
    }

    private void addIncubator() {
        incubatorsGrid.asSingleSelect().clear();
        editIncubator(new EditIncubatorDto());
    }

    private void editIncubator(EditIncubatorDto editIncubatorDto) {
        if (editIncubatorDto == null) {
            closeEditor();
        } else {
            incubatorForm.setEditIncubatorDto(editIncubatorDto);
            incubatorForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void saveIncubator(IncubatorForm.SaveEvent event) {
        EditIncubatorDto editIncubatorDto = event.getEditIncubatorDto();
        ServiceResult serviceResult;
        if (editIncubatorDto.getId() == null) {
            serviceResult = incubatorService.createIncubator(convertEditUserDto(editIncubatorDto));
        } else {
            serviceResult = incubatorService.updateIncubator(convertEditUserDto(editIncubatorDto));
        }

        if (!serviceResult.isSuccess()) {
            incubatorForm.getErrorMessageLabel().setEnabled(!serviceResult.isSuccess());
            incubatorForm.getErrorMessageLabel().setText(serviceResult.getErrorMessage());
        } else {
            closeEditor();
            updateIncubatorList();
        }
    }

    private void closeEditor() {
        incubatorForm.setEditIncubatorDto(null);
        incubatorForm.setVisible(false);
        incubatorForm.getErrorMessageLabel().setText(null);
        incubatorForm.getErrorMessageLabel().setEnabled(false);
        removeClassName("editing");
    }

    private void updateIncubatorList() {
        incubatorsGrid.setItems(incubatorService.getIncubatorsByFilterText(filterText.getValue()).stream()
                .map(this::convertIncubatorDto)
                .collect(Collectors.toList()));
    }

    private EditIncubatorDto convertIncubatorDto(IncubatorDto incubatorDto) {
        EditIncubatorDto editIncubatorDto = new EditIncubatorDto();
        editIncubatorDto.setId(incubatorDto.getId());
        editIncubatorDto.setName(incubatorDto.getIncubatorName());
        editIncubatorDto.setFounded(incubatorDto.getFounded());
        editIncubatorDto.setFounder(incubatorDto.getFounder());
        editIncubatorDto.setManager(incubatorDto.getManager());
        editIncubatorDto.setCountry(incubatorDto.getCountry());

        return editIncubatorDto;
    }

    private IncubatorDto convertEditUserDto(EditIncubatorDto editIncubatorDto) {
        return new IncubatorDto()
                .setId(editIncubatorDto.getId())
                .setIncubatorName(editIncubatorDto.getName())
                .setFounder(editIncubatorDto.getFounder())
                .setFounded(editIncubatorDto.getFounded())
                .setManager(editIncubatorDto.getManager())
                .setCountry(editIncubatorDto.getCountry());
    }
}
