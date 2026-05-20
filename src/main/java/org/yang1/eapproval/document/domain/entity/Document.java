package org.yang1.eapproval.document.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.common.entity.BaseEntity;
import org.yang1.eapproval.document.domain.status.DocumentStatus;
import org.yang1.eapproval.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "documents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 단방향
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drafter_id", nullable = false)
    private User drafter;

    // 양방향
    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private ApprovalLine approvalLine;

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


    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<DocumentHistory> documentHistories = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<ApprovalHistory> approvalHistories = new ArrayList<>();




    @Builder(access = AccessLevel.PRIVATE)
    private Document(User drafter, String title, String content) {
        this.drafter = drafter;
        this.title = title;
        this.content = content;

        this.documentStatus = DocumentStatus.DRAFT; // 문서 최초 생성 시 초기 값은 임시저장
    }


    /**
     * 문서 임시저장 시 결재선을 생성하지 않고 기본 내용만 저장한다
     *
     * @param drafter 기안자
     * @param title 제목
     * @param content 내용
     *
     * @return Document Entity
     */
    public static Document createDocument(User drafter, String title, String content) {
        return Document.builder()
                .drafter(drafter)
                .title(title)
                .content(content)
                .build();
    }


    /**
     * 문서를 생성하고 이어서 결재선을 만들고 상신한다
     *
     * @param drafter 기안자
     * @param title 제목
     * @param content 내용
     * @param createdUser 결재선 생성자
     *
     * @return Document Entity
     */
    public static Document createDocumentAndSubmit(User drafter, String title, String content, User createdUser, List<User> approver) {
        Document document = createDocument(drafter, title, content);

        // 문서 상신
        document.submit(createdUser, approver);

        return document;
    }


    /**
     * 문서 상신
     *
     * @param createdUser
     * @param approver
     */
    public void submit(User createdUser, List<User> approver) {
        if(this.documentStatus != DocumentStatus.DRAFT)
            throw new IllegalStateException("임시저장 상태의 문서만 상신할 수 있습니다.");

        // 결재선 생성
        ApprovalLine approvalLine = ApprovalLine.create(createdUser);

        // 결재 단계 생성
        approvalLine.createApprovalSteps(approver);

        connectApprovalLine(approvalLine);

        this.documentStatus = DocumentStatus.IN_PROGRESS;
        this.submittedAt = LocalDateTime.now();
    }


    /**
     * Document <-> ApprovalLine 연관관계 연결
     *
     * @param approvalLine ApprovalLine Entity
     */
    private void connectApprovalLine(ApprovalLine approvalLine) {
        if(approvalLine == null) throw new IllegalArgumentException("결재선은 필수로 등록해야 합니다.");
        if(this.approvalLine != null) throw new IllegalStateException("결재선이 이미 존재합니다.");

        this.approvalLine = approvalLine;

        // ApprovalLine -> Document 연관관계 연결 메서드 호출
        approvalLine.changeDocument(this);
    }

}
