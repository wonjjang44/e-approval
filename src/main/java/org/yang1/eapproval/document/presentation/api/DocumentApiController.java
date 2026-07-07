package org.yang1.eapproval.document.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yang1.eapproval.common.response.ApiResult;
import org.yang1.eapproval.document.application.command.DocumentApproveCommand;
import org.yang1.eapproval.document.application.command.DocumentDraftCommand;
import org.yang1.eapproval.document.application.command.DocumentSubmitCommand;
import org.yang1.eapproval.document.application.command.DraftedDocumentSubmitCommand;
import org.yang1.eapproval.document.application.service.DocumentService;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DocumentApproveResponse;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DocumentDetailResponse;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DocumentDraftResponse;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DocumentSubmitResponse;
import org.yang1.eapproval.document.presentation.api.dto.request.DocumentApproveRequest;
import org.yang1.eapproval.document.presentation.api.dto.request.DocumentDraftRequest;
import org.yang1.eapproval.document.presentation.api.dto.request.DocumentSubmitRequest;
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
    public ResponseEntity<ApiResult<DocumentSubmitResponse>> createDraftedSubmitDocument(@PathVariable Long documentId, @RequestBody @Valid DraftedDocumentSubmitRequest request) {
        DraftedDocumentSubmitCommand command = request.toCommand(documentId);

        return ResponseEntity.ok(ApiResult.success("임시저장 문서 상신 성공", documentService.submitDraftedDocument(command)));
    }


    /**
     * 임시저장하지 않은 문서 상신
     * 문서 제목, 내용, 결재선 일괄 등록 후 즉시 상신(임시저장 X)
     *
     * @param request
     * @return
     */
    @PostMapping("/documents/submit")
    public ResponseEntity<ApiResult<DocumentSubmitResponse>> createDirectSubmitDocument(@RequestBody @Valid DocumentSubmitRequest request) {
        DocumentSubmitCommand command = request.toCommand();

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.success("문서 상신 성공", documentService.submitDocument(command)));
    }


    @PostMapping("/documents/approve")
    public ResponseEntity<ApiResult<DocumentApproveResponse>> createApproveDocument(@RequestBody @Valid DocumentApproveRequest request) {
        DocumentApproveCommand command = request.toCommand();

        return ResponseEntity.ok(ApiResult.success("문서 승인 성공", documentService.approveDocument(command)));
    }
}
