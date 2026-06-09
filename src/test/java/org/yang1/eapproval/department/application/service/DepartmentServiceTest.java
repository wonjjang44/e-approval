package org.yang1.eapproval.department.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yang1.eapproval.department.application.service.command.DepartmentSaveCommand;
import org.yang1.eapproval.department.domain.entity.Department;
import org.yang1.eapproval.department.domain.repository.DepartmentRepository;
import org.yang1.eapproval.department.exception.DepartmentNotFoundException;
import org.yang1.eapproval.department.exception.DuplicateDepartmentNameException;
import org.yang1.eapproval.department.exception.ParentDepartmentNotFoundException;
import org.yang1.eapproval.department.presentation.api.dto.response.DepartmentResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;



    @Nested
    @DisplayName("부서 조회 테스트")
    class GetDepartmentTests {


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


        @Test
        void 부서_전체_조회() {
            // given
            Department department1 = mock(Department.class);
            given(department1.getDepartmentName()).willReturn("A");

            Department department2 = mock(Department.class);
            given(department2.getDepartmentName()).willReturn("B");

            given(departmentRepository.findAll()).willReturn(List.of(department1, department2));

            // when
            List<DepartmentResponse> departList = departmentService.findAllDepartments();

            // then
            assertThat(departList).hasSize(2);
            assertThat(departList)
                    .extracting(DepartmentResponse::getDepartmentName)
                    .containsExactly("A", "B");
        }


        @Test
        void 부서_전체_조회시_빈_리스트_리턴() {
            // given
            given(departmentRepository.findAll()).willReturn(List.of());

            // when
            List<DepartmentResponse> departList = departmentService.findAllDepartments();

            // then
            assertThat(departList).isEmpty();
        }
    }


    @Nested
    @DisplayName("부서 생성 테스트")
    class CreateDepartmentTests {

        @Test
        @DisplayName("부서 생성")
        void 상위_부서_생성() {
            // given
            String departmentName = "상위 부서 등록 테스트";

            DepartmentSaveCommand command = DepartmentSaveCommand.of(departmentName, null, true);

            given(departmentRepository.existsByDepartmentName(command.getDepartmentName())).willReturn(false);
            given(departmentRepository.save(any(Department.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            DepartmentResponse savedDepartment = departmentService.saveDepartment(command);

            // then
            assertThat(savedDepartment.getDepartmentName()).isEqualTo(departmentName);
            assertThat(savedDepartment.isActive()).isTrue();
            assertThat(savedDepartment.getParentId()).isNull();
        }


        @Test
        @DisplayName("하위 부서 생성")
        void 하위_부서_생성() {
            // given
            String departmentName = "하위 부서 test";

            Department parent = mock(Department.class);
            given(parent.getId()).willReturn(1L);
            given(departmentRepository.findById(1L)).willReturn(Optional.of(parent));

            DepartmentSaveCommand departmentCommand = DepartmentSaveCommand.of(departmentName, parent.getId(), false);

            given(departmentRepository.existsByDepartmentName(departmentName)).willReturn(false);
            given(departmentRepository.save(any(Department.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            DepartmentResponse savedDepartment = departmentService.saveDepartment(departmentCommand);

            // then
            assertThat(savedDepartment.getDepartmentName()).isEqualTo(departmentName);
            assertThat(savedDepartment.isActive()).isFalse();
            assertThat(savedDepartment.getParentId()).isNotNull();
            assertThat(savedDepartment.getParentId()).isEqualTo(1L);
        }


        @Test
        @DisplayName("부서 생성 - 부서명이 중복되면 예외가 발생해야 한다")
        void 부서명_중복시_예외() {
            // given
            String departmentName = "중복 테스트 - 부서명";
            DepartmentSaveCommand command = DepartmentSaveCommand.of(departmentName, null, true);

            given(departmentRepository.existsByDepartmentName(departmentName)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> departmentService.saveDepartment(command))
                    .isInstanceOf(DuplicateDepartmentNameException.class)
                    .hasMessage("이미 존재하는 부서명입니다.");
        }

        
        @Test
        @DisplayName("상위 부서 부재 시 예외가 발생해야 한다")
        void 상위부서_null() {
            // given
            DepartmentSaveCommand command = DepartmentSaveCommand.of("Test", 1L, true);
            given(departmentRepository.existsByDepartmentName(command.getDepartmentName())).willReturn(false);

            given(departmentRepository.findById(1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> departmentService.saveDepartment(command))
                    .isInstanceOf(ParentDepartmentNotFoundException.class)
                    .hasMessage("존재하지 않는 상위 부서입니다.");
        }
    }
}