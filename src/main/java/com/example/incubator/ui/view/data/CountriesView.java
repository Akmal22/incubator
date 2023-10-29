package com.example.incubator.ui.view.data;

import com.example.incubator.back.service.CountriesService;
import com.example.incubator.back.service.dto.ServiceResult;
import com.example.incubator.back.service.dto.country.CountryDto;
import com.example.incubator.ui.MainLayout;
import com.example.incubator.ui.form.CountryForm;
import com.example.incubator.ui.form.dto.EditCountryDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.stream.Collectors;


@RolesAllowed({"ADMIN"})
@Route(value = "data/countries", layout = MainLayout.class)
@PageTitle("Countries")
public class CountriesView extends VerticalLayout {
    private final CountriesService countriesService;

    private Grid<EditCountryDto> countryGrid;
    private CountryForm countryForm;
    private TextField filterText;

    public CountriesView(CountriesService countriesService) {
        this.countriesService = countriesService;

        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureCountryForm();

        add(getToolBar(), getContent());

        closeEditor();
        updateCountriesList();
    }

    private HorizontalLayout getToolBar() {
        filterText = new com.vaadin.flow.component.textfield.TextField();
        filterText.setPlaceholder("Filter countries by name ...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateCountriesList());

        var addButton = new Button("Add country");
        addButton.addClickListener(e -> addCountry());

        var toolbar = new HorizontalLayout(filterText, addButton);
        toolbar.addClassNames("toolbar");
        return toolbar;
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(countryGrid, countryForm);
        content.setFlexGrow(2, countryGrid);
        content.setFlexGrow(1, countryForm);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private void configureCountryForm() {
        countryForm = new CountryForm();

        countryForm.setWidth("25em");
        countryForm.addSaveListener(this::saveCountry);
        countryForm.addDeleteListener(this::deleteCountry);
        countryForm.addCloseEditorListener(e -> closeEditor());
    }

    private void configureGrid() {
        countryGrid = new Grid<>(EditCountryDto.class);

        countryGrid.addClassName("contact-grid");
        countryGrid.setSizeFull();
        countryGrid.setColumns("countryName");

        countryGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        countryGrid.asSingleSelect().addValueChangeListener(event -> editCountry(event.getValue()));
    }

    private void saveCountry(CountryForm.SaveEvent event) {
        EditCountryDto editCountryDto = event.getEditCountryDto();
        ServiceResult serviceResult;
        if (editCountryDto.getId() == null) {
            serviceResult = countriesService.createCountry(convertEditCountryDto(editCountryDto));
        } else {
            serviceResult = countriesService.updateCountry(convertEditCountryDto(editCountryDto));
        }

        if (!serviceResult.isSuccess()) {
            countryForm.getErrorMessageLabel().setEnabled(!serviceResult.isSuccess());
            countryForm.getErrorMessageLabel().setText(serviceResult.getErrorMessage());
        } else {
            closeEditor();
            updateCountriesList();
        }
    }

    private void deleteCountry(CountryForm.DeleteEvent event) {
        ServiceResult serviceResult = countriesService.deleteCountry(convertEditCountryDto(event.getEditCountryDto()));
        if (!serviceResult.isSuccess()) {
            countryForm.getErrorMessageLabel().setEnabled(!serviceResult.isSuccess());
            countryForm.getErrorMessageLabel().setText(serviceResult.getErrorMessage());
        } else {
            updateCountriesList();
            closeEditor();
        }
    }

    private void addCountry() {
        countryGrid.asSingleSelect().clear();
        editCountry(new EditCountryDto());
    }

    private void editCountry(EditCountryDto editUserDto) {
        if (editUserDto == null) {
            closeEditor();
        } else {
            countryForm.setCountryDto(editUserDto);
            countryForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        countryForm.setCountryDto(null);
        countryForm.setVisible(false);
        countryForm.getErrorMessageLabel().setText(null);
        countryForm.getErrorMessageLabel().setEnabled(false);
        removeClassName("editing");
    }

    private void updateCountriesList() {
        countryGrid.setItems(countriesService.getAllCountries(filterText.getValue()).stream()
                .map(this::convertCountryDto)
                .collect(Collectors.toList()));
    }

    private EditCountryDto convertCountryDto(CountryDto countryDto) {
        EditCountryDto editCountryDto = new EditCountryDto();
        editCountryDto.setId(countryDto.getId());
        editCountryDto.setCountryName(countryDto.getCountryName());

        return editCountryDto;
    }

    private CountryDto convertEditCountryDto(EditCountryDto editCountryDto) {
        CountryDto countryDto = new CountryDto();
        countryDto.setId(editCountryDto.getId());
        countryDto.setCountryName(editCountryDto.getCountryName());

        return countryDto;
    }
}
