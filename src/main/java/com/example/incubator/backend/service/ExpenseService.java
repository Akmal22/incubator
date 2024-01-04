package com.example.incubator.backend.service;

import com.example.incubator.backend.entity.data.ExpenseEntity;
import com.example.incubator.backend.entity.data.IncubatorProjectEntity;
import com.example.incubator.backend.repo.ExpenseRepository;
import com.example.incubator.backend.repo.IncubatorProjectRepository;
import com.example.incubator.backend.service.dto.ExpenseDto;
import com.example.incubator.backend.service.dto.ServiceResult;
import com.example.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final IncubatorProjectRepository incubatorProjectRepository;

    public List<ExpenseDto> getAllExpenseDtoByFilterText(String filterText) {
        return expenseRepository.findAllByProjectName(filterText).stream()
                .map(ExpenseService::convertExpenseDto)
                .collect(Collectors.toList());
    }

    public ServiceResult deleteExpense(ExpenseDto expenseDto) {
        ExpenseEntity expenseEntity = expenseRepository.findById(expenseDto.getId())
                .orElseThrow(() -> {
                    log.error("Expenses information with id [{}] not found", expenseDto.getId());
                    throw new IllegalArgumentException("Expense info not found");
                });

        expenseRepository.delete(expenseEntity);

        return new ServiceResult(true, null);
    }

    public ServiceResult createExpense(ExpenseDto expenseDto) {
        IncubatorProjectDto project = expenseDto.getProject();
        IncubatorProjectEntity incubatorProjectEntity = incubatorProjectRepository.findById(project.getId())
                .orElseThrow(() -> {
                    log.error("Error while saving expense info. No project with id [{}]", project.getId());
                    throw new IllegalArgumentException("No project found");
                });
        Optional<ExpenseEntity> optionalExpenseEntity = expenseRepository.findByProjectEntity(incubatorProjectEntity);
        if (optionalExpenseEntity.isPresent()) {
            log.error("Error while creating expense info, expense info data for project [{}] already exists", incubatorProjectEntity.getName());
            return new ServiceResult(false, "Expense info for specified project already exists");
        }
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setProjectEntity(incubatorProjectEntity);
        expenseEntity.setMarketing(expenseDto.getMarketing());
        expenseEntity.setPayroll(expenseDto.getPayroll());
        expenseEntity.setEquipment(expenseDto.getEquipment());
        expenseEntity.setUtilities(expenseDto.getUtilities());
        expenseEntity.setMaterial(expenseDto.getMaterial());
        expenseEntity.setInsurance(expenseDto.getInsurance());

        expenseRepository.save(expenseEntity);
        return new ServiceResult(true, null);
    }

    public ServiceResult updateExpense(ExpenseDto expenseDto) {
        ExpenseEntity expenseEntity = expenseRepository.findById(expenseDto.getId())
                .orElseThrow(() -> {
                    log.error("Expense information with id [{}] not found", expenseDto.getId());
                    throw new IllegalArgumentException("Expense info not found");
                });

        IncubatorProjectDto project = expenseDto.getProject();
        IncubatorProjectEntity incubatorProjectEntity = incubatorProjectRepository.findById(project.getId())
                .orElseThrow(() -> {
                    log.error("Error while saving expense info. No project with id [{}]", project.getId());
                    throw new IllegalArgumentException("No project found");
                });

        Optional<ExpenseEntity> optionalExpenseEntity = expenseRepository.findByProjectEntity(incubatorProjectEntity);
        if (optionalExpenseEntity.isPresent() && !optionalExpenseEntity.get().getId().equals(expenseEntity.getId())) {
            log.error("Error while updating projects expense info. Project [{}] already has expense info entity", project.getName());
            return new ServiceResult(false, String.format("Project [%s] already has expense info entity", project.getName()));
        }

        expenseEntity.setProjectEntity(incubatorProjectEntity);
        expenseEntity.setMarketing(expenseDto.getMarketing());
        expenseEntity.setPayroll(expenseDto.getPayroll());
        expenseEntity.setEquipment(expenseDto.getEquipment());
        expenseEntity.setUtilities(expenseDto.getUtilities());
        expenseEntity.setMaterial(expenseDto.getMaterial());
        expenseEntity.setInsurance(expenseDto.getInsurance());

        expenseRepository.save(expenseEntity);

        return new ServiceResult(true, null);
    }

    public static ExpenseDto convertExpenseDto(ExpenseEntity expenseEntity) {
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
