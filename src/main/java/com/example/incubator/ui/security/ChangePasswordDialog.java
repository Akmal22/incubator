package com.example.incubator.ui.security;


import com.example.incubator.back.service.UserService;
import com.example.incubator.back.service.dto.form.user.ChangePasswordDto;
import com.example.incubator.back.service.dto.ServiceResult;
import com.example.incubator.ui.form.ChangePasswordForm;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ChangePasswordDialog extends Dialog {
    private final UserService userService;

    PasswordField newPassword = new PasswordField("New password");
    PasswordField repeatPassword = new PasswordField("Repeat password");
    PasswordField currentPassword = new PasswordField("Current password");
    Label errorMessageLabel = new Label();

    public ChangePasswordDialog(UserService userService) {
        this.userService = userService;

        setHeaderTitle("Change password");
        errorMessageLabel.getStyle().set("color", "Red");

        Button saveButton = new Button("Save");
        Button closeButton = new Button("Close");

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

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

        binder.forField(repeatPassword)
                .withValidator(repeatPasswordValue -> isNotBlank(repeatPasswordValue) && repeatPasswordValue.equals(newPassword.getValue()),
                        "Passwords are not same")
                .bind(ChangePasswordForm::getRepeatPassword, ChangePasswordForm::setRepeatPassword);

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
