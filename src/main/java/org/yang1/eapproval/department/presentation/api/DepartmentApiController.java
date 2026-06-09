package org.yang1.eapproval.department.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yang1.eapproval.department.application.service.DepartmentService;
import org.yang1.eapproval.department.application.service.command.DepartmentSaveCommand;
import org.yang1.eapproval.department.presentation.api.dto.request.DepartmentSaveRequest;
import org.yang1.eapproval.department.presentation.api.dto.response.DepartmentResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class DepartmentApiController {

    private final DepartmentService departmentService;




    /**
     * 부서 한 건 조회
     *
     * @param departmentId
     * @return
     */
    @GetMapping("/departments/{departmentId}")
    public DepartmentResponse getDepartment(@PathVariable Long departmentId) {
        return departmentService.findDepartmentById(departmentId);
    }


    /**
     * 부서 등록
     *
     * @param request
     * @return
     */
    @PostMapping("/departments")
    public ResponseEntity<DepartmentResponse> saveDepartment(@RequestBody @Valid DepartmentSaveRequest request) {
        DepartmentSaveCommand command = request.toCommand();
        DepartmentResponse response = departmentService.saveDepartment(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
