package com.example.incubator.ui.view;

import com.example.incubator.back.service.UserService;
import com.example.incubator.back.service.dto.EditUserDto;
import com.example.incubator.back.service.dto.ServiceResult;
import com.example.incubator.ui.MainLayout;
import com.example.incubator.ui.form.UserForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import static org.apache.commons.lang3.StringUtils.isBlank;


@RolesAllowed("ADMIN")
@Route(value = "users", layout = MainLayout.class)
@PageTitle("Users")
public class UsersView extends VerticalLayout {
    private final UserService userService;

    private UserForm userForm;
    Grid<EditUserDto> usersGrid = new Grid<>(EditUserDto.class);
    TextField filterText = new TextField();

    public UsersView(UserService userService) {
        this.userService = userService;

        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureUsersGrid();

        add(getToolBar(), getContent());

        closeEditor();
        updateUserList();
    }

    private HorizontalLayout getToolBar() {

        filterText.setPlaceholder("Filter user by name or email ...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateUserList());

        var addButton = new Button("Add user");
        addButton.addClickListener(e -> addUser());

        var toolbar = new HorizontalLayout(filterText, addButton);
        toolbar.addClassNames("toolbar");
        return toolbar;
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(usersGrid, userForm);
        content.setFlexGrow(2, usersGrid);
        content.setFlexGrow(1, userForm);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private void configureGrid() {
        usersGrid.addClassName("contact-grid");
        usersGrid.setSizeFull();
        usersGrid.setColumns("username");
        usersGrid.addColumn(editUserDto -> editUserDto.getRole().getName()).setHeader("Role");

        usersGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        usersGrid.asSingleSelect().addValueChangeListener(event -> editUser(event.getValue(), true));
    }

    private void configureUsersGrid() {
        userForm = new UserForm();

        userForm.setWidth("25em");
        userForm.addSaveListener(this::saveUser);
        userForm.addDeleteListener(this::deleteUser);
        userForm.addCloseEditor(e -> closeEditor());
    }

    private void saveUser(UserForm.SaveEvent event) {
        EditUserDto editUserDto = event.getUserDto();
        ServiceResult serviceResult;
        if (isBlank(editUserDto.getUuid())) {
            serviceResult = userService.createUser(editUserDto);
        } else {
            serviceResult = userService.updateUser(editUserDto);
        }


        if (!serviceResult.isSuccess()) {
            userForm.getErrorMessageLabel().setEnabled(!serviceResult.isSuccess());
            userForm.getErrorMessageLabel().setText(serviceResult.getErrorMessage());
        } else {
            closeEditor();
            updateUserList();
        }
    }

    private void deleteUser(UserForm.DeleteEvent event) {
        userService.deleteUser(event.getUserDto());
        updateUserList();
        closeEditor();
    }

    private void addUser() {
        usersGrid.asSingleSelect().clear();
        editUser(new EditUserDto(), false);
    }

    private void editUser(EditUserDto editUserDto, boolean usernameReadOnly) {
        if (editUserDto == null) {
            closeEditor();
        } else {
            userForm.setUsernameReadOnly(usernameReadOnly);
            userForm.setUser(editUserDto);
            userForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        userForm.setUser(null);
        userForm.setVisible(false);
        userForm.getErrorMessageLabel().setText(null);
        userForm.getErrorMessageLabel().setEnabled(false);
        removeClassName("editing");
    }

    private void updateUserList() {
        usersGrid.setItems(userService.getAllUsers(filterText.getValue()));
    }
}