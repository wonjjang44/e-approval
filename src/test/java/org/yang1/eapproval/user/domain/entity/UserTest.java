package org.yang1.eapproval.user.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.yang1.eapproval.department.domain.entity.Department;
import org.yang1.eapproval.user.domain.status.UserRole;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UserTest {


    @Test
    @DisplayName("사용자를 생성한다 - 소속 부서가 존재하지 않을 경우")
    void 사용자_생성_소속부서X() {
        // given
        String loginId = "yang1-_-";
        String password = "1234";
        String userName = "이양1";
        String email = "test@test.com";
        Department department = null;
        UserRole role = UserRole.USER;
        String positionName = "팀원";

        // when
        User user = User.create(loginId, password, userName, email, department, role, positionName);

        // then
        assertThat(user.getLoginId()).isEqualTo(loginId);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getUserName()).isEqualTo(userName);
        assertThat(user.getEmail()).isEqualTo(email);

        assertThat(user.getDepartment()).isNull();

        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getPositionName()).isEqualTo(positionName);

        assertThat(user.isActive()).isTrue();
    }


    @Test
    @DisplayName("사용자를 생성한다 - 소속 부서가 존재할 경우")
    void 사용자_생성_소속부서O() {
        // given
        String loginId = "yang1-_-";
        String password = "1234";
        String userName = "이양1";
        String email = "test@test.com";
        UserRole role = UserRole.USER;
        String positionName = "팀원";

        Department department = mock(Department.class);
        BDDMockito.given(department.getId()).willReturn(1L);

        // when
        User user = User.create(loginId, password, userName, email, department, role, positionName);

        // then
        assertThat(user.getLoginId()).isEqualTo(loginId);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getUserName()).isEqualTo(userName);
        assertThat(user.getEmail()).isEqualTo(email);

        assertThat(user.getDepartment()).isNotNull();
        assertThat(user.getDepartment().getId()).isEqualTo(1L);

        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getPositionName()).isEqualTo(positionName);

        assertThat(user.isActive()).isTrue();
    }


    @Test
    @DisplayName("사용자 생성 시 로그인ID 값이 누락되면 예외가 발생해야 한다")
    void 로그인_아이디_값_누락시_예외() {
        // given
        String loginId = null;

        // when & then
        assertThatThrownBy(() -> User.create(loginId, "1234", "yang1", "", null, UserRole.USER, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("로그인 ID는 반드시 존재해야 합니다.");
    }


    @Test
    @DisplayName("사용자 생성 시 로그인ID 값이 공백이면 예외가 발생해야 한다")
    void 로그인_아이디_값_공백시_예외() {
        // given
        String loginId = " ";

        // when & then
        assertThatThrownBy(() -> User.create(loginId, "1234", "yang1", "", null, UserRole.USER, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("로그인 ID는 반드시 존재해야 합니다.");
    }


    @Test
    @DisplayName("비밀번호 누락 시 예외가 발생해야 한다")
    void 비밀번호_누락시_예외() {
        // given
        String password = null;

        // when & then
        assertThatThrownBy(() -> User.create("yang1", password, "양1", null, null, UserRole.USER, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 반드시 존재해야 합니다.");
    }


    @Test
    @DisplayName("비밀번호 값이 공백일 경우 예외가 발생해야 한다")
    void 비밀번호_공백_예외() {
        // given
        String password = " ";

        // when & then
        assertThatThrownBy(() -> User.create("yang1", password, "양1", null, null, UserRole.USER, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 반드시 존재해야 합니다.");
    }


    @Test
    @DisplayName("사용자 이름 누락 시 예외가 발생해야 한다")
    void 사용자명_누락시_예외() {
        // given
        String userName = null;

        // when & then
        assertThatThrownBy(() -> User.create("yang1", "1234", userName, null, null, UserRole.USER, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자 이름은 반드시 존재해야 합니다.");
    }


    @Test
    @DisplayName("사용자 이름 공백일 경우 예외가 발생해야 한다")
    void 사용자명_공백_예외() {
        // given
        String userName = " ";

        // when & then
        assertThatThrownBy(() -> User.create("yang1", "1234", userName, null, null, UserRole.USER, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자 이름은 반드시 존재해야 합니다.");
    }


    @Test
    @DisplayName("역할 누락 시 예외가 발생해야 한다")
    void 역할_누락시_예외() {
        // given
        UserRole role = null;

        // when & then
        assertThatThrownBy(() -> User.create("yang1", "1234", "ㅎㅇ", null, null, role, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("권한은 반드시 존재해야 합니다.");
    }

}