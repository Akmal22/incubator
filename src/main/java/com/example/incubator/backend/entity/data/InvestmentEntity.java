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
@Table(name = "INVESTMENT")
public class InvestmentEntity implements BaseDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "investmentSequence")
    @SequenceGenerator(name = "investmentSequence", sequenceName = "SEQ_INVESTMENT", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private IncubatorProjectEntity projectEntity;

    @Column(name = "investors_count", nullable = false)
    private Integer investorsCount;

    @Column(name = "percentage_of_invested_clients", nullable = false)
    private Integer percentageOfInvestedClients;
}
