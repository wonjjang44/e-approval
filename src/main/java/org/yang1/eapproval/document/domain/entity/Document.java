package org.yang1.eapproval.document.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.common.entity.BaseEntity;
import org.yang1.eapproval.document.domain.status.DocumentStatus;
import org.yang1.eapproval.document.domain.vo.ApprovalStepData;
import org.yang1.eapproval.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "documents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drafter_id", nullable = false)
    private User drafter;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DocumentStatus documentStatus;

    private LocalDateTime submittedAt;
    private LocalDateTime completedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime withdrawnAt;
    private LocalDateTime deletedAt;

    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private ApprovalLine approvalLine;



    @Builder(access = AccessLevel.PRIVATE)
    private Document(User drafter, String title, String content) {
        this.drafter = drafter;
        this.title = title;
        this.content = content;
    }


    /**
     * 문서 제목, 내용 임시저장
     *
     * @param drafter 기안자
     * @param title 제목
     * @param content 내용
     *
     * @return Document
     */
    public static Document createDraft(User drafter, String title, String content) {
        Document doc = Document.builder()
                .drafter(drafter)
                .title(title)
                .content(content)
                .build();

        doc.documentStatus = DocumentStatus.DRAFT;

        return doc;
    }


    /**
     * 문서 제목, 내용, 결재선 입력 후 임시저장
     *
     * @param drafter 기안자
     * @param title 제목
     * @param content 내용
     * @param approvalStepDataList 결재자들
     *
     * @return Document
     */
    public static Document createDraftWithApprovalLine(User drafter, String title, String content, List<ApprovalStepData> approvalStepDataList) {
        Document doc = Document.builder()
                .drafter(drafter)
                .title(title)
                .content(content)
                .build();

        doc.documentStatus = DocumentStatus.DRAFT;

        // 결재선 및 결재단계 생성
        ApprovalLine line = ApprovalLine.create(drafter, approvalStepDataList);

        // 결재선 연관관계 연결
        doc.assignApprovalLine(line);

        return doc;
    }


    /**
     * Document <-> ApprovalLine 1:1 양방향 연관관계 연결
     *
     * @param approvalLine ApprovalLine
     */
    private void assignApprovalLine(ApprovalLine approvalLine) {
        this.approvalLine = approvalLine;
        approvalLine.connectDocument(this);
    }

}
