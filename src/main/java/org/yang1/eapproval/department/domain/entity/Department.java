package org.yang1.eapproval.department.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yang1.eapproval.common.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "departments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Department extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    @ToString.Exclude
    private Department parent;

    @OneToMany(mappedBy="parent")
    @ToString.Exclude
    private List<Department> children = new ArrayList<>();

    @Column(name="is_active", nullable = false)
    private boolean isActive = true;


    @Builder
    private Department(String name, Boolean isActive) {
        this.name = Objects.requireNonNull(name);
        this.isActive = isActive == null || isActive;
    }


    /**
     * 최상위 부서 생성
     *
     * @param name 부서명
     *
     * @return Department
     */
    public static Department createBasicDepartment(String name) {
        return Department.builder()
                .name(name)
                .isActive(true)
                .build();
    }


}
