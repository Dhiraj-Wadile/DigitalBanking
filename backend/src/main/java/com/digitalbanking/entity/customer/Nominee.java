package com.digitalbanking.entity.customer;

import com.digitalbanking.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nominees")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Nominee extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private NomineeRelation relation;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(nullable = false)
    private Double percentage;

    @Column(length = 200)
    private String address;

    @Column(length = 20)
    private String panNumber;

    public enum NomineeRelation {
        SPOUSE, CHILD, PARENT, SIBLING, OTHER
    }
}
