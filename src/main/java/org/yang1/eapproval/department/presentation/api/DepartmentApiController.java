package org.yang1.eapproval.department.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yang1.eapproval.common.response.ApiResult;
import org.yang1.eapproval.department.application.service.DepartmentService;
import org.yang1.eapproval.department.application.service.command.DepartmentSaveCommand;
import org.yang1.eapproval.department.presentation.api.dto.request.DepartmentSaveRequest;
import org.yang1.eapproval.department.presentation.api.dto.response.DepartmentResponse;

import java.util.List;

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
    public ResponseEntity<ApiResult<DepartmentResponse>> getDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(ApiResult.success("부서 조회 성공", departmentService.findDepartmentById(departmentId)));
    }


    /**
     * 부서 등록
     *
     * @param request
     * @return
     */
    @PostMapping("/departments")
    public ResponseEntity<ApiResult<DepartmentResponse>> createDepartment(@RequestBody @Valid DepartmentSaveRequest request) {
        DepartmentSaveCommand command = request.toCommand();
        DepartmentResponse response = departmentService.saveDepartment(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.success("부서 생성 성공", response));
    }


    /**
     * 부서 전체 조회
     *
     * @return
     */
    @GetMapping("/departments")
    public ResponseEntity<ApiResult<List<DepartmentResponse>>> getAllDepartments() {
        return ResponseEntity.ok(ApiResult.success("부서 전체 조회 성공", departmentService.findAllDepartments()));
    }
}
