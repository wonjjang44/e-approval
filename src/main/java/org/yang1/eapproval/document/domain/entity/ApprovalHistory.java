package org.yang1.eapproval.document.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yang1.eapproval.common.entity.BaseEntity;
import org.yang1.eapproval.document.domain.ActionStatus;
import org.yang1.eapproval.document.domain.ApprovalStepStatus;
import org.yang1.eapproval.user.domain.entity.User;

import java.util.Objects;

@Entity
@Table(name="approval_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApprovalHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="document_id", nullable=false)
    @ToString.Exclude
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="approval_step_id", nullable = false)
    @ToString.Exclude
    private ApprovalStep approvalStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="actor_id", nullable=false)
    @ToString.Exclude
    private User actor;

    @Enumerated(EnumType.STRING)
    @Column(name="action_type", nullable=false)
    private ActionStatus actionType;

    @Enumerated(EnumType.STRING)
    @Column(name="from_approval_status", length = 30)
    private ApprovalStepStatus fromApprovalStatus;

    @Enumerated(EnumType.STRING)
    @Column(name="to_approval_status", length = 30)
    private ApprovalStepStatus toApprovalStatus;

    @Column(name="comment_text", length = 2000)
    private String commentText;



    @Builder
    private ApprovalHistory(User actor, ActionStatus actionType, ApprovalStepStatus fromApprovalStatus, ApprovalStepStatus toApprovalStatus, String commentText) {
        this.actor = Objects.requireNonNull(actor);
        this.actionType = Objects.requireNonNull(actionType);
        this.fromApprovalStatus = fromApprovalStatus;
        this.toApprovalStatus = toApprovalStatus;
        this.commentText = commentText;
    }


    public static ApprovalHistory createApprovalHistory(User actor, ActionStatus actionType, ApprovalStepStatus fromApprovalStatus, ApprovalStepStatus toApprovalStatus, String commentText) {
        return ApprovalHistory.builder()
                .actor(actor)
                .actionType(actionType)
                .fromApprovalStatus(fromApprovalStatus)
                .toApprovalStatus(toApprovalStatus)
                .commentText(commentText)
                .build();
    }
}
