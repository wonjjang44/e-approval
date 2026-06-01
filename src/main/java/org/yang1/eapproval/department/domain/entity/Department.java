package org.yang1.eapproval.department.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.common.entity.BaseEntity;

@Entity
@Table(name = "departments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Department extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String departmentName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Department parent;

    @Column(nullable = false)
    private boolean isActive;


    @Builder(access = AccessLevel.PRIVATE)
    private Department(String departmentName, Department parent, boolean isActive) {
        this.departmentName = departmentName;
        this.parent = parent;
        this.isActive = isActive;
    }



    public static Department createParent(String departmentName, boolean isActive) {
        return Department.builder()
                .departmentName(departmentName)
                .isActive(isActive)
                .build();
    }


    public static Department createChild(String departmentName, Department parent, boolean isActive) {
        return Department.builder()
                .departmentName(departmentName)
                .parent(parent)
                .isActive(isActive)
                .build();
    }



}
