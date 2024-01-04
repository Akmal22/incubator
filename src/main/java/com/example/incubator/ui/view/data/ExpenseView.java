package com.example.incubator.ui.view.data;

import com.example.incubator.backend.service.ExpenseService;
import com.example.incubator.backend.service.IncubatorProjectService;
import com.example.incubator.backend.service.dto.ExpenseDto;
import com.example.incubator.backend.service.dto.ServiceResult;
import com.example.incubator.ui.MainLayout;
import com.example.incubator.ui.form.ExpenseForm;
import com.example.incubator.ui.form.dto.EditExpenseDto;
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

@RolesAllowed({"ADMIN"})
@Route(value = "data/expense", layout = MainLayout.class)
@PageTitle("Expense")
public class ExpenseView extends VerticalLayout {
    private final ExpenseService expenseService;
    private final IncubatorProjectService incubatorProjectService;

    private ExpenseForm expenseForm;
    private Grid<EditExpenseDto> expenseDtoGrid;
    private TextField filterText;

    public ExpenseView(AuthenticationContext authenticationContext,
                       ExpenseService expenseService,
                       IncubatorProjectService incubatorProjectService) {
        this.expenseService = expenseService;
        this.incubatorProjectService = incubatorProjectService;

        UserDetails userDetails = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .orElseThrow(() -> new RuntimeException("Error"));

        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureExpenseForm(userDetails);

        add(getToolBar(), getContent());

        closeEditor();
        updateExpenseList();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(expenseDtoGrid, expenseForm);
        content.setFlexGrow(2, expenseDtoGrid);
        content.setFlexGrow(1, expenseForm);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private void configureGrid() {
        expenseDtoGrid = new Grid<>(EditExpenseDto.class);

        expenseDtoGrid.addClassName("contact-grid");
        expenseDtoGrid.setSizeFull();
        expenseDtoGrid.setColumns("marketing", "payroll", "equipment", "utilities", "material", "insurance");
        expenseDtoGrid.addColumn(editExpenseDto -> editExpenseDto.getProject().getName()).setHeader("Incubator Project");

        expenseDtoGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        expenseDtoGrid.asSingleSelect().addValueChangeListener(event -> editExpenseDto(event.getValue()));
    }

    private void configureExpenseForm(UserDetails userDetails) {
        expenseForm = new ExpenseForm(userDetails, incubatorProjectService);

        expenseForm.setWidth("25em");
        expenseForm.addSaveListener(this::saveExpense);
        expenseForm.addDeleteListener(this::deleteExpense);
        expenseForm.addCloseEditorListener(e -> closeEditor());
    }

    private void saveExpense(ExpenseForm.SaveEvent event) {
        EditExpenseDto editExpenseDto = event.getEditExpenseDto();
        ServiceResult serviceResult;
        if (editExpenseDto.getId() == null) {
            serviceResult = expenseService.createNewBIData(convertEditExpenseDto(editExpenseDto));
        } else {
            serviceResult = expenseService.updateBIData(convertEditExpenseDto(editExpenseDto));
        }

        if (!serviceResult.isSuccess()) {
            expenseForm.getErrorMessageLabel().setEnabled(!serviceResult.isSuccess());
            expenseForm.getErrorMessageLabel().setText(serviceResult.getErrorMessage());
        } else {
            closeEditor();
            updateExpenseList();
        }
    }

    private HorizontalLayout getToolBar() {
        filterText = new TextField();
        filterText.setPlaceholder("Filter expense info by incubator project name ...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateExpenseList());

        var addButton = new Button("Add expense info");
        addButton.addClickListener(e -> addExpenseInfo());

        var toolbar = new HorizontalLayout(filterText, addButton);
        toolbar.addClassNames("toolbar");
        return toolbar;
    }

    private void addExpenseInfo() {
        expenseDtoGrid.asSingleSelect().clear();
        editExpenseDto(new EditExpenseDto());
    }

    private void updateExpenseList() {
        expenseDtoGrid.setItems(expenseService.findByIncubatorFilterText(filterText.getValue()).stream()
                .map(this::convertExpenseDto)
                .collect(Collectors.toList()));
    }

    private void deleteExpense(ExpenseForm.DeleteEvent event) {
        expenseService.deleteData(convertEditExpenseDto(event.getEditExpenseDto()));
        updateExpenseList();
        closeEditor();
    }

    private void editExpenseDto(EditExpenseDto editExpenseDto) {
        if (editExpenseDto == null) {
            closeEditor();
        } else {
            expenseForm.setEditExpenseDto(editExpenseDto);
            expenseForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        expenseForm.setEditExpenseDto(null);
        expenseForm.setVisible(false);
        expenseForm.getErrorMessageLabel().setText(null);
        expenseForm.getErrorMessageLabel().setEnabled(false);
        removeClassName("editing");
    }

    private EditExpenseDto convertExpenseDto(ExpenseDto expenseDto) {
        EditExpenseDto editExpenseDto = new EditExpenseDto();
        editExpenseDto.setId(expenseDto.getId());
        editExpenseDto.setProject(expenseDto.getProject());
        editExpenseDto.setMarketing(expenseDto.getMarketing());
        editExpenseDto.setEquipment(expenseDto.getEquipment());
        editExpenseDto.setInsurance(expenseDto.getInsurance());
        editExpenseDto.setMaterial(expenseDto.getMaterial());
        editExpenseDto.setPayroll(expenseDto.getPayroll());
        editExpenseDto.setUtilities(expenseDto.getUtilities());

        return editExpenseDto;
    }

    private ExpenseDto convertEditExpenseDto(EditExpenseDto editExpenseDto) {
        return new ExpenseDto()
                .setId(editExpenseDto.getId())
                .setProject(editExpenseDto.getProject())
                .setMarketing(editExpenseDto.getMarketing())
                .setEquipment(editExpenseDto.getEquipment())
                .setInsurance(editExpenseDto.getInsurance())
                .setMaterial(editExpenseDto.getMaterial())
                .setPayroll(editExpenseDto.getPayroll())
                .setUtilities(editExpenseDto.getUtilities());
    }
}
