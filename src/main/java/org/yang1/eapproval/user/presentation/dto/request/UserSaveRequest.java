package org.yang1.eapproval.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.user.application.command.UserSaveCommand;
import org.yang1.eapproval.user.domain.status.UserRole;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSaveRequest {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    @NotBlank
    private String userName;

    private String email;
    private Long departmentId;

    @NotNull
    private UserRole role;

    private String positionName;



    public UserSaveCommand toCommand() {
        return UserSaveCommand.of(
                this.loginId,
                this.password,
                this.userName,
                this.email,
                this.departmentId,
                this.role,
                this.positionName
        );
    }

}
