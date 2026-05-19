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
    @JoinColumn(name = "document_id", nullable = false,  unique = true)
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


    public static ApprovalLine create(User createdUser) {
        if(createdUser == null)
            throw new IllegalArgumentException("결재선 생성자는 필수값입니다.");

        return ApprovalLine.builder()
                .createdUser(createdUser)
                .build();
    }


    /**
     * 결재 단계 생성
     *
     * @param approvers 결재자들
     */
    public void createApprovalSteps(List<User> approvers) {
        if(!this.approvalSteps.isEmpty()) throw new IllegalStateException("이미 결재 단계가 생성되어 있습니다.");
        if(approvers == null || approvers.isEmpty()) throw new IllegalArgumentException("결재자는 최소 1명 이상 존재해야 합니다.");

        for(int i = 0; i < approvers.size(); i++) {
            User approver = approvers.get(i);

            // 결재자 중복 체크
            validateDuplicateApprover(approver);

            ApprovalStep step = ApprovalStep.createApprovalStep(i + 1, approver);
            connectApprovalStep(step);
        }
    }


    /**
     * ApprovalLine <-> Document 연관관계 연결
     *
     * @param document Document Entity
     */
    void changeDocument(Document document) {
        if(document == null) throw new IllegalArgumentException("문서는 필수로 입력돼야 합니다.");

        this.document = document;
    }


    /**
     * ApprovalLine <-> ApprovalStep 연관관계 연결
     *
     * @param approvalStep ApprovalStep
     */
    private void connectApprovalStep(ApprovalStep approvalStep) {
        if(approvalStep == null) throw new IllegalArgumentException("결재 단계는 필수입니다.");
        if(this.approvalSteps.contains(approvalStep)) return ;

        this.approvalSteps.add(approvalStep);
        approvalStep.changeApprovalLine(this);
    }


    /**
     * 결재자 중복체크
     *
     * @param approver 결재자
     */
    private void validateDuplicateApprover(User approver) {
        if(approver == null)
            throw new IllegalArgumentException("결재자는 필수값 입니다.");

        for (ApprovalStep step : this.approvalSteps) {
//            System.out.println("approvalStep = " + approvalStep);
            User existApprover = step.getApprover();

            if(existApprover.getId() != null && existApprover.getId().equals(approver.getId()))
                throw new IllegalArgumentException("결재자는 중복 등록할 수 없습니다.");
        }
    }
}
