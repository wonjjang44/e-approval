package org.yang1.eapproval.document.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.common.entity.BaseEntity;
import org.yang1.eapproval.user.domain.entity.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "approval_lines")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApprovalLine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdUser;

    @OneToMany(mappedBy = "approvalLine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApprovalStep> approvalSteps = new ArrayList<>();



    @Builder(access = AccessLevel.PRIVATE)
    private ApprovalLine(User createdUser) {
        this.createdUser = createdUser;
    }


    public static ApprovalLine create(User createdUser, List<ApprovalStep> approvalSteps) {
        ApprovalLine line = ApprovalLine.builder()
                .createdUser(createdUser)
                .build();

        for (ApprovalStep step : approvalSteps) {
            ApprovalStep approvalStep = ApprovalStep.create(step.getApprover(), step.getStepOrder(), step.getCommentText());
            line.assignApprovalStep(approvalStep);
        }

        return line;
    }



    void connectDocument(Document document) {
        this.document = document;
    }


    /**
     * ApprovalLine <-> ApprovalStep 1:N 양방향 연관관계 연결
     *
     * @param approvalStep ApprovalStep
     */
    private void assignApprovalStep(ApprovalStep approvalStep) {
        this.approvalSteps.add(approvalStep);
        approvalStep.connectApprovalLine(this);
    }
}
