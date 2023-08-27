package com.example.incubator.back.entity.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@Table(name = "INCUBATOR_PROJECT")
public class IncubatorProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "incubatorProjectSequence")
    @SequenceGenerator(name = "incubatorProjectSequence", sequenceName = "SEQ_INC_PROJECT", allocationSize = 1)
    private long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "INCUBATOR_ID", nullable = false)
    private IncubatorEntity incubator;
    @Column(name = "income", nullable = false)
    private Double income;
    @Column(name = "expenses", nullable = false)
    private Double expenses;
    @Column(name = "applications_count", nullable = false)
    private int residentApplications;
    @Column(name = "accepted_applications", nullable = false)
    private int acceptedResidentApplications;
    @Column(name = "graduated_residents_count", nullable = false)
    private int graduatedResidentsCount;
    @Column(name = "started_date", nullable = false)
    private LocalDate startedDate;
    @Column(name = "end_date")
    private LocalDate endDate;
}
