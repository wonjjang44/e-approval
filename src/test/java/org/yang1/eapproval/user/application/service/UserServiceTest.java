package org.yang1.eapproval.user.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yang1.eapproval.department.domain.entity.Department;
import org.yang1.eapproval.user.domain.entity.User;
import org.yang1.eapproval.user.domain.repository.UserRepository;
import org.yang1.eapproval.user.exception.UserNotFoundException;
import org.yang1.eapproval.user.presentation.dto.response.UserResponse;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;


    
    @Test
    @DisplayName("사용자 단건 조회")
    void 사용자_단건_조회() {
        // given
        Long userId = 1L;

        User user = mock(User.class);
        given(user.getId()).willReturn(userId);

        Department department = mock(Department.class);
        given(department.getId()).willReturn(1L);
        given(user.getDepartment()).willReturn(department);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        
        // when
        UserResponse findUser = userService.findUserById(userId);

        // then
        assertThat(findUser.getId()).isEqualTo(userId);
        assertThat(findUser.getDepartmentId()).isEqualTo(1L);
    }


    @Test
    @DisplayName("사용자가 존재하지 않는다면 예외가 발생해야 한다")
    void 사용자_단건_조회_결과가_없다면_예외() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findUserById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }
}