package org.yang1.eapproval.department.application.service.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DepartmentSaveCommand {

    private String departmentName;
    private Long parentId;
    private boolean isActive;



    public static DepartmentSaveCommand of(String departmentName, Long parentId, boolean isActive) {
        return new DepartmentSaveCommand(departmentName, parentId, isActive);
    }

}
