package org.yang1.eapproval.department.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yang1.eapproval.department.application.service.command.DepartmentSaveCommand;
import org.yang1.eapproval.department.domain.entity.Department;
import org.yang1.eapproval.department.domain.repository.DepartmentRepository;
import org.yang1.eapproval.department.exception.DepartmentNotFoundException;
import org.yang1.eapproval.department.exception.DuplicateDepartmentNameException;
import org.yang1.eapproval.department.exception.ParentDepartmentNotFoundException;
import org.yang1.eapproval.department.presentation.api.dto.response.DepartmentResponse;

import java.util.List;

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


    /**
     * 부서 등록
     *
     * @param command
     * @return
     */
    @Transactional
    public DepartmentResponse saveDepartment(DepartmentSaveCommand command) {
        if(departmentRepository.existsByDepartmentName(command.getDepartmentName()))
            throw new DuplicateDepartmentNameException("이미 존재하는 부서명입니다.");

        Department department;

        if(command.getParentId() != null) {
            Department parent = departmentRepository.findById(command.getParentId())
                    .orElseThrow(() -> new ParentDepartmentNotFoundException("존재하지 않는 상위 부서입니다."));

            department = Department.createChild(command.getDepartmentName(), parent, command.isActive());
        } else {
            department = Department.createParent(command.getDepartmentName(), command.isActive());
        }

        return DepartmentResponse.from(departmentRepository.save(department));
    }


    /**
     * 부서 전체 조회
     * - 페이징 및 검색 조건 적용 X
     *
     * @return
     */
    public List<DepartmentResponse> findAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(DepartmentResponse::from)
                .toList();
    }
}
