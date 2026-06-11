package org.yang1.eapproval.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.common.entity.BaseEntity;
import org.yang1.eapproval.department.domain.entity.Department;
import org.yang1.eapproval.user.domain.status.UserRole;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String userName;

    @Column(length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    @Column(length = 50)
    private String positionName;

    @Column(nullable = false)
    private boolean isActive;


    @Builder(access = AccessLevel.PRIVATE)
    private User(String loginId, String password, String userName, String email, Department department, UserRole role, String positionName) {
        this.loginId = loginId;
        this.password = password;
        this.userName = userName;
        this.email = email;
        this.department = department;
        this.role = role;
        this.positionName = positionName;
        this.isActive = true;
    }


    public static User create(String loginId, String password, String userName, String email, Department department, UserRole role, String positionName) {
        if(loginId == null || loginId.isBlank()) throw new IllegalArgumentException("로그인 ID는 반드시 존재해야 합니다.");
        if(password == null || password.isBlank()) throw new IllegalArgumentException("비밀번호는 반드시 존재해야 합니다.");
        if(userName == null || userName.isBlank()) throw new IllegalArgumentException("사용자 이름은 반드시 존재해야 합니다.");
        if(role == null) throw new IllegalArgumentException("권한은 반드시 존재해야 합니다.");

        return User.builder()
                .loginId(loginId)
                .password(password)
                .userName(userName)
                .email(email)
                .department(department)
                .role(role)
                .positionName(positionName)
                .build();
    }

}
