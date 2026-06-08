package org.yang1.eapproval.department.presentation.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.department.application.service.command.DepartmentSaveCommand;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentSaveRequest {

    @NotBlank
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
