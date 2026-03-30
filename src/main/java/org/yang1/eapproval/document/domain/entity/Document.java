package org.yang1.eapproval.document.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yang1.eapproval.common.entity.BaseEntity;
import org.yang1.eapproval.document.domain.ApprovalStatus;
import org.yang1.eapproval.document.domain.DocumentStatus;
import org.yang1.eapproval.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="documents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="drafter_id", nullable=false)
    private User drafter;

    @Column(name="title",  nullable = false, length = 200)
    private String title;

    @Lob
    @Column(name="content", columnDefinition = "LONGTEXT")
    @ToString.Exclude
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name="document_status", nullable = false, length = 30)
    private DocumentStatus documentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name="approval_status", length = 30)
    private ApprovalStatus approvalStatus;

    @Column(name="current_step_order")
    private Integer currentStepOrder;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;



    @Builder
    private Document(User drafter, String title, String content) {
        this.drafter = Objects.requireNonNull(drafter);
        this.title = Objects.requireNonNull(title);
        this.content = content;
        this.documentStatus = DocumentStatus.DRAFT;
        this.approvalStatus = null;
        this.currentStepOrder = null;
    }


    public static Document createDocument(User drafter, String title, String content) {
        return Document.builder()
                .drafter(drafter)
                .title(title)
                .content(content)
                .build();
    }
}
