package org.yang1.eapproval.document.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yang1.eapproval.common.entity.BaseEntity;
import org.yang1.eapproval.user.domain.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "approval_lines")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApprovalLine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="document_id", nullable = false, unique = true)
    @ToString.Exclude
    private Document document;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="created_by_id", nullable = false)
    @ToString.Exclude
    private User creator;

    @OneToMany(mappedBy="approvalLine", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ApprovalStep> approvalSteps = new ArrayList<>();



    @Builder
    private ApprovalLine(User creator) {
        this.creator = Objects.requireNonNull(creator);
    }


    public static ApprovalLine createApprovalLine(User creator) {
        return ApprovalLine.builder()
                .creator(creator)
                .build();
    }

    /**
     * 문서와 결재선 연관관계 동기화 메서드
     *
     * @param document 결재선이 소속될 문서
     */
    void addDocument(Document document) {
        this.document = document;
    }


    /**
     * 결재선에 결재 단계 추가
     *
     * @param approvalStep 결재선에 추가할 결재 단계
     */
    void connectApprovalStep(ApprovalStep approvalStep) {
        this.approvalSteps.add(approvalStep);
        approvalStep.addApprovalLine(this);
    }
}
