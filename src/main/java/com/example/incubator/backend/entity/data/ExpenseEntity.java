package com.example.incubator.backend.entity.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "EXPENSE")
public class ExpenseEntity implements BaseDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expenseSequence")
    @SequenceGenerator(name = "expenseSequence", sequenceName = "SEQ_EXPENSE", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private IncubatorProjectEntity projectEntity;

    @Column(name = "marketing", nullable = false)
    private Double marketing;

    @Column(name = "payroll", nullable = false)
    private Double payroll;

    @Column(name = "equipment", nullable = false)
    private Double equipment;

    @Column(name = "utilities", nullable = false)
    private Double utilities;

    @Column(name = "material", nullable = false)
    private Double material;

    @Column(name = "insurance", nullable = false)
    private Double insurance;
}
