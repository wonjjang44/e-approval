package org.yang1.eapproval.document.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yang1.eapproval.document.application.command.DocumentApproveCommand;
import org.yang1.eapproval.document.application.command.DocumentDraftCommand;
import org.yang1.eapproval.document.application.command.DocumentSubmitCommand;
import org.yang1.eapproval.document.application.command.DraftedDocumentSubmitCommand;
import org.yang1.eapproval.document.domain.entity.ApprovalHistory;
import org.yang1.eapproval.document.domain.entity.ApprovalStep;
import org.yang1.eapproval.document.domain.entity.Document;
import org.yang1.eapproval.document.domain.entity.DocumentHistory;
import org.yang1.eapproval.document.domain.repository.ApprovalHistoryRepository;
import org.yang1.eapproval.document.domain.repository.DocumentHistoryRepository;
import org.yang1.eapproval.document.domain.repository.DocumentRepository;
import org.yang1.eapproval.document.domain.status.ActionType;
import org.yang1.eapproval.document.domain.status.ApprovalStepStatus;
import org.yang1.eapproval.document.domain.status.DocumentStatus;
import org.yang1.eapproval.document.domain.vo.ApprovalStepData;
import org.yang1.eapproval.document.exception.DocumentNotFoundException;
import org.yang1.eapproval.document.infrastructure.repository.jpa.ApprovalHistoryJpaRepository;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DocumentApproveResponse;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DocumentDetailResponse;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DocumentDraftResponse;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DocumentSubmitResponse;
import org.yang1.eapproval.user.domain.entity.User;
import org.yang1.eapproval.user.domain.repository.UserRepository;
import org.yang1.eapproval.user.exception.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    private final DocumentHistoryRepository documentHistoryRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;



    /**
     * 문서 임시저장
     *
     * @param command
     * @return
     */
    @Transactional
    public DocumentDraftResponse saveDraftDocument(DocumentDraftCommand command) {
        if(command.getDrafterId() == null) throw new IllegalArgumentException("기안자는 누락될 수 없습니다.");

        User drafter = userRepository.findById(command.getDrafterId())
                .orElseThrow(() -> new UserNotFoundException("기안자가 존재하지 않습니다."));

        Document doc;

        // 제목, 내용만 임시저장
        if(command.getSteps().isEmpty()) doc = Document.createDraft(drafter, command.getTitle(), command.getContent());

        // 제목, 내용, 결재선 일괄 임시저장
        else {
            List<ApprovalStepData> stepDataVoList = command.getSteps().stream()
                    .map(s -> {
                        User approver = userRepository.findById(s.getApproverId())
                                .orElseThrow(() -> new UserNotFoundException("결재자가 존재하지 않습니다."));

                        return ApprovalStepData.of(approver, s.getStepOrder(), s.getCommentText());
                    })
                    .toList();

            doc = Document.createDraftWithApprovalLine(drafter, command.getTitle(), command.getContent(), stepDataVoList);
        }

        Document savedDoc = documentRepository.save(doc);

        // 문서 이력
        DocumentHistory docHistory = DocumentHistory.create(
                savedDoc, drafter,
                ActionType.CREATED,
                DocumentStatus.DRAFT,
                DocumentStatus.DRAFT,
                "최초 임시저장"
        );

        documentHistoryRepository.save(docHistory);

        return DocumentDraftResponse.from(savedDoc);
    }


    /**
     * 문서 상세조회
     *
     * @param id pk
     * @return
     */
    public DocumentDetailResponse getDocumentDetail(Long id) {
        Document findDoc = documentRepository.findDetailById(id)
                .orElseThrow(() -> new DocumentNotFoundException("문서가 존재하지 않습니다."));

        return DocumentDetailResponse.from(findDoc);
    }


    /**
     * 임시저장 문서 상신
     *
     * @param command 내용 + 결재선
     * @return
     */
    @Transactional
    public DocumentSubmitResponse submitDraftedDocument(DraftedDocumentSubmitCommand command) {
        // 기존 문서 조회
        Document findDoc = documentRepository.findDetailById(command.getDocumentId())
                .orElseThrow(() -> new DocumentNotFoundException("문서가 존재하지 않습니다."));

        findDoc.updateTitle(command.getTitle());
        findDoc.updateContent(command.getContent());

        // 결재자 등록
        List<ApprovalStepData> stepDataVoList = command.getSteps().stream()
                .map(s -> {
                    User approver = userRepository.findById(s.getApproverId())
                            .orElseThrow(() -> new UserNotFoundException("결재자가 존재하지 않습니다."));

                    return ApprovalStepData.of(approver, s.getStepOrder(), s.getCommentText());
                })
                .toList();

        findDoc.attachApprovalLine(stepDataVoList);

        findDoc.submit();

        // 이력 추가
        DocumentHistory findDocHistory = DocumentHistory.create(
                findDoc, findDoc.getDrafter(),
                ActionType.SUBMITTED,
                DocumentStatus.DRAFT,
                DocumentStatus.IN_PROGRESS,
                "임시저장 문서 상신"
        );

        documentHistoryRepository.save(findDocHistory);

        return DocumentSubmitResponse.from(findDoc);
    }


    /**
     * 문서를 상신한다
     *
     * 임시저장되지 않은 상태의 문서
     *
     * @param command 문서 및 결재자
     * @return
     */
    @Transactional
    public DocumentSubmitResponse submitDocument(DocumentSubmitCommand command) {
        if(command.getDrafterId() == null) throw new IllegalArgumentException("기안자는 누락될 수 없습니다.");

        User drafter = userRepository.findById(command.getDrafterId())
                .orElseThrow(() -> new UserNotFoundException("기안자가 존재하지 않습니다."));

        List<ApprovalStepData> steps = command.getSteps().stream()
                .map((step) -> {
                    User approver = userRepository.findById(step.getApproverId())
                            .orElseThrow(() -> new UserNotFoundException("결재자가 존재하지 않습니다."));

                    return ApprovalStepData.of(approver, step.getStepOrder(), step.getCommentText());
                })
                .toList();

        Document doc = Document.createDraftWithApprovalLine(drafter, command.getTitle(), command.getContent(), steps);
        doc.submit();

        Document savedDoc = documentRepository.save(doc);

        DocumentHistory docHistory = DocumentHistory.create(
                savedDoc,
                drafter,
                ActionType.SUBMITTED,
                DocumentStatus.DRAFT,
                DocumentStatus.IN_PROGRESS,
                "문서 상신"
        );

        documentHistoryRepository.save(docHistory);

        return DocumentSubmitResponse.from(savedDoc);
    }


    /**
     * 문서를 결재 승인한다
     *
     * @param command
     * @return
     */
    @Transactional
    public DocumentApproveResponse approveDocument(DocumentApproveCommand command) {
        Document doc = documentRepository.findDetailById(command.getDocumentId())
                .orElseThrow(() -> new DocumentNotFoundException("문서가 존재하지 않습니다."));

        DocumentStatus beforeStatus = doc.getDocumentStatus();

        ApprovalStep step = doc.approve(command.getApproverId(), command.getCommentText());

        // 문서 이력(문서는 승인일 때 한 번만)
        if(doc.getDocumentStatus() == DocumentStatus.APPROVED) {
            DocumentHistory docHistory = DocumentHistory.create(doc, step.getApprover(), ActionType.APPROVED, beforeStatus, doc.getDocumentStatus(), "문서 최종 승인");
            documentHistoryRepository.save(docHistory);
        }

        // 결재 이력
        ApprovalHistory stepHistory = ApprovalHistory.create(doc, step, step.getApprover(), ActionType.APPROVED, ApprovalStepStatus.PENDING, step.getStepStatus(), step.getCommentText());
        approvalHistoryRepository.save(stepHistory);

        return DocumentApproveResponse.from(doc);
    }
}
