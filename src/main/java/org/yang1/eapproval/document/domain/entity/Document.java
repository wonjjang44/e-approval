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
        if(drafter == null) throw new IllegalArgumentException("기안자는 누락될 수 없습니다.");
        if(title == null || title.isBlank()) throw new IllegalArgumentException("제목은 누락될 수 없습니다.");

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
        if(drafter == null) throw new IllegalArgumentException("기안자는 누락될 수 없습니다.");
        if(title == null || title.isBlank()) throw new IllegalArgumentException("제목은 누락될 수 없습니다.");
        if(approvalStepDataList == null || approvalStepDataList.isEmpty()) throw new IllegalArgumentException("결재자는 누락될 수 없습니다.");

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
     * 상신의 경우의 수
     * - 1. 임시저장된 문서를 상신
     * - 2. 임시저장하지 않은 상태에서 제목 + 내용 + 결재선 입력 후 일괄 상신
     *
     * 상신이 되면 어떤 현상이 발생하는가
     *  - 문서 상태가 IN_PROGRESS 상태로 변경된다 => 도메인 엔티티 비즈니스 로직 담당
     */
    public void submit() {
        if(this.documentStatus != DocumentStatus.DRAFT || this.approvalLine == null)
            throw new IllegalArgumentException("상신할 수 없는 상태의 문서입니다.");

        this.documentStatus = DocumentStatus.IN_PROGRESS;
        this.submittedAt = LocalDateTime.now();

        this.approvalLine.lineSubmit();
    }


    /**
     * 문서 제목 수정
     *
     * @param title
     */
    public void updateTitle(String title) {
        if(title == null || title.isBlank()) throw new IllegalArgumentException("제목은 공란일 수 없습니다.");
        if(this.documentStatus == null || this.documentStatus != DocumentStatus.DRAFT)
            throw new IllegalArgumentException("임시저장 상태에서만 가능합니다.");

        this.title = title;
    }


    /**
     * 문서 내용 수정
     *
     * @param content
     */
    public void updateContent(String content) {
        if(this.documentStatus == null || this.documentStatus != DocumentStatus.DRAFT)
            throw new IllegalArgumentException("임시저장 상태에서만 가능합니다.");

        this.content = content;
    }


    /**
     * 결재선 생성
     * 임시저장 상태의 문서에 결재선을 붙이기 위함
     *
     * @param steps
     */
    public void attachApprovalLine(List<ApprovalStepData> steps) {
        if(steps == null || steps.isEmpty()) throw new IllegalArgumentException("결재자는 최소 1명 이상 존재해야 합니다.");
        if(this.documentStatus == null || this.documentStatus != DocumentStatus.DRAFT)
            throw new IllegalArgumentException("임시저장 상태에서만 결재선을 추가할 수 있습니다.");

        /*
             임시저장 문서의 경우의 수
             1. 제목 + 내용만 입력된 상태
             2. 제목 + 내용 + 결재선까지 모두 입력된 상태
             두 번째 케이스에서 결재선이 변경됐다면 UK 제약 조건 위배가 발생할 수 있음
             hibernate flush 순서는  i -> u -> d 순서임.
             따라서 UK 제약 조건 위배의 원흉인 결재선(ApprovalLine)을 통째로 갈아끼우기 보단
             결재선에 물린 결재 단계들만 모두 지운 다음 새롭게 insert해주면 된다.
         */

        // 임시저장 문서에 결재선이 없는 경우
        if(this.approvalLine == null) {
            ApprovalLine line = ApprovalLine.create(this.drafter, steps);
            assignApprovalLine(line);
        } else { // 결재선이 있다면 결재 단계만 교체한다(기존 결재선 살림)
            this.approvalLine.replaceSteps(steps);
        }
    }


    /**
     * 승인이 어떻게 이루어지나?
     * 1. 문서의 상태는 반드시 결재 진행 중 상태여야 한다.
     * 2. 결재선이 반드시 존재해야 한다.
     * 3. 존재하는 결재선에 속한 결재자들의 결재 순번대로 결재가 이루어저야 한다.
     */
    public ApprovalStep approve(Long approverId, String commentText) {
        if(this.documentStatus != DocumentStatus.IN_PROGRESS) throw new IllegalArgumentException("결재 진행 중인 문서가 아닙니다.");

        // 결재선으로 위임 -> 결재선에 포함된 결재자들(결재단계) 순번을 확인하여 순차적으로 결재 진행하게 한다
        ApprovalStep approvedStep = this.approvalLine.approveSteps(approverId, commentText);

        // 마지막 결재 단계까지 결재 완료 됐다면 문서 최종 승인처리
        if(this.approvalLine.isAllApproved()) {
            this.documentStatus = DocumentStatus.APPROVED;
            this.completedAt = LocalDateTime.now();
        }

        return approvedStep;
    }


    /**
     * 문서 결재 반려
     * 현재 차례(PENDING)의 결재자가 반려하면 문서는 즉시 반려(REJECTED) 처리
     *
     * @param approverId
     * @param commentText
     * @return
     */
    public ApprovalStep reject(Long approverId, String commentText) {
        if(this.documentStatus != DocumentStatus.IN_PROGRESS) throw new IllegalArgumentException("결재 진행 중인 문서가 아닙니다.");

        ApprovalStep rejectedStep = this.approvalLine.rejectSteps(approverId, commentText);

        // 한 명이라도 반려하면 문서는 바로 반려 종료
        this.documentStatus = DocumentStatus.REJECTED;
        this.rejectedAt = LocalDateTime.now();

        return rejectedStep;
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
