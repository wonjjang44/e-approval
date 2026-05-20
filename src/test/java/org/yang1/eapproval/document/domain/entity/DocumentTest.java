package org.yang1.eapproval.document.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yang1.eapproval.document.domain.status.DocumentStatus;
import org.yang1.eapproval.user.domain.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DocumentTest {
    
    
    @Test
    @DisplayName("문서를 임시저장하면 상태는 DRAFT가 돼야 한다.(결재선 없는 임시저장)")
    void 문서_임시저장_결재선_없음() {
        // given
        User drafter = mock(User.class);
        String title = "제목";
        String content = "내용";

        // when
        Document doc = Document.createDocument(drafter, title, content);

        // then
        assertThat(doc.getDrafter()).isEqualTo(drafter);
        assertThat(doc.getTitle()).isEqualTo(title);
        assertThat(doc.getContent()).isEqualTo(content);
        assertThat(doc.getDocumentStatus()).isEqualTo(DocumentStatus.DRAFT);

        assertThat(doc.getApprovalLine()).isNull();
        assertThat(doc.getSubmittedAt()).isNull();
    }


    @Test
    @DisplayName("문서 임시저장 시 결재선까지 함께 등록했다면 문서 상태는 IN_PROGRESS여야 한다")
    void 문서_등록_결재선_같이_등록_및_상신() {
        // given
        User drafter = mock(User.class);
        String title = "제목";
        String content = "내용";

        User approver1 = mock(User.class);
        User approver2 = mock(User.class);

        when(approver1.getId()).thenReturn(1L);
        when(approver2.getId()).thenReturn(2L);

        // when
        Document doc = Document.createDocumentAndSubmit(drafter, title, content, drafter, List.of(approver1, approver2));
        ApprovalLine line = doc.getApprovalLine();
        ApprovalStep step1 = line.getApprovalSteps().get(0);
        ApprovalStep step2 = line.getApprovalSteps().get(1);

        // then
        assertThat(doc.getDrafter()).isEqualTo(drafter);
        assertThat(doc.getTitle()).isEqualTo(title);
        assertThat(doc.getContent()).isEqualTo(content);

        assertThat(doc.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
        assertThat(doc.getSubmittedAt()).isNotNull();

        assertThat(line).isNotNull();
        assertThat(line.getDocument()).isSameAs(doc);
        assertThat(line.getCreatedUser()).isEqualTo(drafter);
        assertThat(line.getApprovalSteps().size()).isEqualTo(2);

        assertThat(step1.getStepOrder()).isEqualTo(1);
        assertThat(step1.getApprover()).isEqualTo(approver1);
        assertThat(step1.getApprovalLine()).isEqualTo(line);

        assertThat(step2.getStepOrder()).isEqualTo(2);
        assertThat(step2.getApprover()).isEqualTo(approver2);
        assertThat(step2.getApprovalLine()).isEqualTo(line);
    }


    @Test
    @DisplayName("임시저장인 문서를 상신하면 결재선 및 결재단계가 생성되고 문서 상태는 IN_PROGRESS로 변경돼야 한다")
    void 임시저장_상신_시_결재선_결재단계가_생성돼야한다() {
        // given
        User drafter = mock(User.class);
        User approver = mock(User.class);

        Document doc = Document.createDocument(drafter, "제목", "냐용");

        // when
        doc.submit(drafter, List.of(approver));

        // then
        assertThat(doc.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
        assertThat(doc.getSubmittedAt()).isNotNull();

        assertThat(doc.getApprovalLine()).isNotNull();
        assertThat(doc.getApprovalLine().getDocument()).isEqualTo(doc);
        assertThat(doc.getApprovalLine().getApprovalSteps().size()).isEqualTo(1);
        assertThat(doc.getApprovalLine().getApprovalSteps().get(0).getStepOrder()).isEqualTo(1);
        assertThat(doc.getApprovalLine().getApprovalSteps().get(0).getApprover()).isEqualTo(approver);
    }


    @Test
    @DisplayName("이미 상신된 문서를 다시 상신하면 예외가 발생해야 한다")
    void 이미_상신된_문서를_다시_상신했을때_예외_발생() {
        // given
        User drafter = mock(User.class);
        User approver = mock(User.class);

        Document doc = Document.createDocument(drafter, "제목", "내용");
        doc.submit(drafter, List.of(approver));

        // then
        assertThatThrownBy(() -> doc.submit(drafter, List.of(approver)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("임시저장 상태의 문서만 상신할 수 있습니다.");
    }


    @Test
    @DisplayName("결재자가 누락됐다면 상신할 때 예외가 발생해야 한다")
    void 결재자_누락_예외_발생() {
        // given
        User drafter = mock(User.class);
        Document doc = Document.createDocument(drafter, "제목", "내요ㅇ");

        // then
        assertThatThrownBy(() -> doc.submit(drafter, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결재자는 최소 1명 이상 존재해야 합니다.");
    }
}