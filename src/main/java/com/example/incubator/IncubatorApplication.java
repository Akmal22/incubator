package com.example.incubator;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme("incubator")
public class IncubatorApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(IncubatorApplication.class, args);
    }

}
