package org.yang1.eapproval.department.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yang1.eapproval.department.domain.entity.Department;
import org.yang1.eapproval.department.domain.repository.DepartmentRepository;
import org.yang1.eapproval.department.exception.DepartmentNotFoundException;
import org.yang1.eapproval.department.presentation.api.dto.response.DepartmentResponse;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;



    @Test
    @DisplayName("부서 단건을 조회한다")
    void 부서_단건_조회() {
        // given
        Long departmentId = 1L;

        Department parent = mock(Department.class);
        Department department = mock(Department.class);

        given(department.getId()).willReturn(departmentId);
        given(department.getDepartmentName()).willReturn("Stubbing");
        given(department.isActive()).willReturn(true);
        given(department.getParent()).willReturn(parent);
        given(parent.getId()).willReturn(9999L);

        given(departmentRepository.findById(departmentId)).willReturn(Optional.of(department));

        // when
        DepartmentResponse findDepartment = departmentService.findDepartmentById(departmentId);

        // then
        assertThat(findDepartment.getId()).isEqualTo(departmentId);
        assertThat(findDepartment.getDepartmentName()).isEqualTo("Stubbing");
        assertThat(findDepartment.isActive()).isTrue();
        assertThat(findDepartment.getParentId()).isNotNull();
        assertThat(findDepartment.getParentId()).isEqualTo(9999L);
    }


    @Test
    @DisplayName("부서가 존재하지 않는다면 예외가 발생해야 한다")
    void 부서가_없을_시_예외() {
        // given
        Long departmentId = 1L;

        given(departmentRepository.findById(departmentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> departmentService.findDepartmentById(departmentId))
                .isInstanceOf(DepartmentNotFoundException.class)
                .hasMessage("존재하지 않는 부서입니다.");
    }

}