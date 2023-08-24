package com.example.incubator.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

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
}
