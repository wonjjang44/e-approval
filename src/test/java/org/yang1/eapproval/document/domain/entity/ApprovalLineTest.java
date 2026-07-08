package org.yang1.eapproval.document.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yang1.eapproval.document.domain.status.ApprovalStepStatus;
import org.yang1.eapproval.document.domain.vo.ApprovalStepData;
import org.yang1.eapproval.user.domain.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

class ApprovalLineTest {


    // 1. 결재자 List를 stub한다.
    // 2. 결재자의 상태를 WAITING으로 고정(submit 메서드 호출X)하여 예외가 발생하는지 확인한다.
    @Test
    @DisplayName("PENDING 단계가 없다면 예외가 발생해야 한다")
    void 현재_결재차례_단계가_없다면_예외() {
        // given
        User createdUser = mock(User.class);

        User approver = mock(User.class);
        given(approver.getId()).willReturn(1L);

        ApprovalStepData approverData = ApprovalStepData.of(approver, 1, "첫 번째 결재자");

        // 결재자 WAITING
        ApprovalLine line = ApprovalLine.create(createdUser, List.of(approverData));

        // when & then
        assertThatThrownBy(() -> line.approveSteps(approver.getId(), "test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결재할 차례의 단계가 존재하지 않습니다.");
    }


    @Test
    @DisplayName("결재 승인 성공")
    void 결재_승인_성공() {
        // given
        User createdUser = mock(User.class);

        User approver1 = mock(User.class);
        given(approver1.getId()).willReturn(1L);

        User approver2 = mock(User.class);
        given(approver2.getId()).willReturn(2L);

        ApprovalStepData approverData1 = ApprovalStepData.of(approver1, 1, "첫 번째 결재자");
        ApprovalStepData approverData2 = ApprovalStepData.of(approver2, 2, "두 번째 결재자");
        List<ApprovalStepData> approvers = List.of(approverData1, approverData2);

        ApprovalLine line = ApprovalLine.create(createdUser, approvers);
        line.lineSubmit();

        // when
        line.approveSteps(approver1.getId(), "해피 테스트1");
        line.approveSteps(approver2.getId(), "해피 테스트2");

        // then
        assertThat(line.getApprovalSteps())
                .extracting(
                        ApprovalStep::getStepOrder,
                        ApprovalStep::getStepStatus,
                        ApprovalStep::getCommentText
                )
                .containsExactly(
                        tuple(1, ApprovalStepStatus.APPROVED, "해피 테스트1"),
                        tuple(2, ApprovalStepStatus.APPROVED, "해피 테스트2")
                );
    }


    /**
     * 결재자 3명이 존재한다고 가정
     * 각 A, B, C 결재자
     * A결재자가 결재를 진행함. -> A결재자의 상태는 APPROVED 상태로 변경돼야 하며, 이후 B 결재자의 상태는 PENDING 상태가 돼야 함
     * C결재자는 여전히 WAITING
     */
    @Test
    @DisplayName("결재 승인 시 나머지 결재에 대한 상태 변화 추적")
    void 결재_승인_시_나머지_결재의_상태_변화() {
        // given
        User createdUser = mock(User.class);

        User approver1 = mock(User.class);
        given(approver1.getId()).willReturn(1L);

        User approver2 = mock(User.class);
        given(approver2.getId()).willReturn(2L);

        User approver3 = mock(User.class);
        given(approver3.getId()).willReturn(3L);

        ApprovalStepData stepData1 = ApprovalStepData.of(approver1, 1, "첫 번째 결재자");
        ApprovalStepData stepData2 = ApprovalStepData.of(approver2, 2, "두 번째 결재자");
        ApprovalStepData stepData3 = ApprovalStepData.of(approver3, 3, "세 번째 결재자");

        List<ApprovalStepData> approvers = List.of(stepData1, stepData2, stepData3);
        ApprovalLine line = ApprovalLine.create(createdUser, approvers);
        line.lineSubmit(); // 첫 번째 결재자 PENDING

        // when
        line.approveSteps(1L, "첫 번째 결재자 승인");

        // then
        assertThat(line.getApprovalSteps())
                .extracting(
                        ApprovalStep::getStepOrder,
                        ApprovalStep::getStepStatus,
                        ApprovalStep::getCommentText
                )
                .containsExactly(
                        tuple(1, ApprovalStepStatus.APPROVED, "첫 번째 결재자 승인"),
                        tuple(2, ApprovalStepStatus.PENDING, "두 번째 결재자"),
                        tuple(3, ApprovalStepStatus.WAITING, "세 번째 결재자")
                );
    }
    
    
    @Test
    @DisplayName("결재자 중 승인을 한 명이라도 수행하지 않았다면 false가 리턴돼야 한다")
    void 한_명이라도_결재_승인이_누락됐다면_false() {
        // given
        User approver1 = mock(User.class);
        given(approver1.getId()).willReturn(1L);

        User approver2 = mock(User.class);
        given(approver2.getId()).willReturn(2L);

        ApprovalStepData stepData1 = ApprovalStepData.of(approver1, 1, "첫 번째");
        ApprovalStepData stepData2 = ApprovalStepData.of(approver2, 2, "두 번째");

        List<ApprovalStepData> approvers = List.of(stepData1, stepData2);

        ApprovalLine line = ApprovalLine.create(mock(User.class), approvers);
        line.lineSubmit();

        line.approveSteps(1L, "첫 번째 결재자만 승인 완료한 상태");

        // when
        boolean isApproved = line.isAllApproved();

        // then
        assertThat(isApproved).isFalse();
    }
    
    
    @Test
    @DisplayName("모든 결재자가 승인을 완료했다면 true가 리턴돼야 한다")
    void 모든_결재자가_승인했다면_true() {
        // given
        User approver1 = mock(User.class);
        given(approver1.getId()).willReturn(1L);

        User approver2 = mock(User.class);
        given(approver2.getId()).willReturn(2L);

        User approver3 = mock(User.class);
        given(approver3.getId()).willReturn(3L);

        ApprovalStepData stepData1 = ApprovalStepData.of(approver1, 1, "첫 번째");
        ApprovalStepData stepData2 = ApprovalStepData.of(approver2, 2, "두 번째");
        ApprovalStepData stepData3 = ApprovalStepData.of(approver3, 3, "세 번째");

        List<ApprovalStepData> approvers = List.of(stepData1, stepData2, stepData3);

        ApprovalLine line = ApprovalLine.create(mock(User.class), approvers);
        line.lineSubmit();

        line.approveSteps(1L, "첫 번째 결재자 승인 완료");
        line.approveSteps(2L, "두 번째 결재자 승인 완료");
        line.approveSteps(3L, "세 번째 결재자 승인 완료");

        // when
        boolean isApproved = line.isAllApproved();

        // then
        assertThat(isApproved).isTrue();
    }

}