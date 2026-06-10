package org.yang1.eapproval.user.presentation.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yang1.eapproval.department.domain.entity.Department;
import org.yang1.eapproval.user.domain.entity.User;
import org.yang1.eapproval.user.domain.status.UserRole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class UserResponseTest {
    
    
    @Test
    @DisplayName("User Entity -> DTO 변환 시 소속된 부서가 없다면 departmentId는 null이여야 한다")
    void 소속부서가_없다면_departmentId_값은_null() {
        // given
        User user = User.create("yang1", "1234", "2양1", "test@test.or.kr", null, UserRole.USER, "팀원");

        // when
        UserResponse response = UserResponse.from(user);

        // then
        assertThat(response.getLoginId()).isEqualTo("yang1");
        assertThat(response.getUserName()).isEqualTo("2양1");
        assertThat(response.getEmail()).isEqualTo("test@test.or.kr");
        assertThat(response.getDepartmentId()).isNull();
        assertThat(response.getRole()).isEqualTo(UserRole.USER);
        assertThat(response.getPositionName()).isEqualTo("팀원");

    }


    @Test
    @DisplayName("User Entity -> DTO 변환 시 부서가 있다면 departmentId가 존재해야 한다")
    void 소속부서_있다면_departmentId_값_존재() {
        // given
        Department department = mock(Department.class);
        given(department.getId()).willReturn(1L);

        User user = User.create("yang1-_-", "2222", "이양원", "test12@test.com", department, UserRole.ADMIN, "팀원");

        // when
        UserResponse response = UserResponse.from(user);

        // then
        assertThat(response.getLoginId()).isEqualTo("yang1-_-");
        assertThat(response.getUserName()).isEqualTo("이양원");
        assertThat(response.getEmail()).isEqualTo("test12@test.com");
        assertThat(response.getDepartmentId()).isNotNull();
        assertThat(response.getDepartmentId()).isEqualTo(1L);
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(response.getPositionName()).isEqualTo("팀원");
    }
    
}