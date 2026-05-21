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
    private User(String loginId, String password, String userName, String email, String positionName) {
        this.loginId = loginId;
        this.password = password;
        this.userName = userName;
        this.email = email;
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
     * @param department 부서
     * @param positionName 직위명
     *
     * @return User
     */
    public static User createUser(String loginId, String password, String userName, String email, Department department, String positionName) {
        validateLoginId(loginId);
        validatePassword(password);
        validateUserName(userName);

        User user = User.builder()
                .loginId(loginId)
                .password(password)
                .userName(userName)
                .email(email)
                .positionName(positionName)
                .build();

        // 부서 세팅
        user.addDepartment(department);

        return user;
    }


    /**
     * 부서 할당
     *
     * @param department
     */
    public void addDepartment(Department department) {
        this.department = department;
    }


    private static void validateLoginId(String loginId) {
        if(loginId == null || loginId.isBlank()) throw new IllegalArgumentException("로그인 ID는 필수값 입니다.");
        if(loginId.length() > 50) throw new IllegalArgumentException("로그인 ID는 50자를 초과할 수 없습니다.");
    }

    private static void validatePassword(String password) {
        if(password == null || password.isBlank()) throw new IllegalArgumentException("비밀번호는 필수값 입니다.");
        if(password.length() > 255) throw new IllegalArgumentException("비밀번호는 255자를 초과할 수 없습니다.");
    }

    private static void validateUserName(String userName) {
        if(userName == null || userName.isBlank()) throw new IllegalArgumentException("사용자명은 필수값 입니다.");
        if(userName.length() > 100) throw new IllegalArgumentException("사용자명은 100자를 초과할 수 없습니다.");
    }
}
