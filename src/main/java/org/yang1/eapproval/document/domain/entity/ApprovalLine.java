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

    @OneToMany(mappedBy="approvalLine")
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
}
