package org.yang1.eapproval.department.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yang1.eapproval.common.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Department> children = new ArrayList<>();

    @Column(nullable = false)
    private Boolean isActive;



    @Builder(access = AccessLevel.PRIVATE)
    private Department(String departmentName, Department parent) {
        this.departmentName = departmentName;
        this.isActive = true;
    }


    /**
     * 상위 Department 생성
     *
     * @param departmentName 부서명
     *
     * @return Parent Department
     */
    public static Department createParent(String departmentName) {
        // 상위 부서명 검증
        validateDepartmentName(departmentName);

        return Department.builder()
                .departmentName(departmentName)
                .build();
    }


    /**
     * 하위 Department 생성
     *
     * 상위 부서 밑에 새 하위 부서를 생성하고,
     * parent.children / child.parent 양방향 관계를 세팅한다
     *
     * @param departmentName 부서명
     * @param parent 상위 부서
     *
     * @return Child Department
     */
    public static Department createChild(String departmentName, Department parent) {
        if(parent == null) throw new IllegalArgumentException("상위 부서는 null일 수 없습니다.");

        // 상위 부서명 검증
        validateDepartmentName(departmentName);

        Department child = Department.builder()
                .departmentName(departmentName)
                .build();

        // 연관관계 연결
        parent.addChild(child);

        return child;
    }


    /**
     * 연관관계 편의 메서드 호출
     *
     * 여기서 this는 parent
     *
     * @param child 하위 부서
     */
    public void addChild(Department child) {
        if(child == null) throw new IllegalArgumentException("하위 부서는 null일 수 없습니다.");
        if(child == this) throw new IllegalArgumentException("자기 자신을 하위 부서로 추가할 수 없습니다.");

        child.changeParent(this);
    }


    /**
     * 양방향 연관관계 세팅
     *
     * 현재 부서가 가지고 있는 부모 부서의 값을 세팅한다
     * 새 부모의 children 목록에 현재 자식 부서를 세팅한다
     *
     * 여기서 this는 child
     *
     * @param parent 상위 부서
     */
    private void changeParent(Department parent) {
        // 같은 부모 부서 체크(같은 부서면 종료)
        if(this.parent == parent) return ;

        // 현재 자식 부서가 예전에 소속되어 있던 부모 부서의 children 목록에서 현재 자식 부서를 제거한다
        if (this.parent != null)
            this.parent.children.remove(this);

        this.parent = parent;

        // 새 부모의 children 목록에 현재 자식 부서 세팅
        if(parent != null && !parent.children.contains(this))
            parent.children.add(this);
    }


    private static void validateDepartmentName(String departmentName) {
        if(departmentName == null || departmentName.isBlank()) throw new IllegalArgumentException("부서명은 필수값 입니다.");
        if(departmentName.length() > 100) throw new IllegalArgumentException("부서명은 100자를 초과할 수 없습니다.");
    }

}
