package org.yang1.eapproval.user.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.yang1.eapproval.user.domain.entity.User;
import org.yang1.eapproval.user.domain.status.UserRole;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String loginId;
    private String userName;
    private String email;
    private Long departmentId;
    private UserRole role;
    private String positionName;
    private boolean isActive;



    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getLoginId(),
                user.getUserName(),
                user.getEmail(),
                user.getDepartment() == null ? null : user.getDepartment().getId(),
                user.getRole(),
                user.getPositionName(),
                user.isActive());
    }

}
