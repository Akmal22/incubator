package com.example.incubator.ui.view;


import com.example.incubator.back.entity.Role;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MainLayout extends AppLayout {
    private final String appName;

    public MainLayout(@Value("${app.name}") String appName, AuthenticationContext authenticationContext) {
        this.appName = appName;
        createHeader();
        createDrawer(authenticationContext);
    }

    private void createHeader() {
        H1 logo = new H1(appName);
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM
        );

        var header = new HorizontalLayout(new DrawerToggle(), logo);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM
        );

        addToNavbar(header);
    }

    private void createDrawer(AuthenticationContext authenticationContext) {
        UserDetails optionalUserDetails = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .orElseThrow(() -> new RuntimeException("Error"));
        if (optionalUserDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_USER.name()))) {
            addToDrawer(new VerticalLayout(
                    new RouterLink("Главная", MainView.class),
                    new RouterLink("Краткий отчет", ShortReportView.class),
                    new RouterLink("Полный отчет", FullReportView.class),
                    new RouterLink("Добавить информацию", FullReportView.class),
                    new RouterLink("О нас", AboutUsView.class)
            ));
        } else {
            addToDrawer(new VerticalLayout(
                    new RouterLink("Главная", MainView.class),
                    new RouterLink("Краткий отчет", ShortReportView.class),
                    new RouterLink("Полный отчет", FullReportView.class),
                    new RouterLink("Добавить информацию", FullReportView.class)
            ));
        }

    }
}
