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

    /**
     * OneToOne LAZY는 EAGER처럼 동작
     * 조회 쿼리 별도로 만들어야 할 듯?
     */
    @OneToOne(mappedBy = "document", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private ApprovalLine approvalLine;

    // 첨부파일은 논리 삭제로..
    @OneToMany(mappedBy="document", cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private List<DocumentAttachment> attachments = new ArrayList<>();

    // 이력은 저장할 때만 전이되도록
    @OneToMany(mappedBy="document", cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private List<DocumentHistory> documentHistories = new ArrayList<>();

    // 이력은 저장할 때만 전이되도록
    @OneToMany(mappedBy="document", cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private List<ApprovalHistory> approvalHistories = new ArrayList<>();


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


    /**
     * 문서에 결재선을 연결하는 연관관계 편의 메서드
     *
     * @param approvalLine 문서에 연결할 결재선
     */
    public void connectApprovalLine(ApprovalLine approvalLine) {
        this.approvalLine = approvalLine;
        approvalLine.addDocument(this);
    }


    /**
     * 문서에 첨부파일을 추가하는 연관관계 편의 메서드
     *
     * @param documentAttachment 문서에 추가할 첨부파일
     */
    public void connectAttachment(DocumentAttachment documentAttachment) {
        this.attachments.add(documentAttachment);
        documentAttachment.addDocument(this);
    }


    /**
     * 문서에 문서 이력을 추가하는 연관관계 편의 메서드
     *
     * @param documentHistory 문서에 추가할 문서 이력
     */
    public void connectDocumentHistory(DocumentHistory documentHistory) {
        this.documentHistories.add(documentHistory);
        documentHistory.addDocument(this);
    }


    /**
     * 문서에 결재 이력을 추가하는 연관관계 편의 메서드
     *
     * @param approvalHistory 문서에 추가할 결재 이력
     */
    public void connectApprovalHistory(ApprovalHistory approvalHistory) {
        this.approvalHistories.add(approvalHistory);
        approvalHistory.addDocument(this);
    }


    /**
     * 문서와 결재 단계에 결재 이력을 함께 연결
     *
     * @param approvalStep 결재 이력이 소속될 결재 단계
     * @param approvalHistory 문서와 결재 단계에 연결할 결재 이력
     */
    public void connectApprovalHistory(ApprovalStep approvalStep, ApprovalHistory approvalHistory) {
        this.connectApprovalHistory(approvalHistory);
        approvalStep.connectApprovalHistory(approvalHistory);
    }

}
