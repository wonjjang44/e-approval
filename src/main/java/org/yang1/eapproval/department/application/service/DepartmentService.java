package org.yang1.eapproval.department.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yang1.eapproval.department.application.service.command.DepartmentSaveCommand;
import org.yang1.eapproval.department.domain.entity.Department;
import org.yang1.eapproval.department.domain.repository.DepartmentRepository;
import org.yang1.eapproval.department.exception.DepartmentNotFoundException;
import org.yang1.eapproval.department.presentation.api.dto.response.DepartmentResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DepartmentService {

    private final DepartmentRepository departmentRepository;



    /**
     * 부서 단건 조회
     *
     * @param departmentId
     *
     * @return DepartmentResponse DTO
     */
    public DepartmentResponse findDepartmentById(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException("존재하지 않는 부서입니다."));

        return DepartmentResponse.from(department);
    }

}
