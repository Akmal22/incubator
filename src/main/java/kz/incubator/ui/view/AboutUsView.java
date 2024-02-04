package kz.incubator.ui.view;

import kz.incubator.ui.MainLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@Route(value = "about", layout = MainLayout.class)
@PageTitle("About")
public class AboutUsView extends VerticalLayout {
    public AboutUsView() {
        add(new Span("Text about BIA Business Incubator Analyzer"));
    }
}
