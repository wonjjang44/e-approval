package org.yang1.eapproval.department.presentation.api.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.yang1.eapproval.department.domain.entity.Department;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class DepartmentResponseTest {


    @Test
    @DisplayName("Entity -> DTO 변환 시 상위 부서를 등록한다면 parentId 값이 null이여야 한다")
    void 상위_부서_변환_시_parentId_값은_null() {
        // given
        String departmentName = "상위 부서";

        Department parent = Department.createParent(departmentName, true);

        // when
        DepartmentResponse from = DepartmentResponse.from(parent);

        // then
        assertThat(from.getDepartmentName()).isEqualTo("상위 부서");
        assertThat(from.isActive()).isTrue();
        assertThat(from.getParentId()).isNull();
    }


    @Test
    @DisplayName("Entity -> DTO 변환 시 하위 부서 등록 시 parentId 값이 null이 아니여야 한다")
    void 하위_부서_변환_시_parentId_값은_null이_아님() {
        // given
        String departmentName = "하위 부서";
        Department parent = BDDMockito.mock(Department.class);

        // stubbing
        given(parent.getId()).willReturn(1L);

        Department child = Department.createChild(departmentName, parent, false);

        // when
        DepartmentResponse from = DepartmentResponse.from(child);

        // then
        assertThat(from.getDepartmentName()).isEqualTo("하위 부서");
        assertThat(from.isActive()).isFalse();
        assertThat(from.getParentId()).isNotNull();
        assertThat(from.getParentId()).isEqualTo(1L);
    }

}