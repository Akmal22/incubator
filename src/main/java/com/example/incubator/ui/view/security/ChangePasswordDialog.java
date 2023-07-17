package com.example.incubator.ui.view.security;


import com.example.incubator.back.service.UserService;
import com.example.incubator.back.service.dto.ChangePasswordDto;
import com.example.incubator.back.service.dto.ServiceResult;
import com.example.incubator.ui.dto.ChangePasswordForm;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public class ChangePasswordDialog extends Dialog {
    private final UserService userService;

    TextField newPassword = new TextField("New password");
    TextField repeatPassword = new TextField("Repeat password");
    TextField currentPassword = new TextField("Current password");
    Label errorMessageLabel = new Label();

    public ChangePasswordDialog(UserService userService) {
        this.userService = userService;

        setHeaderTitle("Change password");
        errorMessageLabel.getStyle().set("color", "Red");

        Button saveButton = new Button("Save");
        Button closeButton = new Button("Close");

        saveButton.addClickShortcut(Key.ENTER);
        closeButton.addClickShortcut(Key.ESCAPE);
        closeButton.addClickListener(e -> this.close());
        getFooter().add(saveButton);
        getFooter().add(closeButton);

        VerticalLayout dialogLayout = new VerticalLayout(errorMessageLabel, newPassword, repeatPassword, currentPassword);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "22rem").set("max-width", "100%");

        Binder<ChangePasswordForm> binder = new BeanValidationBinder<>(ChangePasswordForm.class);
        binder.setBean(new ChangePasswordForm());
        binder.bindInstanceFields(this);

        binder.addStatusChangeListener(e -> saveButton.setEnabled(binder.isValid()));
        saveButton.addClickListener(e -> validateAndSave(binder, errorMessageLabel));
        add(dialogLayout);
    }

    private void validateAndSave(Binder<ChangePasswordForm> binder, Label errorMessageLabel) {
        boolean success;
        String errorMessage;
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();

        try {
            binder.writeBean(changePasswordForm);
            ServiceResult serviceResult = userService.changePassword(new ChangePasswordDto(changePasswordForm.getNewPassword(), changePasswordForm.getCurrentPassword()));
            success = serviceResult.isSuccess();
            errorMessage = serviceResult.getErrorMessage();
        } catch (ValidationException exc) {
            success = false;
            errorMessage = "Validation error!";
        }

        errorMessageLabel.setEnabled(!success);
        errorMessageLabel.setText(errorMessage);
    }
}
