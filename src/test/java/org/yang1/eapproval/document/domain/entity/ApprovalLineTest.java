package org.yang1.eapproval.document.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yang1.eapproval.user.domain.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApprovalLineTest {
    
    
    @Test
    @DisplayName("결재선을 생성하면 결재생성자가 세팅돼야 한다")
    void 결재생성자_생성() {
        // given
        User createdUser = mock(User.class);

        // when
        ApprovalLine line = ApprovalLine.create(createdUser);

        // then
        assertThat(line.getCreatedUser()).isSameAs(createdUser);
    }


    @Test
    @DisplayName("결재선 생성자가 null이라면 예외가 발생해야 한다")
    void 결재선_생성자_null() {
        assertThatThrownBy(() -> ApprovalLine.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결재선 생성자는 필수값입니다.");
    }
    
    
    @Test
    @DisplayName("1명 이상의 결재자로 결재 단계를 생성한다")
    void 결재단계_생성_및_연관관계_확인() {
        // given
        User createdUser = mock(User.class);

        User approver1 = mock(User.class);
        User approver2 = mock(User.class);

        when(approver1.getId()).thenReturn(1L);
        when(approver2.getId()).thenReturn(2L);

        ApprovalLine line = ApprovalLine.create(createdUser);

        // when
        line.createApprovalSteps(List.of(approver1, approver2)); // 여기서 중복 예외가 터지네?

        ApprovalStep step1 = line.getApprovalSteps().get(0);
        ApprovalStep step2 = line.getApprovalSteps().get(1);

        // then
        assertThat(line.getApprovalSteps()).hasSize(2);

        assertThat(step1.getStepOrder()).isEqualTo(1);
        assertThat(step1.getApprover()).isSameAs(approver1);
        assertThat(step1.getApprovalLine()).isSameAs(line); // 양방향 연관관계 동등 확인

        assertThat(step2.getStepOrder()).isEqualTo(2);
        assertThat(step2.getApprover()).isSameAs(approver2);
        assertThat(step2.getApprovalLine()).isSameAs(line);
    }


    @Test
    @DisplayName("결재자 리스트가 null 또는 비었다면 예외가 발생해야 한다")
    void 결재자_리스트가_null_이라면_예외가_발생해야한다() {
        // given
        User createdUser = mock(User.class);
        ApprovalLine line = ApprovalLine.create(createdUser);

        // then
        assertThatThrownBy(() -> line.createApprovalSteps(List.of())) // 비어있는 List 또는 null 일 경우 예외 발생해야 함
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결재자는 최소 1명 이상 존재해야 합니다.");
    }


    @Test
    @DisplayName("결재가 동일하다면 중복 예외가 발생해야 한다")
    void 결재자가_중복이라면_예외_발생() {
        // given
        User approver1 = mock(User.class);
        User approver2 = mock(User.class);

        when(approver1.getId()).thenReturn(1L);
        when(approver2.getId()).thenReturn(1L);

        ApprovalLine line = ApprovalLine.create(mock(User.class));

        // then
        assertThatThrownBy(() -> line.createApprovalSteps(List.of(approver1, approver2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결재자는 중복 등록할 수 없습니다.");
    }
}