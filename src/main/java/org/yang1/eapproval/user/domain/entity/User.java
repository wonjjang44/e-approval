package org.yang1.eapproval.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yang1.eapproval.common.entity.BaseEntity;
import org.yang1.eapproval.department.domain.entity.Department;
import org.yang1.eapproval.user.domain.UserRole;

import java.util.Objects;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(name="password", nullable = false, length = 255)
    @ToString.Exclude
    private String password;

    @Column(name="name",  nullable = false, length = 100)
    private String name;

    @Column(name="email", length = 100)
    private String email;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="department_id")
    @ToString.Exclude
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false, length = 30)
    private UserRole role;

    @Column(name="position_name", length = 50)
    private String positionName;

    @Column(name="is_active", nullable = false)
    private boolean isActive;


    @Builder
    private User(String loginId, String password, String name, String email, UserRole role, String positionName, Boolean isActive) {
        this.loginId = Objects.requireNonNull(loginId);
        this.password = Objects.requireNonNull(password);
        this.name = Objects.requireNonNull(name);
        this.email = email;
        this.role = Objects.requireNonNull(role);
        this.positionName = positionName;
        this.isActive = isActive == null || isActive;
    }


    public static User createUser(String loginId, String password, String name, String email, UserRole role, String positionName) {
        return User.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .email(email)
                .role(role)
                .positionName(positionName)
                .isActive(true)
                .build();
    }


    public void changeDepartment(Department department) {
        this.department = department;
        department.getUsers().add(this);
    }
}
