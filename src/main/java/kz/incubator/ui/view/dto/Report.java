package kz.incubator.ui.view.dto;

import com.vaadin.flow.component.Component;
import lombok.Builder;
import lombok.Getter;

import java.util.Collection;

@Getter
@Builder
public class Report {
    private String founder;
    private String founded;
    private String projectName;
    private Collection<Component> clientsCharts;
}
