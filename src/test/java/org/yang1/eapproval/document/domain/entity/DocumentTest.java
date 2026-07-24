package org.yang1.eapproval.document.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yang1.eapproval.document.domain.status.ApprovalStepStatus;
import org.yang1.eapproval.document.domain.status.DocumentStatus;
import org.yang1.eapproval.document.domain.vo.ApprovalStepData;
import org.yang1.eapproval.user.domain.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.tuple;
import static org.mockito.BDDMockito.*;

class DocumentTest {

    @Test
    @DisplayName("결재선을 제외한 문서 임시저장 시 문서 상태는 DRAFT(임시저장) 상태여야 한다")
    void 제목_내용_문서_생성() {
        // given
        User drafter = mock(User.class);

        String title = "test";
        String content = "test11";

        // when
        Document doc = Document.createDraft(drafter, title, content);

        // then
        assertThat(doc.getDrafter()).isSameAs(drafter);

        assertThat(doc.getTitle()).isEqualTo(title);
        assertThat(doc.getContent()).isEqualTo(content);

        assertThat(doc.getDocumentStatus()).isEqualTo(DocumentStatus.DRAFT);

        assertThat(doc.getApprovalLine()).isNull();

        assertThat(doc.getSubmittedAt()).isNull();
        assertThat(doc.getCompletedAt()).isNull();
        assertThat(doc.getRejectedAt()).isNull();
        assertThat(doc.getWithdrawnAt()).isNull();
        assertThat(doc.getDeletedAt()).isNull();
    }

    
    @Test
    @DisplayName("결재선을 포함한 문서 임시저장 시 결재선이 null이면 안 된다")
    void 결재선_포함_문서_생성() {
        // given
        User drafter = mock(User.class);

        User approver1 = mock(User.class);
        User approver2 = mock(User.class);
        User approver3 = mock(User.class);

        String title = "test55";
        String content = "test66";

        ApprovalStepData firstApprovalStep = ApprovalStepData.of(approver1, 1, "내가 첫 번째 결재자");
        ApprovalStepData secondApprovalStep = ApprovalStepData.of(approver2, 2, "내가 두 번째 결재자");
        ApprovalStepData thirdApprovalStep = ApprovalStepData.of(approver3, 3, "내가 세 번째 결재자");

        // when
        Document doc = Document.createDraftWithApprovalLine(drafter, title, content, List.of(firstApprovalStep, secondApprovalStep, thirdApprovalStep));

        // then
        assertThat(doc.getDrafter()).isSameAs(drafter);
        assertThat(doc.getTitle()).isEqualTo(title);
        assertThat(doc.getContent()).isEqualTo(content);

        assertThat(doc.getDocumentStatus()).isEqualTo(DocumentStatus.DRAFT);

        assertThat(doc.getApprovalLine()).isNotNull();

        ApprovalLine approvalLine = doc.getApprovalLine();
        assertThat(approvalLine.getDocument()).isSameAs(doc);
        assertThat(approvalLine.getCreatedUser()).isSameAs(drafter);
        assertThat(approvalLine.getApprovalSteps()).hasSize(3);

        List<ApprovalStep> approvalSteps = approvalLine.getApprovalSteps();
        assertThat(approvalSteps.get(0).getApprovalLine()).isSameAs(approvalLine);
        assertThat(approvalSteps.get(0).getStepOrder()).isEqualTo(1);

        assertThat(approvalSteps.get(0).getStepStatus()).isEqualTo(ApprovalStepStatus.WAITING);
        assertThat(approvalSteps.get(0).getActedAt()).isNull();
        assertThat(approvalSteps.get(0).getCommentText()).isEqualTo("내가 첫 번째 결재자");

        assertThat(approvalSteps.get(1).getApprovalLine()).isSameAs(approvalLine);
        assertThat(approvalSteps.get(1).getStepOrder()).isEqualTo(2);

        assertThat(approvalSteps.get(1).getStepStatus()).isEqualTo(ApprovalStepStatus.WAITING);
        assertThat(approvalSteps.get(1).getActedAt()).isNull();
        assertThat(approvalSteps.get(1).getCommentText()).isEqualTo("내가 두 번째 결재자");

        assertThat(approvalSteps.get(2).getApprovalLine()).isSameAs(approvalLine);
        assertThat(approvalSteps.get(2).getStepOrder()).isEqualTo(3);

        assertThat(approvalSteps.get(2).getStepStatus()).isEqualTo(ApprovalStepStatus.WAITING);
        assertThat(approvalSteps.get(2).getActedAt()).isNull();
        assertThat(approvalSteps.get(2).getCommentText()).isEqualTo("내가 세 번째 결재자");

        assertThat(approvalSteps.get(0).getApprover()).isSameAs(approver1);
        assertThat(approvalSteps.get(1).getApprover()).isSameAs(approver2);
        assertThat(approvalSteps.get(2).getApprover()).isSameAs(approver3);
    }
    
    
    @Test
    @DisplayName("임시저장 + 결재선이 있는 문서를 상신하면 문서는 IN_PROGRESS 상태가 되고 첫 결재단계는 PENDING이 돼야 한다")
    void 문서_상신_성공() {
        // given
        User drafter = mock(User.class);

        User approver1 = mock(User.class);
        User approver2 = mock(User.class);

        Document doc = Document.createDraftWithApprovalLine(
                drafter,
                "제목",
                "내용",
                List.of(
                        ApprovalStepData.of(approver1, 1, "첫 번째 결재자"),
                        ApprovalStepData.of(approver2, 2, "두 번째 결재자")
                )
        );

        // when
        doc.submit();

        // then
        assertThat(doc.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
        assertThat(doc.getSubmittedAt()).isNotNull();

        List<ApprovalStep> steps = doc.getApprovalLine().getApprovalSteps();
        assertThat(steps.get(0).getStepStatus()).isEqualTo(ApprovalStepStatus.PENDING);
        assertThat(steps.get(1).getStepStatus()).isEqualTo(ApprovalStepStatus.WAITING);
    }


    @Test
    @DisplayName("결재선이 없는 문서를 상신하면 예외가 발생해야 한다")
    void 결재선_없는_문서_상신_시_예외() {
        // given
        User drafter = mock(User.class);
        Document doc = Document.createDraft(drafter, "테스트 제목", "테스트 내용");

        // when & then
        assertThatThrownBy(() -> doc.submit())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상신할 수 없는 상태의 문서입니다.");
    }


    @Test
    @DisplayName("이미 상신된 문서를 다시 상신하면 예외가 발생해야 한다")
    void 재상신_시_예외() {
        // given
        User drafter = mock(User.class);
        User approver = mock(User.class);

        Document doc = Document.createDraftWithApprovalLine(
                drafter,
                "제목",
                "제목이 곧 내용",
                List.of(
                        ApprovalStepData.of(approver, 1, "첫 번째 결재자")
                )
        );

        doc.submit();

        // when & then
        assertThatThrownBy(() -> doc.submit())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상신할 수 없는 상태의 문서입니다.");
    }


    @Test
    @DisplayName("결재선이 있는 임시저장 문서에 결재선을 추가하면 기존 결재선을 유지한 채 결재단계만 교체한다")
    void 결재선_있는_문서에_결재선_추가_시_기존_결재선_유지() {
        // given
        User drafter = mock(User.class);

        User oldApprover = mock(User.class);
        User newApprover1 = mock(User.class);
        User newApprover2 = mock(User.class);

        Document doc = Document.createDraftWithApprovalLine(
                drafter,
                "제목",
                "내용",
                List.of(ApprovalStepData.of(oldApprover, 1, "기존 결재자"))
        );

        ApprovalLine beforeLine = doc.getApprovalLine();

        List<ApprovalStepData> newSteps = List.of(
                ApprovalStepData.of(newApprover1, 1, "새로운 결재선의 첫 번째 결재자"),
                ApprovalStepData.of(newApprover2, 2, "새로운 결재선의 두 번째 결재자")
        );

        // when
        doc.attachApprovalLine(newSteps);

        // then
        assertThat(doc.getApprovalLine()).isSameAs(beforeLine);
        assertThat(doc.getApprovalLine().getApprovalSteps())
                .extracting(ApprovalStep::getApprover, ApprovalStep::getStepOrder, ApprovalStep::getCommentText)
                .containsExactly(
                        tuple(newApprover1, 1, "새로운 결재선의 첫 번째 결재자"),
                        tuple(newApprover2, 2, "새로운 결재선의 두 번째 결재자")
                );
    }


    @Test
    @DisplayName("결재선 추가 시 결재자가 비어 있다면 예외가 발생해야 한다")
    void 결재선_추가_시_결재가_없다면_예외() {
        // given
        User drafter = mock(User.class);
        Document doc = Document.createDraft(drafter, "제목", "내용");

        // when & then
        assertThatThrownBy(() -> doc.attachApprovalLine(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결재자는 최소 1명 이상 존재해야 합니다.");

        assertThatThrownBy(() -> doc.attachApprovalLine(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결재자는 최소 1명 이상 존재해야 합니다.");
    }


    @Test
    @DisplayName("임시저장 상태가 아닌 문서에 결재선을 추가하면 예외가 발생해야 한다")
    void 임시저장_아닌_문서에_결재선_추가_시_예외() {
        // given
        User drafter = mock(User.class);
        User approver = mock(User.class);

        Document doc = Document.createDraftWithApprovalLine(
                drafter,
                "제목",
                "내용",
                List.of(ApprovalStepData.of(approver, 1, "첫 번째 결재자"))
        );

        doc.submit();

        User newApprover = mock(User.class);

        // when & then
        assertThatThrownBy(() -> doc.attachApprovalLine(
                List.of(
                        ApprovalStepData.of(newApprover, 1, "새로운 결재자"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("임시저장 상태에서만 결재선을 추가할 수 있습니다.");
    }


    @Test
    @DisplayName("문서의 상태가 결재진행중(IN_PROGRESS) 상태가 아니라면 예외가 발생해야 한다")
    void 문서_상태가_결재진행중이_아닐_경우_예외() {
        // given
        User approver1 = mock(User.class);
        ApprovalStepData stepData = ApprovalStepData.of(approver1, 1, "첫 번째 결재자");

        Document doc = Document.createDraftWithApprovalLine(mock(User.class), "휴가 신청서", "2026-07-09 ~ 2026-07-09 총 1일 연차 사용", List.of(stepData));

        // when & then
        assertThatThrownBy(() -> doc.approve(1L, "첫 번째 결재자 승인 완료"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결재 진행 중인 문서가 아닙니다.");
    }
    
    
    @Test
    @DisplayName("결재 승인 시 나머지 결재에 대한 상태 변화 추적")
    void 결재_승인_시_나머지_결재의_상태_변화() {
        // given
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

        Document doc = Document.createDraftWithApprovalLine(mock(User.class), "휴가 신청서", "2026-07-09 ~ 2026-07-09 총 1일 연차 사용", approvers);
        doc.submit();

        // when
        doc.approve(1L, "첫 번째 결재자 결재 승인 완료");

        // then
        assertThat(doc.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
        assertThat(doc.getCompletedAt()).isNull();
    }


    @Test
    @DisplayName("문서 결재 최종 승인")
    void 문서_결재_최종_승인() {
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

        Document doc = Document.createDraftWithApprovalLine(mock(User.class), "휴가 신청서", "2026-07-09 ~ 2026-07-09 총 1일 연차 사용", approvers);
        doc.submit();

        // when
        doc.approve(1L, "첫 번째 결재자 결재 승인 완료");
        doc.approve(2L, "두 번째 결재자 결재 승인 완료");
        doc.approve(3L, "세 번째 결재자 결재 승인 완료");

        // then
        assertThat(doc.getDocumentStatus()).isEqualTo(DocumentStatus.APPROVED);
        assertThat(doc.getCompletedAt()).isNotNull();
    }


    @Test
    @DisplayName("문서 상태가 결재진행중(IN_PROGRESS)이 아니라면 반려 시 예외가 발생해야 한다")
    void 반려_시_문서_상태가_결재진행중이_아니면_예외() {
        // given
        User approver = mock(User.class);
        ApprovalStepData stepData = ApprovalStepData.of(approver, 1, "첫 번째 결재자");

        // 상신하지 않은 DRAFT 문서
        Document doc = Document.createDraftWithApprovalLine(mock(User.class), "휴가 신청서", "연차 1일", List.of(stepData));

        // when & then
        assertThatThrownBy(() -> doc.reject(1L, "반려 사유"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결재 진행 중인 문서가 아닙니다.");
    }


    @Test
    @DisplayName("문서 반려 시 문서는 REJECTED가 되고, 남은 대기 단계는 CANCELED가 돼야 한다")
    void 문서_반려() {
        // given
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
        Document doc = Document.createDraftWithApprovalLine(mock(User.class), "휴가 신청서", "연차 1일", approvers);
        doc.submit();
        doc.approve(1L, "첫 번째 결재자 승인 완료"); // 1: APPROVED, 2: PENDING

        // when
        doc.reject(2L, "두 번째 결재자 반려"); // 2: REJECTED, 3: CANCELED

        // then
        assertThat(doc.getDocumentStatus()).isEqualTo(DocumentStatus.REJECTED);
        assertThat(doc.getRejectedAt()).isNotNull();
        assertThat(doc.getCompletedAt()).isNull(); // 반려는 완료가 아니므로 completedAt 없음

        assertThat(doc.getApprovalLine().getApprovalSteps())
                .extracting(ApprovalStep::getStepOrder, ApprovalStep::getStepStatus)
                .containsExactly(
                        tuple(1, ApprovalStepStatus.APPROVED),
                        tuple(2, ApprovalStepStatus.REJECTED),
                        tuple(3, ApprovalStepStatus.CANCELED)
                );
    }
}