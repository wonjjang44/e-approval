package org.yang1.eapproval.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
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
    private Boolean isActive;


    @Builder(access = AccessLevel.PRIVATE)
    private User(String loginId, String password, String userName, String email, Department department, String positionName) {
        this.loginId = loginId;
        this.password = password;
        this.userName = userName;
        this.email = email;
        this.department = department;
        this.role = UserRole.USER;
        this.positionName = positionName;
        this.isActive = true;
    }


    /**
     * User 생성
     *
     * @param loginId 로그인ID
     * @param password 패스워드
     * @param userName 사용자명
     * @param email 이메일
     * @param department 부서정보
     * @param positionName 직위명
     *
     * @return User
     */
    public static User createUser(String loginId, String password, String userName, String email, Department department, String positionName) {
        return User.builder()
                .loginId(loginId)
                .password(password)
                .userName(userName)
                .email(email)
                .department(department)
                .positionName(positionName)
                .build();
    }

}
