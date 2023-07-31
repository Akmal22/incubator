package com.example.incubator.ui;

import com.example.incubator.back.entity.Role;
import com.example.incubator.back.service.UserService;
import com.example.incubator.back.service.security.SecurityService;
import com.example.incubator.ui.security.ChangePasswordDialog;
import com.example.incubator.ui.view.AboutUsView;
import com.example.incubator.ui.view.AddDataView;
import com.example.incubator.ui.view.ReportView;
import com.example.incubator.ui.view.UsersView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
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
    private final SecurityService securityService;
    private final UserService userService;

    public MainLayout(@Value("${app.name}") String appName,
                      AuthenticationContext authenticationContext,
                      SecurityService securityService,
                      UserService userService
    ) {
        this.appName = appName;
        this.securityService = securityService;
        this.userService = userService;
        createHeader();
        createDrawer(authenticationContext);
    }

    private void createHeader() {
        H1 logo = new H1(appName);
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM
        );

        var header = new HorizontalLayout(new DrawerToggle(), logo, getUserActionMenuBar());
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM
        );

        addToNavbar(header);
    }

    private MenuBar getUserActionMenuBar() {
        String username = securityService.getAuthenticationContext().getUsername();
        Avatar avatar = new Avatar(username);
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        MenuItem menuItem = menuBar.addItem(avatar);
        SubMenu subMenu = menuItem.getSubMenu();
        MenuItem changePasswordMenuItem = subMenu.addItem("Change password");
        MenuItem logoutMenuItem = subMenu.addItem("Log out");
        changePasswordMenuItem.addClickListener(e -> this.changePasswordListener());
        logoutMenuItem.addClickListener(e -> securityService.logout());

        return menuBar;
    }


    private void changePasswordListener() {
        ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(userService);
        changePasswordDialog.open();
    }

    private void createDrawer(AuthenticationContext authenticationContext) {
        UserDetails optionalUserDetails = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .orElseThrow(() -> new RuntimeException("Error"));
        if (optionalUserDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            addToDrawer(new VerticalLayout(
                    new RouterLink("Report", ReportView.class),
                    new RouterLink("Users", UsersView.class),
                    new RouterLink("Add Data", AddDataView.class),
                    new RouterLink("About", AboutUsView.class)
            ));
        }
        if (optionalUserDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_BI_MANAGER.name()))) {
            addToDrawer(new VerticalLayout(
                    new RouterLink("Report", ReportView.class),
                    new RouterLink("Add Data", AddDataView.class),
                    new RouterLink("About", AboutUsView.class)
            ));
        }

        if (optionalUserDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_USER.name()))) {
            addToDrawer(new VerticalLayout(
                    new RouterLink("Report", ReportView.class),
                    new RouterLink("About", AboutUsView.class)
            ));
        }
    }
}