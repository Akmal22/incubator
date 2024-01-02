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
@Table(name = "CLIENT")
public class ClientsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clientsSequence")
    @SequenceGenerator(name = "clientsSequence", sequenceName = "SEQ_CLIENT", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private IncubatorProjectEntity projectEntity;

    @Column(name = "applications", nullable = false)
    private Integer applications;

    @Column(name = "accepted", nullable = false)
    private Integer accepted;

    @Column(name = "graduated", nullable = false)
    private Integer graduated;

    @Column(name = "failed", nullable = false)
    private Integer failed;
}
