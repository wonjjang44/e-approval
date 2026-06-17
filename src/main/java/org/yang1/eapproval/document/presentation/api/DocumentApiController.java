package org.yang1.eapproval.document.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yang1.eapproval.common.response.ApiResult;
import org.yang1.eapproval.document.application.command.DocumentDraftCommand;
import org.yang1.eapproval.document.application.service.DocumentService;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DocumentDraftResponse;
import org.yang1.eapproval.document.presentation.api.dto.request.DocumentDraftRequest;

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

}
