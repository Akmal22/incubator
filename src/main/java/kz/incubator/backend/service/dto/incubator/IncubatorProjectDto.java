package kz.incubator.backend.service.dto.incubator;

import kz.incubator.backend.service.dto.ClientsDto;
import kz.incubator.backend.service.dto.ConsumedResourcesDto;
import kz.incubator.backend.service.dto.ExpenseDto;
import kz.incubator.backend.service.dto.InvestmentDto;
import kz.incubator.backend.service.dto.RevenueDto;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class IncubatorProjectDto {
    private Long id;
    private String name;
    private IncubatorDto incubatorDto;
    private ClientsDto clientsDto;
    private RevenueDto revenueDto;
    private ConsumedResourcesDto consumedResourcesDto;
    private InvestmentDto investmentDto;
    private ExpenseDto expenseDto;
    private LocalDate startDate;
    private LocalDate endDate;
}
