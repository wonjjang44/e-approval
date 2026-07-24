package org.yang1.eapproval.document.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.common.entity.BaseEntity;
import org.yang1.eapproval.document.domain.status.ApprovalStepStatus;
import org.yang1.eapproval.user.domain.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_steps")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApprovalStep extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_line_id", nullable = false)
    private ApprovalLine approvalLine;

    @Column(nullable = false)
    private int stepOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private ApprovalStepStatus stepStatus;

    private LocalDateTime actedAt;

    @Column(length = 2000)
    private String commentText;


    @Builder(access = AccessLevel.PRIVATE)
    private ApprovalStep(User approver, int stepOrder, String commentText) {
        this.approver = approver;
        this.stepOrder = stepOrder;
        this.commentText = commentText;
    }



    public static ApprovalStep create(User approver, int stepOrder, String commentText) {
        ApprovalStep step = ApprovalStep.builder()
                .approver(approver)
                .stepOrder(stepOrder)
                .commentText(commentText)
                .build();

        step.stepStatus = ApprovalStepStatus.WAITING;

        return step;
    }


    public void stepSubmit() {
        if(this.stepStatus != ApprovalStepStatus.WAITING)
            throw new IllegalStateException("결재 대기 상태일 경우만 해당됩니다.");

        this.stepStatus = ApprovalStepStatus.PENDING;
    }


    /**
     * 본인 차례의 결재 승인
     */
    public void approve(Long approverId, String commentText) {
        if(this.stepStatus != ApprovalStepStatus.PENDING) throw new IllegalArgumentException("결재할 차례의 단계가 아닙니다.");
        if(!this.approver.getId().equals(approverId)) throw new IllegalArgumentException("해당 단계의 결재자가 아닙니다.");

        this.stepStatus = ApprovalStepStatus.APPROVED;
        this.actedAt = LocalDateTime.now();
        this.commentText = commentText;
    }


    /**
     * 본인 차례의 결재 반려
     */
    public void reject(Long approverId, String commentText) {
        if(this.stepStatus != ApprovalStepStatus.PENDING) throw new IllegalArgumentException("결재할 차례의 단계가 아닙니다.");
        if(!this.approver.getId().equals(approverId)) throw new IllegalArgumentException("해당 단계의 결재자가 아닙니다.");

        this.stepStatus = ApprovalStepStatus.REJECTED;
        this.actedAt = LocalDateTime.now();
        this.commentText = commentText;
    }


    /**
     * 앞 단계가 반려되어 더 이상 진행되지 않는 대기 단계 취소 처리
     */
    public void cancel() {
        if(this.stepStatus != ApprovalStepStatus.WAITING) throw new IllegalStateException("결재 대기 상태의 단계만 취소할 수 있습니다.");

        this.stepStatus = ApprovalStepStatus.CANCELED;
    }



    void connectApprovalLine(ApprovalLine approvalLine) {
        this.approvalLine = approvalLine;
    }
}
