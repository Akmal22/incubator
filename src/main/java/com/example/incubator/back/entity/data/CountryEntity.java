package com.example.incubator.back.entity.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "COUNTRY")
public class CountryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "countrySequence")
    @SequenceGenerator(name = "countrySequence", sequenceName = "SEQ_COUNTRY", allocationSize = 1)
    private Long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private List<IncubatorEntity> incubators;
}
