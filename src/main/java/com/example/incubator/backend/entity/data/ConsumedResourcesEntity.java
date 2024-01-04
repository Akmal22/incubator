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
@Table(name = "CONSUMED_RESOURCES")
public class ConsumedResourcesEntity implements BaseDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resourcesSequence")
    @SequenceGenerator(name = "resourcesSequence", sequenceName = "SEQ_RESOURCES", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private IncubatorProjectEntity projectEntity;

    @Column(name = "involved_managers", nullable = false)
    private Integer involvedManagers;

    @Column(name = "involved_coaches", nullable = false)
    private Integer involvedCoaches;

    @Column(name = "involved_mentors", nullable = false)
    private Integer involvedMentors;

    @Column(name = "used_services", nullable = false)
    private Integer usedServices;

    @Column(name = "rent_space", nullable = false)
    private Double rentSpace;
}
