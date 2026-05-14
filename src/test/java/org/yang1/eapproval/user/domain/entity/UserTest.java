package org.yang1.eapproval.user.domain.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yang1.eapproval.department.domain.entity.Department;
import org.yang1.eapproval.user.domain.status.UserRole;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    
    @Test
    @DisplayName("회원 생성")
    void 회원_생성() {
        // Arrange
        String loginId = "yang1";
        String password = "1111";
        String userName = "테스트";
        String email = "test12@test.com";
        String positionName = "대리";

        Department department = Department.createChild("개발부", Department.createParent("SI 사업부"));

        // Act
        User user = User.createUser(loginId, password, userName, email, department, positionName);

        // Assert
        assertThat(user.getLoginId()).isEqualTo(loginId);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getUserName()).isEqualTo(userName);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPositionName()).가(positionName);

        assertThat(user.getDepartment()).isSameAs(department);

        assertThat(user.getRole()).isEqualTo(UserRole.USER);
        assertThat(user.getIsActive()).isTrue();
    }
    
}