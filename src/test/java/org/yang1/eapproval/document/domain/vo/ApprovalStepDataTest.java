package org.yang1.eapproval.document.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yang1.eapproval.user.domain.entity.User;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ApprovalStepDataTest {


    @Test
    @DisplayName("결재 단계 생성 시 결재자가 null이면 예외가 발생해야 한다")
    void 결재자가_null_인_경우_예외() {
        // given
        User approver = null;

        // when & then
        assertThatThrownBy(() -> ApprovalStepData.of(approver, 1, "testTest"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결재자는 필수값 입니다.");
    }


    @Test
    @DisplayName("결재 단계 생성 시 결재 순번이 1보다 작다면 예외가 발생해야 한다")
    void 결재단계가_1보다_작을_때_예외() {
        // given
        User approver = mock(User.class);

        // when & then
        assertThatThrownBy(() -> ApprovalStepData.of(approver, 0, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결재 순서는 1 이상이여야 합니다.");
    }

}