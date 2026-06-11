package org.yang1.eapproval.user.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yang1.eapproval.department.domain.entity.Department;
import org.yang1.eapproval.department.domain.repository.DepartmentRepository;
import org.yang1.eapproval.department.exception.DepartmentNotFoundException;
import org.yang1.eapproval.user.application.command.UserSaveCommand;
import org.yang1.eapproval.user.domain.entity.User;
import org.yang1.eapproval.user.domain.repository.UserRepository;
import org.yang1.eapproval.user.domain.status.UserRole;
import org.yang1.eapproval.user.exception.DuplicateUserLoginIdException;
import org.yang1.eapproval.user.exception.UserNotFoundException;
import org.yang1.eapproval.user.presentation.dto.response.UserResponse;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    DepartmentRepository departmentRepository;

    @InjectMocks
    UserService userService;



    @Nested
    @DisplayName("사용자 조회 테스트")
    class GetUserTests {

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

        
        @Test
        @DisplayName("사용자 전체 조회 결과가 있을 경우")
        void 사용자_전체_데이터_존재() {
            // given
            User user1 = mock(User.class);
            given(user1.getId()).willReturn(1L);

            User user2 = mock(User.class);
            given(user2.getId()).willReturn(2L);

            given(userRepository.findAll()).willReturn(List.of(user1, user2));

            // when
            List<UserResponse> userList = userService.findAllUsers();

            // then
            assertThat(userList).hasSize(2);
            assertThat(userList)
                    .extracting(UserResponse::getId)
                    .containsExactly(1L, 2L);
        }
        

        @Test
        @DisplayName("사용자 전체 조회 결과가 없을 경우")
        void 사용자_전체_데이터_없음() {
            // given
            given(userRepository.findAll()).willReturn(List.of());

            // when
            List<UserResponse> userList = userService.findAllUsers();

            // then
            assertThat(userList).isEmpty();
        }
    }


    @Nested
    @DisplayName("사용자 생성 테스트")
    class SaveUserTests {

        @Test
        @DisplayName("사용자 생성 - 소속 부서가 없는 경우")
        void 사용자_생성시_부서X() {
            // given
            UserSaveCommand command = UserSaveCommand.of(
                    "2yang1",
                    "123456",
                    "이양원",
                    "test123@test.or.kr",
                    null,
                    UserRole.ADMIN,
                    "팀장"
            );

            given(userRepository.existsByLoginId(command.getLoginId())).willReturn(false);
            given(userRepository.save(any(User.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // 여기서 굳이 department 까지 stubbing 해줘야 하는 것은 아니지? 그러니까 아래 주석처럼 말이야.
            // given(departmentRepository.findById(1L)).willReturn(Optional.empth());
            // 해당 테스트 메서드는 부서가 없다고 가정하니까 말이야.

            // when
            UserResponse savedUser = userService.saveUser(command);

            // then
            assertThat(savedUser.getLoginId()).isEqualTo("2yang1");
            assertThat(savedUser.getUserName()).isEqualTo("이양원");
            assertThat(savedUser.getEmail()).isEqualTo("test123@test.or.kr");

            assertThat(savedUser.getDepartmentId()).isNull();
            assertThat(savedUser.getRole()).isEqualTo(UserRole.ADMIN);
            assertThat(savedUser.getPositionName()).isEqualTo("팀장");
            assertThat(savedUser.isActive()).isTrue();
        }


        @Test
        @DisplayName("사용자 생성 - 소속 부서 존재함")
        void 사용자_생성시_부서O() {
            // given
            Department department = mock(Department.class);
            given(department.getId()).willReturn(1L);

            UserSaveCommand command = UserSaveCommand.of(
                    "yang11",
                    "1111",
                    "2양1",
                    "test666@test.net",
                    department.getId(),
                    UserRole.USER,
                    "팀원"
            );

            given(userRepository.existsByLoginId(command.getLoginId())).willReturn(false);
            given(departmentRepository.findById(command.getDepartmentId())).willReturn(Optional.of(department));

            given(userRepository.save(any(User.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            UserResponse savedUser = userService.saveUser(command);

            // then
            assertThat(savedUser.getLoginId()).isEqualTo("yang11");
            assertThat(savedUser.getUserName()).isEqualTo("2양1");
            assertThat(savedUser.getEmail()).isEqualTo("test666@test.net");

            assertThat(savedUser.getDepartmentId()).isEqualTo(1L);
            assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
            assertThat(savedUser.getPositionName()).isEqualTo("팀원");
            assertThat(savedUser.isActive()).isTrue();
        }


        @Test
        @DisplayName("사용자 등록 시 로그인 아이디가 중복되면 예외가 발생해야 한다")
        void loginId가_중복된다면_예외() {
            // given
            String loginId = "yang1123";
            UserSaveCommand command = UserSaveCommand.of(loginId, "", "", "", null, UserRole.USER, "");

            given(userRepository.existsByLoginId(loginId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.saveUser(command))
                    .isInstanceOf(DuplicateUserLoginIdException.class)
                    .hasMessage("이미 존재하는 로그인 아이디 입니다.");
        }


        @Test
        @DisplayName("존재하지 않는 부서로 생성 시 예외가 발생해야 한다")
        void 부서가_존재하지_않으면_예외() {
            // given
            UserSaveCommand command = UserSaveCommand.of("yang1", "1234", "이름", null, 999L, UserRole.USER, null);
            given(userRepository.existsByLoginId("yang1")).willReturn(false);
            given(departmentRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.saveUser(command))
                    .isInstanceOf(DepartmentNotFoundException.class)
                    .hasMessage("부서를 찾을 수 없습니다.");
        }

    }

}