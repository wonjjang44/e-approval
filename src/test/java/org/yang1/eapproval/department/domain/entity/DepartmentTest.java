package org.yang1.eapproval.department.domain.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DepartmentTest {


    @Test
    @DisplayName("상위 및 하위 부서 생성")
    void 부서_생성() {
        // Arrange
        Department parentDepart = Department.createParent("SI 사업부");

        // Act
        Department childDepart1 = Department.createChild("개발부", parentDepart);
        Department childDepart2 = Department.createChild("영업부", parentDepart);

        // Assert
        assertThat(parentDepart.getDepartmentName()).isEqualTo("SI 사업부");
        assertThat(childDepart1.getDepartmentName()).isEqualTo("개발부");
        assertThat(childDepart2.getDepartmentName()).isEqualTo("영업부");

        assertThat(childDepart1.getParent()).isEqualTo(parentDepart);
        assertThat(childDepart2.getParent()).isEqualTo(parentDepart);

        assertThat(parentDepart.getChildren()).containsExactlyInAnyOrder(childDepart1, childDepart2);
    }


    @Test
    @DisplayName("상위 부서가 null이라면 IllegalArgumentException 예외가 터져야 한다")
    void 하위부서_null_예외() {
        assertThatThrownBy(() -> Department.createChild("하위 부서", null))
                .isInstanceOf(IllegalArgumentException.class);

    }

}