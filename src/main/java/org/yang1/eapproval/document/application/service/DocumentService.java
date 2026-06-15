package org.yang1.eapproval.document.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yang1.eapproval.document.application.command.DocumentDraftCommand;
import org.yang1.eapproval.document.domain.entity.Document;
import org.yang1.eapproval.document.domain.entity.DocumentHistory;
import org.yang1.eapproval.document.domain.repository.DocumentHistoryRepository;
import org.yang1.eapproval.document.domain.repository.DocumentRepository;
import org.yang1.eapproval.document.domain.status.ActionType;
import org.yang1.eapproval.document.domain.status.DocumentStatus;
import org.yang1.eapproval.document.domain.vo.ApprovalStepData;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DocumentDraftResponse;
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

}
