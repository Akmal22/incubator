package com.example.incubator.ui.view.data;

import com.example.incubator.ui.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;


@RolesAllowed({"ADMIN"})
@Route(value = "data/countries", layout = MainLayout.class)
@PageTitle("")
public class CountriesView extends VerticalLayout {

}
