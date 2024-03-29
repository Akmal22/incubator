package com.example.incubator.backend.entity.data;

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

    @Column(name = "started_date", nullable = false)
    private LocalDate startedDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}
