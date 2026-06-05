package org.yang1.eapproval.document.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.common.entity.BaseEntity;
import org.yang1.eapproval.document.domain.status.ApprovalStepStatus;
import org.yang1.eapproval.document.domain.status.DocumentStatus;
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


    void connectApprovalLine(ApprovalLine approvalLine) {
        this.approvalLine = approvalLine;
    }
}
