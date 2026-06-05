package org.yang1.eapproval.document.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.common.entity.BaseCreatedEntity;
import org.yang1.eapproval.document.domain.status.ActionType;
import org.yang1.eapproval.document.domain.status.ApprovalStepStatus;
import org.yang1.eapproval.user.domain.entity.User;

@Entity
@Table(name = "approval_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApprovalHistory extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_step_id", nullable = false)
    private ApprovalStep approvalStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApprovalStepStatus beforeApprovalStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApprovalStepStatus afterApprovalStatus;

    @Column(length = 2000)
    private String commentText;


    @Builder(access = AccessLevel.PRIVATE)
    private ApprovalHistory(Document document, ApprovalStep approvalStep, User actor, ActionType actionType, ApprovalStepStatus beforeApprovalStatus, ApprovalStepStatus afterApprovalStatus, String commentText) {
        this.document = document;
        this.approvalStep = approvalStep;
        this.actor = actor;
        this.actionType = actionType;
        this.beforeApprovalStatus = beforeApprovalStatus;
        this.afterApprovalStatus = afterApprovalStatus;
        this.commentText = commentText;
    }


    public static ApprovalHistory create(Document document, ApprovalStep approvalStep, User actor, ActionType actionType, ApprovalStepStatus beforeApprovalStatus, ApprovalStepStatus afterApprovalStatus, String commentText) {
        return ApprovalHistory.builder()
                .document(document)
                .approvalStep(approvalStep)
                .actor(actor)
                .actionType(actionType)
                .beforeApprovalStatus(beforeApprovalStatus)
                .afterApprovalStatus(afterApprovalStatus)
                .commentText(commentText)
                .build();
    }
}
