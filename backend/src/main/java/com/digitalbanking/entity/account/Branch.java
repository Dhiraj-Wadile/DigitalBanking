package com.digitalbanking.entity.account;

import com.digitalbanking.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "branches", indexes = {
    @Index(name = "idx_branch_code", columnList = "branch_code", unique = true)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Branch extends BaseEntity {

    @Column(nullable = false, unique = true, length = 10)
    private String branchCode;

    @Column(nullable = false, length = 200)
    private String branchName;

    @Column(length = 200)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 10)
    private String pincode;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(length = 10)
    private String ifscCode;
}
