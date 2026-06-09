package org.yang1.eapproval.department.presentation.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.department.application.service.command.DepartmentSaveCommand;

@Getter
@NoArgsConstructor
public class DepartmentSaveRequest {

    @NotBlank(message = "부서명은 null이거나 공백일 수 없습니다.")
    private String departmentName;

    private Long parentId;
    private boolean isActive;


    /**
     * Controller DTO를 Service Command로 변환한다
     *
     * @return DepartmentSaveCommand
     */
    public DepartmentSaveCommand toCommand() {
        return DepartmentSaveCommand.of(this.departmentName, this.parentId, this.isActive);
    }

}
