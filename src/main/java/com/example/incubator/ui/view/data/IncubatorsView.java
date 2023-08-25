package com.example.incubator.ui.view.data;

import com.example.incubator.back.service.IncubatorService;
import com.example.incubator.ui.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed({"ADMIN", "BI_MANAGER"})
@Route(value = "data/incubators", layout = MainLayout.class)
@PageTitle("Incubators")
public class IncubatorsView extends VerticalLayout {
    private final IncubatorService incubatorService;

    public IncubatorsView(IncubatorService incubatorService) {
        this.incubatorService = incubatorService;
    }


}
