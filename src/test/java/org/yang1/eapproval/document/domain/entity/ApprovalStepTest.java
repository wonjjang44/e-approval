package org.yang1.eapproval.document.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.yang1.eapproval.document.domain.status.ApprovalStepStatus;
import org.yang1.eapproval.user.domain.entity.User;

import static org.assertj.core.api.Assertions.*;

class ApprovalStepTest {


    @Test
    @DisplayName("결재 단계를 생성하면 결재 순번, 결재자, 최초 결재 상태 WAITING이 세팅돼야 한다")
    void 결재_단계_생성() {
        // given
        User approver = Mockito.mock(User.class);

        // when
        ApprovalStep step = ApprovalStep.createApprovalStep(1, approver);

        // then
        assertThat(step.getStepOrder()).isEqualTo(1);
        assertThat(step.getApprover()).isSameAs(approver);
        assertThat(step.getStepStatus()).isEqualTo(ApprovalStepStatus.WAITING);
    }


    @Test
    @DisplayName("결재 순서가 1보다 적다면 예외가 발생해야 한다")
    void 결재_단계_순서가_1보다_작다() {
        // given
        User approver = Mockito.mock(User.class);

        // when
//        ApprovalStep step = ApprovalStep.createApprovalStep(0, approver);

        // then
        assertThatThrownBy(() -> ApprovalStep.createApprovalStep(0, approver))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결재 순서는 1부터 시작돼야 합니다.");
    }


    @Test
    @DisplayName("결재자가 null이라면 예외가 발생해야 한다")
    void 결재자_null() {
        assertThatThrownBy(() -> ApprovalStep.createApprovalStep(1, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결재자는 필수로 입력돼야 합니다.");

    }
}