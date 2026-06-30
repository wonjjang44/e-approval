package org.yang1.eapproval.document.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yang1.eapproval.common.response.ApiResult;
import org.yang1.eapproval.document.application.command.DocumentDraftCommand;
import org.yang1.eapproval.document.application.command.DraftedDocumentSubmitCommand;
import org.yang1.eapproval.document.application.service.DocumentService;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DocumentDetailResponse;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DocumentDraftResponse;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DraftedDocumentSubmitResponse;
import org.yang1.eapproval.document.presentation.api.dto.request.DocumentDraftRequest;
import org.yang1.eapproval.document.presentation.api.dto.request.DraftedDocumentSubmitRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DocumentApiController {

    private final DocumentService documentService;



    /**
     * 문서 임시저장
     *
     * @param request
     * @return
     */
    @PostMapping("/documents/draft")
    public ResponseEntity<ApiResult<DocumentDraftResponse>> createDraftDocument(@RequestBody @Valid DocumentDraftRequest request) {
        DocumentDraftCommand command = request.toCommand();

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.success("문서 임시저장 성공", documentService.saveDraftDocument(command)));
    }


    /**
     * 문서 한 건 상세 조회
     *
     * @param id doc pk
     * @return
     */
    @GetMapping("/documents/{documentId}")
    public ResponseEntity<ApiResult<DocumentDetailResponse>> getDocument(@PathVariable(name = "documentId") Long id) {
        return ResponseEntity.ok(ApiResult.success("문서 조회 성공", documentService.getDocumentDetail(id)));
    }


    /**
     * 임시저장된 문서 상신
     *
     * @param documentId pk
     * @param request content 및 결재선
     * @return
     */
    @PostMapping("/documents/{documentId}/submit")
    public ResponseEntity<ApiResult<DraftedDocumentSubmitResponse>> createDraftedSubmitDocument(@PathVariable Long documentId, @RequestBody @Valid DraftedDocumentSubmitRequest request) {
        DraftedDocumentSubmitCommand command = request.toCommand(documentId);

        return ResponseEntity.ok(ApiResult.success("임시저장 문서 상신 성공", documentService.submitDraftedDocument(command)));
    }

}
