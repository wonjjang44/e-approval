package org.yang1.eapproval.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yang1.eapproval.department.domain.entity.Department;
import org.yang1.eapproval.department.domain.repository.DepartmentRepository;
import org.yang1.eapproval.department.exception.DepartmentNotFoundException;
import org.yang1.eapproval.user.application.command.UserSaveCommand;
import org.yang1.eapproval.user.domain.entity.User;
import org.yang1.eapproval.user.domain.repository.UserRepository;
import org.yang1.eapproval.user.exception.DuplicateUserLoginIdException;
import org.yang1.eapproval.user.exception.UserNotFoundException;
import org.yang1.eapproval.user.presentation.dto.response.UserResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    private final DepartmentRepository departmentRepository;



    /**
     * 사용자 단건 조회
     *
     * @param userId
     * @return
     */
    public UserResponse findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        return UserResponse.from(user);
    }


    /**
     * 사용자 등록
     *
     * @param command
     * @return
     */
    @Transactional
    public UserResponse saveUser(UserSaveCommand command) {
        Department department = null;

        if(userRepository.existsByLoginId(command.getLoginId()))
            throw new DuplicateUserLoginIdException("이미 존재하는 로그인 아이디 입니다.");


        if(command.getDepartmentId() != null) {
            department = departmentRepository.findById(command.getDepartmentId())
                    .orElseThrow(() -> new DepartmentNotFoundException("부서를 찾을 수 없습니다."));
        }

        User user = User.create(
                command.getLoginId(),
                command.getPassword(),
                command.getUserName(),
                command.getEmail(),
                department,
                command.getRole(),
                command.getPositionName()
        );

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

}
