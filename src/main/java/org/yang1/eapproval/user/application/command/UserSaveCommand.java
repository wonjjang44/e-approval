package org.yang1.eapproval.user.application.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.yang1.eapproval.user.domain.status.UserRole;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSaveCommand {

    private String loginId;
    private String password;
    private String userName;
    private String email;
    private Long departmentId;
    private UserRole role;
    private String positionName;



    public static UserSaveCommand of(String loginId, String password, String userName, String email, Long departmentId, UserRole role, String positionName) {
        return new UserSaveCommand(
                loginId,
                password,
                userName,
                email,
                departmentId,
                role,
                positionName
        );
    }

}
