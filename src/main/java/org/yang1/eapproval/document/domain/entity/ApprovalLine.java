package org.yang1.eapproval.document.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.common.entity.BaseEntity;
import org.yang1.eapproval.document.domain.status.ApprovalStepStatus;
import org.yang1.eapproval.document.domain.vo.ApprovalStepData;
import org.yang1.eapproval.document.exception.InvalidDocumentStatusException;
import org.yang1.eapproval.user.domain.entity.User;

import java.util.ArrayList;
import java.util.Comparator;
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
    @OrderBy("stepOrder asc")
    private List<ApprovalStep> approvalSteps = new ArrayList<>();



    @Builder(access = AccessLevel.PRIVATE)
    private ApprovalLine(User createdUser) {
        this.createdUser = createdUser;
    }


    public static ApprovalLine create(User createdUser, List<ApprovalStepData> approvalStepsDataList) {
        ApprovalLine line = ApprovalLine.builder()
                .createdUser(createdUser)
                .build();

        for (ApprovalStepData stepData : approvalStepsDataList) {
            ApprovalStep approvalStep = ApprovalStep.create(stepData.getApprover(), stepData.getStepOrder(), stepData.getCommentText());
            line.assignApprovalStep(approvalStep);
        }

        return line;
    }


    public void lineSubmit() {
        // 순서 정렬 후 가장 첫 번째 결재자의 결재 상태 변경(WAITING -> PENDING)
        this.approvalSteps.stream()
                .min(Comparator.comparingInt(ApprovalStep::getStepOrder))
                .orElseThrow(() -> new InvalidDocumentStatusException("결재 단계가 존재하지 않습니다."))
                .stepSubmit();
    }


    /**
     * 결재 단계를 변경한다
     *
     * @param steps 결재 단계(결재자들)
     */
    public void replaceSteps(List<ApprovalStepData> steps) {
        // 재할당이 아니라 완전 초기화(리스트 비우기)
        this.approvalSteps.clear();

        steps.forEach(step -> {
            ApprovalStep approvalStep = ApprovalStep.create(step.getApprover(), step.getStepOrder(), step.getCommentText());
            assignApprovalStep(approvalStep);
        });
    }


    /**
     * 결재선의 결재자들의 순번대로 결재를 진행해야 한다
     *
     * @param approverId
     * @param commentText
     * @return
     */
    public ApprovalStep approveSteps(Long approverId, String commentText) {
        // 결재 상태가 PENDING(자신의 결재차례)인 결재자 조회
        ApprovalStep currentStep = this.approvalSteps.stream()
                .filter(step -> step.getStepStatus() == ApprovalStepStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("결재할 차례의 단계가 존재하지 않습니다."));

        // 실제 상태 변경은 ApprovalStep에서 진행한다
        currentStep.approve(approverId, commentText);

        // 다음 WAITING 상태를 PENDING 상태로 변경한다(앞 사람 결재 끝났으니까)
        this.approvalSteps.stream()
                .filter(step -> step.getStepStatus() == ApprovalStepStatus.WAITING)
                .min(Comparator.comparingInt(ApprovalStep::getStepOrder))
                .ifPresent(ApprovalStep::stepSubmit);

        return currentStep;
    }


    /**
     * 모든 결재자가 결재를 승인 했는지 체크
     */
    public boolean isAllApproved() {
        return this.approvalSteps.stream()
                .allMatch(step -> step.getStepStatus() == ApprovalStepStatus.APPROVED);
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
