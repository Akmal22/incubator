package com.example.incubator.ui.view;

import com.example.incubator.ui.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed({"USER","ADMIN","BI_MANAGER"})
@Route(value = "about", layout = MainLayout.class)
@PageTitle("About")
public class AboutUsView extends VerticalLayout {
}
