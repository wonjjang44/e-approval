package org.yang1.eapproval.document.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yang1.eapproval.document.domain.status.ApprovalStepStatus;
import org.yang1.eapproval.document.domain.status.DocumentStatus;
import org.yang1.eapproval.document.domain.vo.ApprovalStepData;
import org.yang1.eapproval.user.domain.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    
}