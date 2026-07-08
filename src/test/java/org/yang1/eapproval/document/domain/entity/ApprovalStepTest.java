package org.yang1.eapproval.document.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yang1.eapproval.document.domain.status.ApprovalStepStatus;
import org.yang1.eapproval.user.domain.entity.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;


class ApprovalStepTest {



    @Test
    @DisplayName("결재 승인 성공")
    void 결재_승인_성공() {
        // given
        User approver = mock(User.class);
        given(approver.getId()).willReturn(1L);

        ApprovalStep step = ApprovalStep.create(approver, 1, "휴가 기안 결재 부탁드립니다.");
        step.stepSubmit();

        // when
        step.approve(1L, "결재 진행하겠습니다.");

        // then
        assertThat(step.getStepStatus()).isEqualTo(ApprovalStepStatus.APPROVED);
        assertThat(step.getActedAt()).isNotNull();
        assertThat(step.getCommentText()).isEqualTo("결재 진행하겠습니다.");
    }



    @Test
    @DisplayName("결재 승인 시 나의 결재할 차례가 아니라면(WATITNG) 예외가 발생해야 한다")
    void 단계가_결재대기_상태가_아니면_예외() {
        // given
        User approver = mock(User.class);

        ApprovalStep step = ApprovalStep.create(approver, 1, "결재 해주세요");

        // when & then
        assertThatThrownBy(() -> step.approve(1L, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결재할 차례의 단계가 아닙니다.");
    }


    @Test
    @DisplayName("결재 승인할 문서에 대한 담당 결재자가 아닌 경우 예외가 발생해야 한다")
    void 승인_시_담당_문서의_결재자가_아니라면_예외() {
        // given
        User originApprover = mock(User.class);
        given(originApprover.getId()).willReturn(999L);

        User diffApprover = mock(User.class);
        given(diffApprover.getId()).willReturn(1L);

        ApprovalStep step = ApprovalStep.create(originApprover, 1, "결재 승인 바랍니다.");
        step.stepSubmit();

        // when & then
        assertThatThrownBy(() -> step.approve(diffApprover.getId(), "문서 결재 라인에 포함되지 않았던 결재자"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 단계의 결재자가 아닙니다.");
    }

}