package kz.incubator.ui.view.data;

import kz.incubator.backend.service.IncubatorProjectService;
import kz.incubator.backend.service.InvestmentService;
import kz.incubator.backend.service.dto.InvestmentDto;
import kz.incubator.backend.service.dto.ServiceResult;
import kz.incubator.ui.MainLayout;
import kz.incubator.ui.form.InvestmentForm;
import kz.incubator.ui.form.dto.EditInvestmentDto;
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
@Route(value = "data/investment", layout = MainLayout.class)
@PageTitle("Investment")
public class InvestmentView extends VerticalLayout {
    private final InvestmentService investmentService;
    private final IncubatorProjectService incubatorProjectService;

    private InvestmentForm investmentForm;
    private Grid<EditInvestmentDto> investmentDtoGrid;
    private TextField filterText;

    public InvestmentView(AuthenticationContext authenticationContext,
                          InvestmentService investmentService,
                          IncubatorProjectService incubatorProjectService) {
        this.investmentService = investmentService;
        this.incubatorProjectService = incubatorProjectService;

        UserDetails userDetails = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .orElseThrow(() -> new RuntimeException("Error"));

        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureInvestmentForm(userDetails);

        add(getToolBar(), getContent());

        closeEditor();
        updateInvestmentList();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(investmentDtoGrid, investmentForm);
        content.setFlexGrow(2, investmentDtoGrid);
        content.setFlexGrow(1, investmentForm);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private HorizontalLayout getToolBar() {
        filterText = new TextField();
        filterText.setPlaceholder("Filter investment info by incubator project name ...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateInvestmentList());

        var addButton = new Button("Add investment info");
        addButton.addClickListener(e -> addInvestmentInfo());

        var toolbar = new HorizontalLayout(filterText, addButton);
        toolbar.addClassNames("toolbar");
        return toolbar;
    }

    private void addInvestmentInfo() {
        investmentDtoGrid.asSingleSelect().clear();
        editInvestment(new EditInvestmentDto());
    }

    private void updateInvestmentList() {
        investmentDtoGrid.setItems(investmentService.findByIncubatorFilterText(filterText.getValue()).stream()
                .map(this::convertInvestmentDto)
                .collect(Collectors.toList()));
    }

    private void configureInvestmentForm(UserDetails userDetails) {
        investmentForm = new InvestmentForm(userDetails, incubatorProjectService);

        investmentForm.setWidth("25em");
        investmentForm.addSaveListener(this::saveInvestment);
        investmentForm.addDeleteListener(this::deleteInvestment);
        investmentForm.addCloseEditorListener(e -> closeEditor());
    }

    private void saveInvestment(InvestmentForm.SaveEvent event) {
        EditInvestmentDto editInvestmentDto = event.getEditInvestmentDto();
        ServiceResult serviceResult;
        if (editInvestmentDto.getId() == null) {
            serviceResult = investmentService.createNewBIData(convertEditInvestmentDto(editInvestmentDto));
        } else {
            serviceResult = investmentService.updateBIData(convertEditInvestmentDto(editInvestmentDto));
        }

        if (!serviceResult.isSuccess()) {
            investmentForm.getErrorMessageLabel().setEnabled(!serviceResult.isSuccess());
            investmentForm.getErrorMessageLabel().setText(serviceResult.getErrorMessage());
        } else {
            closeEditor();
            updateInvestmentList();
        }
    }

    private void deleteInvestment(InvestmentForm.DeleteEvent event) {
        investmentService.deleteData(convertEditInvestmentDto(event.getEditInvestmentDto()));
        updateInvestmentList();
        closeEditor();
    }

    private void configureGrid() {
        investmentDtoGrid = new Grid<>(EditInvestmentDto.class);

        investmentDtoGrid.addClassName("contact-grid");
        investmentDtoGrid.setSizeFull();
        investmentDtoGrid.setColumns("investorsCount", "percentageOfInvestedClients");
        investmentDtoGrid.addColumn(editInvestmentDto -> editInvestmentDto.getProject().getName()).setHeader("Incubator Project");

        investmentDtoGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        investmentDtoGrid.asSingleSelect().addValueChangeListener(event -> editInvestment(event.getValue()));
    }

    private void editInvestment(EditInvestmentDto editInvestmentDto) {
        if (editInvestmentDto == null) {
            closeEditor();
        } else {
            investmentForm.setEditInvestmentDto(editInvestmentDto);
            investmentForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        investmentForm.setEditInvestmentDto(null);
        investmentForm.setVisible(false);
        investmentForm.getErrorMessageLabel().setText(null);
        investmentForm.getErrorMessageLabel().setEnabled(false);
        removeClassName("editing");
    }

    private EditInvestmentDto convertInvestmentDto(InvestmentDto investmentDto) {
        EditInvestmentDto editInvestmentDto = new EditInvestmentDto();
        editInvestmentDto.setId(investmentDto.getId());
        editInvestmentDto.setProject(investmentDto.getProject());
        editInvestmentDto.setInvestorsCount(investmentDto.getInvestorsCount());
        editInvestmentDto.setPercentageOfInvestedClients(investmentDto.getPercentageOfInvestedClients());

        return editInvestmentDto;
    }

    private InvestmentDto convertEditInvestmentDto(EditInvestmentDto editInvestmentDto) {
        return new InvestmentDto()
                .setId(editInvestmentDto.getId())
                .setProject(editInvestmentDto.getProject())
                .setInvestorsCount(editInvestmentDto.getInvestorsCount())
                .setPercentageOfInvestedClients(editInvestmentDto.getPercentageOfInvestedClients());
    }
}
