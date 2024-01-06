package com.example.incubator.backend.service;

import com.example.incubator.backend.entity.data.ExpenseEntity;
import com.example.incubator.backend.repo.ExpenseRepository;
import com.example.incubator.backend.service.dto.ExpenseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService extends BiDataService<ExpenseEntity, ExpenseDto> {
    private final ExpenseRepository expenseRepository;

    public ExpenseRepository getRepository() {
        return expenseRepository;
    }

    public void updateEntity(ExpenseEntity expenseEntity, ExpenseDto expenseDto) {
        expenseEntity.setMarketing(expenseDto.getMarketing());
        expenseEntity.setPayroll(expenseDto.getPayroll());
        expenseEntity.setEquipment(expenseDto.getEquipment());
        expenseEntity.setUtilities(expenseDto.getUtilities());
        expenseEntity.setMaterial(expenseDto.getMaterial());
        expenseEntity.setInsurance(expenseDto.getInsurance());
    }

    public ExpenseEntity createEntityFromDto(ExpenseDto expenseDto) {
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setMarketing(expenseDto.getMarketing());
        expenseEntity.setPayroll(expenseDto.getPayroll());
        expenseEntity.setEquipment(expenseDto.getEquipment());
        expenseEntity.setUtilities(expenseDto.getUtilities());
        expenseEntity.setMaterial(expenseDto.getMaterial());
        expenseEntity.setInsurance(expenseDto.getInsurance());

        return expenseEntity;
    }

    public ExpenseDto convertEntityIntoDto(ExpenseEntity expenseEntity) {
        return new ExpenseDto()
                .setId(expenseEntity.getId())
                .setProject(IncubatorProjectService.convertIncubatorProjectEntity(expenseEntity.getProjectEntity()))
                .setMarketing(expenseEntity.getMarketing())
                .setEquipment(expenseEntity.getEquipment())
                .setPayroll(expenseEntity.getPayroll())
                .setUtilities(expenseEntity.getUtilities())
                .setMaterial(expenseEntity.getMaterial())
                .setInsurance(expenseEntity.getInsurance());
    }
}
