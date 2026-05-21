package org.yang1.eapproval.user.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yang1.eapproval.department.domain.entity.Department;
import org.yang1.eapproval.user.domain.status.UserRole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(user.getPositionName()).isEqualTo(positionName);

        assertThat(user.getDepartment()).isSameAs(department);

        assertThat(user.getRole()).isEqualTo(UserRole.USER);
        assertThat(user.getIsActive()).isTrue();
    }
    
    
    @Test
    @DisplayName("로그인 ID 누락 시 예외가 발생해야 한다")
    void 로그인ID_누락_예외_발생() {
        // given
        String loginId = "";

        // then
        assertThatThrownBy(() -> User.createUser(loginId, "password", "userName", "", null, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("로그인 ID는 필수값 입니다.");
    }


    @Test
    @DisplayName("로그인 ID가 50자라면 회원 생성에 성공해야 한다")
    void 로그인ID_길이_50자_회원_등록() {
        // given
        String loginId = "a".repeat(50);

        // when
        User user = User.createUser(loginId, "password", "userName", "", null, "");

        // then
        assertThat(user.getLoginId()).isEqualTo(loginId);
    }


    @Test
    @DisplayName("로그인 ID가 50자를 초과한다면 예외가 발생해야 한다")
    void 로그인ID_50자_초과_예외_발생() {
        // given
        String loginId = "t".repeat(51);

        // then
        assertThatThrownBy(() -> User.createUser(loginId, "12", "test", "", null, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("로그인 ID는 50자를 초과할 수 없습니다.");
    }


    @Test
    @DisplayName("비밀번호 누락 시 예외가 발생해야 한다")
    void 비밀번호_누락_예외_발생() {
        // given
        String password = "";

        // then
        assertThatThrownBy(() -> User.createUser("test", password, "test1", "", null, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 필수값 입니다.");
    }
    
    
    @Test
    @DisplayName("비밀번호가 255자라면 회원 생성에 성공해야 한다")
    void 비밀번호_길이_255자_회원_등록() {
        // given
        String password = "1".repeat(255);
        
        // when
        User user = User.createUser("1", password, "2", "", null, "");

        // then
        assertThat(user.getPassword()).isEqualTo(password);
    }


    @Test
    @DisplayName("비밀번호가 255자를 초과한다면 예외가 발생해야 한다")
    void 비밀번호_255자_초과_예외_발생() {
        // given
        String password = "1".repeat(256);

        // then
        assertThatThrownBy(() -> User.createUser("1", password, "2", "", null, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 255자를 초과할 수 없습니다.");
    }
    
    
    @Test
    @DisplayName("사용자명 누락 시 예외가 발생해야 한다")
    void 사용자명_누락_예외_발생() {
        // given
        String userName = "";
        
        // then
        assertThatThrownBy(() -> User.createUser("test", "1234", userName, "", null, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자명은 필수값 입니다.");
    }
    
    
    @Test
    @DisplayName("사용자명이 100자라면 회원 생성에 성공해야 한다")
    void 사용자명_길이_100자_회원_등록() {
        // given
        String userName = "t".repeat(100);
        
        // when
        User user = User.createUser("test", "123,", userName, "", null, "");

        // then
        assertThat(user.getUserName()).isEqualTo(userName);
    }


    @Test
    @DisplayName("사용자명이 100자를 초과한다면 예외가 발생해야 한다")
    void 사용자명_길이_100자_초과_예외_발생() {
        // given
        String userName = "t".repeat(101);

        // then
        assertThatThrownBy(() -> User.createUser("test", "1234", userName, "", null, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자명은 100자를 초과할 수 없습니다.");

    }
}