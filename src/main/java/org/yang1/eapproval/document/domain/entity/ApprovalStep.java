package org.yang1.eapproval.document.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yang1.eapproval.common.entity.BaseEntity;
import org.yang1.eapproval.document.domain.ApprovalStepStatus;
import org.yang1.eapproval.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="approval_steps")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApprovalStep extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="approval_line_id", nullable=false)
    @ToString.Exclude
    private ApprovalLine approvalLine;

    @Column(name="step_order", nullable=false)
    private Integer stepOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="approver_id", nullable=false)
    @ToString.Exclude
    private User approver;

    @Enumerated(EnumType.STRING)
    @Column(name="step_status", nullable=false, length=30)
    private ApprovalStepStatus stepStatus;

    @Column(name="acted_at")
    private LocalDateTime actedAt;

    @Column(name="comment_text", length=2000)
    private String commentText;

    @OneToMany(mappedBy="approvalStep")
    @ToString.Exclude
    private List<ApprovalHistory>  approvalHistories = new ArrayList<>();


    @Builder
    private ApprovalStep(Integer stepOrder, User approver, ApprovalStepStatus stepStatus) {
        this.stepOrder = Objects.requireNonNull(stepOrder);
        this.approver = Objects.requireNonNull(approver);
        this.stepStatus = Objects.requireNonNull(stepStatus);
    }


    public static ApprovalStep createApprovalStep(Integer stepOrder, User approver, ApprovalStepStatus stepStatus) {
        return ApprovalStep.builder()
                .stepOrder(stepOrder)
                .approver(approver)
                .stepStatus(stepStatus)
                .build();
    }


    /**
     * 결재선과 결재 단계의 연관관계 동기화 메서드
     *
     * @param approvalLine 결재 단계가 소속될 결재선
     */
    void addApprovalLine(ApprovalLine approvalLine) {
        this.approvalLine = approvalLine;
    }


    /**
     * 결재 단계에 결재 이력 추가
     *
     * @param approvalHistory 결재 단계에 추가할 결재 이력
     */
    void connectApprovalHistory(ApprovalHistory approvalHistory) {
        this.approvalHistories.add(approvalHistory);
        approvalHistory.addApprovalStep(this);
    }
}
