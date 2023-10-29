package com.example.incubator.back.entity.data;

import com.example.incubator.back.entity.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "INCUBATOR")
public class IncubatorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "incubatorSequence")
    @SequenceGenerator(name = "incubatorSequence", sequenceName = "SEQ_INCUBATOR", allocationSize = 1)
    private long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MANAGER_ID", nullable = false)
    private UserEntity manager;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COUNTRY_ID", nullable = false)
    private CountryEntity country;
    @Column(name = "FOUNDED", nullable = false)
    private LocalDate founded;
    @Column(name = "FOUNDER", nullable = false)
    private String founder;
    @OneToMany(mappedBy = "incubator", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<IncubatorProjectEntity> incubatorProjects;
}
