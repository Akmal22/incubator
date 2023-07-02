package com.example.incubator.ui.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed("ADMIN")
@Route(value = "shortreport", layout = MainLayout.class)
@PageTitle("Краткий отчет")
public class ShortReportView extends VerticalLayout {
}
