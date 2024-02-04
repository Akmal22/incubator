package kz.incubator.backend.entity.data;

import jakarta.persistence.Column;
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
@jakarta.persistence.Entity
@Table(name = "REVENUE")
public class RevenueEntity implements Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revenueSequence")
    @SequenceGenerator(name = "revenueSequence", sequenceName = "SEQ_REVENUE", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private IncubatorProjectEntity projectEntity;

    @Column(name = "lease_revenue", nullable = false)
    private Double leaseRevenue;

    @Column(name = "services_revenue", nullable = false)
    private Double serviceRevenue;

    @Column(name = "sponsorship_revenue", nullable = false)
    private Double sponsorshipRevenue;

    @Column(name = "grant_revenue", nullable = false)
    private Double grantRevenue;
}
