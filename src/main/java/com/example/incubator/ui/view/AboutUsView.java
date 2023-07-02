package com.example.incubator.ui.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed("USER")
@Route(value = "about", layout = MainLayout.class)
@PageTitle("О нас")
public class AboutUsView extends VerticalLayout {
}
