package com.example.incubator.ui.view;

import com.example.incubator.ui.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed({"ADMIN", "USER", "BI_MANAGER"})
@Route(value = "", layout = MainLayout.class)
@PageTitle("Report")
public class ReportView extends VerticalLayout {
}
