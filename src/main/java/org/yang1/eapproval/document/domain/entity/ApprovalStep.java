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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "approval_steps")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApprovalStep extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_line_id",  nullable = false)
    private ApprovalLine approvalLine;

    @Column(nullable = false)
    private int stepOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApprovalStepStatus stepStatus;

    private LocalDateTime actedAt;

    @Column(length = 2000)
    private String commentText;

    @OneToMany(mappedBy = "approvalStep", cascade = CascadeType.ALL)
    private List<ApprovalHistory> approvalHistories = new ArrayList<>();



    @Builder(access = AccessLevel.PRIVATE)
    private ApprovalStep(int stepOrder, User approver) {
        this.stepOrder = stepOrder;
        this.approver = approver;
        this.stepStatus = ApprovalStepStatus.WAITING;
    }


    public static ApprovalStep createApprovalStep(int stepOrder, User approver) {
        if(stepOrder <= 0) throw new IllegalArgumentException("결재 순서는 1부터 시작돼야 합니다.");
        if(approver == null) throw new IllegalArgumentException("결재자는 필수로 입력돼야 합니다.");

        return ApprovalStep.builder()
                .stepOrder(stepOrder)
                .approver(approver)
                .build();
    }


    /**
     * ApprovalStep <-> ApprovalLine 연관관계 연결
     *
     * @param approvalLine ApprovalLine
     */
    void changeApprovalLine(ApprovalLine approvalLine) {
        if(approvalLine == null)
            throw new IllegalArgumentException("결재선은 필수로 입력돼야 합니다.");

        this.approvalLine = approvalLine;
    }
}
