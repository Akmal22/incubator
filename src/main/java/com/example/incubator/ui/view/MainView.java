package com.example.incubator.ui.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed({"ADMIN", "USER"})
@Route(value = "", layout = MainLayout.class)
@PageTitle("Главная")
public class MainView extends VerticalLayout {

}
