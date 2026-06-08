package org.yang1.eapproval.department.presentation.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.department.domain.entity.Department;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {

    private Long id;
    private String departmentName;
    private Long parentId;

    private boolean isActive;



    /**
     * Entity -> Response
     *
     * @param department Entity
     * @return Response
     */
    public static DepartmentResponse from(Department department) {
        Long parentId = (department.getParent() != null) ? department.getParent().getId() : null;

        return new DepartmentResponse(
                department.getId(),
                department.getDepartmentName(),
                parentId,
                department.isActive()
        );
    }

}
