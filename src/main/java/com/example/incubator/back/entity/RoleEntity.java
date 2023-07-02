package com.example.incubator.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ROLES")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleSequence")
    @SequenceGenerator(name = "roleSequence", sequenceName = "SEQ_ROLES", allocationSize = 1)
    private Long id;
    @Column(name = "NAME", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private Role name;
}
