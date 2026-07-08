package org.yang1.eapproval.document.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yang1.eapproval.document.application.command.*;
import org.yang1.eapproval.document.domain.entity.ApprovalHistory;
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
import org.yang1.eapproval.document.presentation.api.dto.reponse.*;
import org.yang1.eapproval.user.domain.entity.User;
import org.yang1.eapproval.user.domain.repository.UserRepository;
import org.yang1.eapproval.user.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    DocumentRepository documentRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    DocumentHistoryRepository documentHistoryRepository;

    @Mock
    ApprovalHistoryRepository approvalHistoryRepository;

    @InjectMocks
    DocumentService documentService;


    @Nested
    @DisplayName("문서 임시저장 테스트")
    class CreateDocumentTests {

        @Test
        @DisplayName("문서 임시저장 시 기안자가 누락됐다면 예외가 발생해야 한다")
        void 문서_임시저장_시_기안자가_누락됐다면_예외() {
            // given
            // null 체크 이외에 공백 체크도 해야하나? 그런데 Service단에서는 null체크 로직만 있는데..
            DocumentDraftCommand command = DocumentDraftCommand.of(null, "test", "test1", List.of());

            // when & then
            assertThatThrownBy(() -> documentService.saveDraftDocument(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("기안자는 누락될 수 없습니다.");

            then(documentRepository).should(never()).save(any());
        }


        @Test
        @DisplayName("문서 임시저장 시 존재하지 않는 기안자일 경우 예외가 발생해야 한다")
        void 문서_임시저장_시_존재하지_않는_기안자라면_예외() {
            // given
            Long userId = 1L;
            DocumentDraftCommand command = DocumentDraftCommand.of(userId, "test", "test", List.of());

            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> documentService.saveDraftDocument(command))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("기안자가 존재하지 않습니다.");

            then(documentRepository).should(never()).save(any());
        }


        @Test
        @DisplayName("문서 제목이 누락되면 예외가 발생해야 한다")
        void 문서_제목_누락_시_예외() {
            // given
            Long drafterId = 1L;
            DocumentDraftCommand command = DocumentDraftCommand.of(drafterId, null, "test", List.of());

            User drafter = mock(User.class);
            given(userRepository.findById(drafterId)).willReturn(Optional.of(drafter));

            // when & then
            assertThatThrownBy(() -> documentService.saveDraftDocument(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("제목은 누락될 수 없습니다.");

            then(documentRepository).should(never()).save(any());
        }


        @Test
        @DisplayName("문서의 제목/내용을 임시저장한다")
        void 문서_임시저장() {
            // given
            Long drafterId = 1L;
            String title = "임시저장 등록";
            String content = "임시저장 등록 테스트";

            DocumentDraftCommand command = DocumentDraftCommand.of(drafterId, title, content, List.of());

            User drafter = mock(User.class);
            given(drafter.getUserName()).willReturn("Yang1");

            given(userRepository.findById(drafterId)).willReturn(Optional.of(drafter));

            given(documentRepository.save(any(Document.class)))
                    .willAnswer(i -> i.getArgument(0));

            // when
            DocumentDraftResponse docDraftResponse = documentService.saveDraftDocument(command);

            // then
            assertThat(docDraftResponse.getDrafterName()).isEqualTo("Yang1");

            assertThat(docDraftResponse.getTitle()).isEqualTo(title);
            assertThat(docDraftResponse.getContent()).isEqualTo(content);
            assertThat(docDraftResponse.getDocumentStatus()).isEqualTo(DocumentStatus.DRAFT);

            assertThat(docDraftResponse.getSteps()).hasSize(0);
        }


        @Test
        @DisplayName("문서 결재선 임시저장 시 결재자가 누락되면 예외가 발생해야 한다")
        void 임시저장_결재선_등록_시_결재자가_누락됐다면_예외() {
            // given
            Long drafterId = 9L;
            Long approverId = 99L;

            String title = "결재선 임시저장";
            String content = "결재선 임시저장 테스트";

            List<ApprovalStepCommand> steps = List.of(ApprovalStepCommand.of(approverId, 1, "첫 번째 결재자"));
            DocumentDraftCommand command = DocumentDraftCommand.of(drafterId, title, content, steps);

            // 기안자 존재
            User drafter = mock(User.class);
            given(userRepository.findById(drafterId)).willReturn(Optional.of(drafter));

            // 결재자 존재하지 않음
            given(userRepository.findById(approverId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> documentService.saveDraftDocument(command))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("결재자가 존재하지 않습니다.");

            then(documentRepository).should(never()).save(any());
        }


        @Test
        @DisplayName("문서의 제목, 내용, 결재선 일괄 임시저장")
        void 문서_제목_내용_결재선_임시저장() {
            // given
            Long drafterId = 9L;

            Long approverId1 = 99L;
            Long approverId2 = 999L;

            String title = "일괄 임시저장";
            String content = "일괄 임시저장 테스트";

            ApprovalStepCommand step1 = ApprovalStepCommand.of(approverId1, 1, "첫 번째 결재자");
            ApprovalStepCommand step2 = ApprovalStepCommand.of(approverId2, 2, "두 번째 결재자");

            DocumentDraftCommand command = DocumentDraftCommand.of(drafterId, title, content, List.of(step1, step2));

            User drafter = mock(User.class);
            given(drafter.getUserName()).willReturn("Yang1");

            given(userRepository.findById(drafterId)).willReturn(Optional.of(drafter));

            User approver1 = mock(User.class);
            given(approver1.getUserName()).willReturn("결재자1");
            given(userRepository.findById(approverId1)).willReturn(Optional.of(approver1));

            User approver2 = mock(User.class);
            given(approver2.getUserName()).willReturn("결재자2");
            given(userRepository.findById(approverId2)).willReturn(Optional.of(approver2));


            given(documentRepository.save(any(Document.class)))
                    .willAnswer(i -> i.getArgument(0));

            // when
            DocumentDraftResponse docDraftResponse = documentService.saveDraftDocument(command);

            // then
            assertThat(docDraftResponse.getDrafterName()).isEqualTo("Yang1");
            assertThat(docDraftResponse.getTitle()).isEqualTo(title);
            assertThat(docDraftResponse.getContent()).isEqualTo(content);
            assertThat(docDraftResponse.getDocumentStatus()).isEqualTo(DocumentStatus.DRAFT);

            assertThat(docDraftResponse.getSteps())
                    .extracting(
                            ApprovalStepResponse::getApproverName,
                            ApprovalStepResponse::getStepOrder,
                            ApprovalStepResponse::getCommentText,
                            ApprovalStepResponse::getStepStatus
                    )
                    .containsExactly(
                            tuple("결재자1", 1, "첫 번째 결재자", ApprovalStepStatus.WAITING),
                            tuple("결재자2", 2, "두 번째 결재자", ApprovalStepStatus.WAITING)
                    );
        }


        @Test
        @DisplayName("문서 임시저장 시 최초 이력은 CREATED 상태로 저장된다")
        void 문서_이력_최초_등록() {
            // given
            Long drafterId = 10L;
            DocumentDraftCommand command = DocumentDraftCommand.of(drafterId, "test", "test11", List.of());

            User actor = mock(User.class);
            given(userRepository.findById(drafterId)).willReturn(Optional.of(actor));
            given(documentRepository.save(any(Document.class)))
                    .willAnswer(i -> i.getArgument(0));

            // when
            documentService.saveDraftDocument(command);

            // then
            ArgumentCaptor<DocumentHistory> captor = ArgumentCaptor.forClass(DocumentHistory.class);
            then(documentHistoryRepository).should().save(captor.capture()); // save 메서드 파라미터인 DocumentHistory 가져옴

            DocumentHistory docHistory = captor.getValue();

            assertThat(docHistory.getActor()).isSameAs(actor);
            assertThat(docHistory.getActionType()).isEqualTo(ActionType.CREATED);
            assertThat(docHistory.getBeforeDocumentStatus()).isEqualTo(DocumentStatus.DRAFT);
            assertThat(docHistory.getAfterDocumentStatus()).isEqualTo(DocumentStatus.DRAFT);
            assertThat(docHistory.getMemo()).isEqualTo("최초 임시저장");
        }
    }


    @Nested
    @DisplayName("문서 조회 테스트")
    class GetDocumentTests {
        
        @Test
        @DisplayName("문서 단건을 조회 시 문서가 조회하지 않는다면 예외가 발생해야 한다")
        void 문서가_존재하지_않는다면_예외() {
            // given
            Long docId = 1L;

            given(documentRepository.findDetailById(docId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> documentService.getDocumentDetail(docId))
                    .isInstanceOf(DocumentNotFoundException.class)
                    .hasMessage("문서가 존재하지 않습니다.");
        }


        @Test
        @DisplayName("문서 단건 조회")
        void 문서_단건_조회() {
            // given
            Long docId = 1L;

            User drafter = mock(User.class);
            given(drafter.getUserName()).willReturn("Yang1");

            User approver1 = mock(User.class);
            given(approver1.getUserName()).willReturn("결재자1");

            User approver2 = mock(User.class);
            given(approver2.getUserName()).willReturn("결재자2");

            Document doc = Document.createDraftWithApprovalLine(
                    drafter,
                    "연차 생성",
                    "1일 연차 사용",
                    List.of(
                            ApprovalStepData.of(approver1, 1, "첫 번째 결재자"),
                            ApprovalStepData.of(approver2, 2, "두 번째 결재자"))
            );

            given(documentRepository.findDetailById(docId)).willReturn(Optional.of(doc));

            // when
            DocumentDetailResponse findDoc = documentService.getDocumentDetail(docId);

            // then
            assertThat(findDoc.getDrafterName()).isEqualTo("Yang1");
            assertThat(findDoc.getTitle()).isEqualTo("연차 생성");
            assertThat(findDoc.getContent()).isEqualTo("1일 연차 사용");
            assertThat(findDoc.getDocumentStatus()).isEqualTo(DocumentStatus.DRAFT);

            assertThat(findDoc.getSteps())
                    .extracting(
                            ApprovalStepResponse::getApproverName,
                            ApprovalStepResponse::getStepOrder,
                            ApprovalStepResponse::getCommentText,
                            ApprovalStepResponse::getStepStatus
                    )
                    .containsExactly(
                            tuple("결재자1", 1, "첫 번째 결재자", ApprovalStepStatus.WAITING),
                            tuple("결재자2", 2, "두 번째 결재자", ApprovalStepStatus.WAITING)
                    );

        }
        
        
        @Test
        @DisplayName("결재선 없는 문서 단건 조회 시 결재선은 반드시 비어있는 리스트로 리턴돼야 한다")
        void 결재선_없는_문서_단건_조회() {
            // given
            Long docId = 1L;
            User drafter = mock(User.class);
            given(drafter.getUserName()).willReturn("Yang1");

            // 결재선 없도록 세팅
            Document doc = Document.createDraft(drafter, "제목", "내용");
            given(documentRepository.findDetailById(docId)).willReturn(Optional.of(doc));

            // when
            DocumentDetailResponse response = documentService.getDocumentDetail(docId);

            // then
            assertThat(response.getSteps()).isEmpty();
        }
        
    }


    @Nested
    @DisplayName("문서 상신 테스트")
    class CreateSubmitDocumentTest {

        @Test
        @DisplayName("임시저장 상태의 문서를 상신할 때, 기존 임시저장된 문서가 조회하지 않는다면 예외가 발생해야 한다")
        void 임시저장_문서_존재하지_않을_시_예외() {
            // given
            Long drafterId = 1L;
            DraftedDocumentSubmitCommand command = DraftedDocumentSubmitCommand.of(1L, drafterId, "title", "content", List.of());

            given(documentRepository.findDetailById(drafterId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> documentService.submitDraftedDocument(command))
                    .isInstanceOf(DocumentNotFoundException.class)
                    .hasMessage("문서가 존재하지 않습니다.");
        }


        @Test
        @DisplayName("임시저장 상태의 문서에 결재선을 추가 시 결재자가 존재하지 않으면 예외가 발생해야 한다")
        void 임시저장_문서_결재선_추가_시_결재가_존재하지_않으면_예외() {
            // given
            Long approverId = 1L;
            Long documentId = 11L;

            ApprovalStepCommand approver = ApprovalStepCommand.of(approverId, 1, "임시저장 결재선 추가");
            DraftedDocumentSubmitCommand command = DraftedDocumentSubmitCommand.of(documentId, 1L, "title", "content", List.of(approver));

            Document doc = mock(Document.class);
            given(documentRepository.findDetailById(documentId)).willReturn(Optional.of(doc));

            given(userRepository.findById(approverId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> documentService.submitDraftedDocument(command))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("결재자가 존재하지 않습니다.");
        }


        @Test
        @DisplayName("임시저장 상태의 문서를 상신한다")
        void 임시저장_문서_상신() {
            // given
            Long drafterId = 1L;

            Long approverId1 = 1L;
            Long approverId2 = 2L;

            String changeTitle = "변경 후 제목";
            String changeContent = "변경 후 내용";

            ApprovalStepCommand approverCommand1 = ApprovalStepCommand.of(approverId1, 1, "첫 번째 결재자");
            ApprovalStepCommand approverCommand2 = ApprovalStepCommand.of(approverId2, 2, "두 번째 결재자");
            DraftedDocumentSubmitCommand command = DraftedDocumentSubmitCommand.of(1L, drafterId, "변경 후 제목", "변경 후 내용", List.of(approverCommand1, approverCommand2));

            User drafter = mock(User.class);

            User approver1 = mock(User.class);
            given(userRepository.findById(1L)).willReturn(Optional.of(approver1));

            User approver2 = mock(User.class);
            given(userRepository.findById(2L)).willReturn(Optional.of(approver2));

            ApprovalStepData approverData1 = ApprovalStepData.of(approver1, 1, "첫 번째 결재자");
            ApprovalStepData approverData2 = ApprovalStepData.of(approver2, 2, "두 번째 결재자");

            Document doc = Document.createDraftWithApprovalLine(drafter, "변경 전 제목", "변경 전 내용", List.of(approverData1, approverData2));
            given(documentRepository.findDetailById(drafterId)).willReturn(Optional.of(doc));

            // when
            DocumentSubmitResponse submittedDoc = documentService.submitDraftedDocument(command);

            // then
            assertThat(submittedDoc.getTitle()).isEqualTo(changeTitle);
            assertThat(submittedDoc.getContent()).isEqualTo(changeContent);
            assertThat(submittedDoc.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
            assertThat(submittedDoc.getSteps())
                    .extracting(ApprovalStepResponse::getStepOrder, ApprovalStepResponse::getStepStatus)
                    .containsExactly(
                            tuple(1, ApprovalStepStatus.PENDING),
                            tuple(2, ApprovalStepStatus.WAITING)
                    );
        }


        @Test
        @DisplayName("신규 문서 상신 시 기안자가 누락되면 예외가 발생해야 한다")
        void 신규_문서_상신_시_기안자_누락되면_예외() {
            // given
            DocumentSubmitCommand command = DocumentSubmitCommand.of(null, "", "", List.of());

            // when & then
            assertThatThrownBy(() -> documentService.submitDocument(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("기안자는 누락될 수 없습니다.");
        }


        @Test
        @DisplayName("신규 문서 상신 시 기안자가 존재하지 않는다면 예외가 발생해야 한다")
        void 신규_문서_상신_시_기안자_존재하지_않는다면_예외() {
            // given
            Long drafterId = 1L;

            DocumentSubmitCommand command = DocumentSubmitCommand.of(drafterId, "", "", List.of());

            given(userRepository.findById(drafterId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> documentService.submitDocument(command))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("기안자가 존재하지 않습니다.");
        }


        @Test
        @DisplayName("신규 문서 상신 시 결재자가 존재하지 않는다면 예외가 발생해야 한다")
        void 결재자_존재하지_않는다면_예외() {
            // given
            Long drafterId = 1L;

            Long approverId = 11L;

            User drafter = mock(User.class);
            given(userRepository.findById(drafterId)).willReturn(Optional.of(drafter));

            ApprovalStepCommand approver = ApprovalStepCommand.of(approverId, 1, "첫 번째 결재자");
            DocumentSubmitCommand command = DocumentSubmitCommand.of(drafterId, "", "", List.of(approver));

            given(userRepository.findById(approverId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> documentService.submitDocument(command))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("결재자가 존재하지 않습니다.");
        }
        
        
        @Test
        @DisplayName("신규 문서를 상신한다")
        void 신규_문서_상신() {
            // given
            Long drafterId = 1L;

            Long approverId1 = 11L;
            Long approverId2 = 12L;

            User drafter = mock(User.class);
            given(userRepository.findById(drafterId)).willReturn(Optional.of(drafter));

            User approver1 = mock(User.class);
            given(userRepository.findById(approverId1)).willReturn(Optional.of(approver1));

            User approver2 = mock(User.class);
            given(userRepository.findById(approverId2)).willReturn(Optional.of(approver2));

            ApprovalStepCommand approverCommand1 = ApprovalStepCommand.of(approverId1, 1, "첫 번째 결재자");
            ApprovalStepCommand approverCommand2 = ApprovalStepCommand.of(approverId2, 2, "두 번째 결재자");

            DocumentSubmitCommand command = DocumentSubmitCommand.of(drafterId, "변경할 제목 값", "변경할 내용 값", List.of(approverCommand1, approverCommand2));

            given(documentRepository.save(any(Document.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            DocumentSubmitResponse submittedDoc = documentService.submitDocument(command);

            // then
            assertThat(submittedDoc.getTitle()).isEqualTo("변경할 제목 값");
            assertThat(submittedDoc.getContent()).isEqualTo("변경할 내용 값");
            assertThat(submittedDoc.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
            assertThat(submittedDoc.getSteps())
                    .extracting(ApprovalStepResponse::getStepOrder, ApprovalStepResponse::getStepStatus)
                    .containsExactly(
                            tuple(1, ApprovalStepStatus.PENDING),
                            tuple(2, ApprovalStepStatus.WAITING)
                    );
        }


        @Test
        @DisplayName("임시저장 문서 상신 시 이력은 SUBMITTED 상태로 저장돼야 한다")
        void 임시저장_문서_상신_이력() {
            // given
            Long documentId = 1L;
            Long approverId = 11L;

            User drafter = mock(User.class);
            User approver = mock(User.class);
            given(userRepository.findById(approverId)).willReturn(Optional.of(approver));

            Document doc = Document.createDraftWithApprovalLine(
                    drafter,
                    "변경 전 제목",
                    "변경 전 내용",
                    List.of(ApprovalStepData.of(approver, 1, "첫 번째 결재자"))
            );
            given(documentRepository.findDetailById(documentId)).willReturn(Optional.of(doc));

            DraftedDocumentSubmitCommand command = DraftedDocumentSubmitCommand.of(
                    documentId,
                    1L,
                    "변경 후 제목",
                    "변경 후 내용",
                    List.of(ApprovalStepCommand.of(approverId, 1, "첫 번째 결재자"))
            );

            // when
            documentService.submitDraftedDocument(command);

            // then
            ArgumentCaptor<DocumentHistory> captor = ArgumentCaptor.forClass(DocumentHistory.class);
            then(documentHistoryRepository).should().save(captor.capture());

            DocumentHistory history = captor.getValue();
            assertThat(history.getActor()).isSameAs(drafter);
            assertThat(history.getActionType()).isEqualTo(ActionType.SUBMITTED);
            assertThat(history.getBeforeDocumentStatus()).isEqualTo(DocumentStatus.DRAFT);
            assertThat(history.getAfterDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
            assertThat(history.getMemo()).isEqualTo("임시저장 문서 상신");
        }
        
        
        @Test
        @DisplayName("신규 문서 상신 시 이력은 SUBMITTED 상태로 저장돼야 한다")
        void 신규_문서_상신_이력() {
            // given
            Long drafterId = 1L;
            Long approverId = 11L;

            User drafter = mock(User.class);
            given(userRepository.findById(drafterId)).willReturn(Optional.of(drafter));

            User approver = mock(User.class);
            given(userRepository.findById(approverId)).willReturn(Optional.of(approver));

            DocumentSubmitCommand command = DocumentSubmitCommand.of(
                    drafterId,
                    "제목",
                    "내용",
                    List.of(ApprovalStepCommand.of(approverId, 1, "첫 번째 결재자"))
            );

            given(documentRepository.save(any(Document.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            documentService.submitDocument(command);
            
            // then
            ArgumentCaptor<DocumentHistory> captor = ArgumentCaptor.forClass(DocumentHistory.class);
            then(documentHistoryRepository).should().save(captor.capture());

            DocumentHistory history = captor.getValue();
            assertThat(history.getActor()).isSameAs(drafter);
            assertThat(history.getActionType()).isEqualTo(ActionType.SUBMITTED);
            assertThat(history.getBeforeDocumentStatus()).isEqualTo(DocumentStatus.DRAFT);
            assertThat(history.getAfterDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
            assertThat(history.getMemo()).isEqualTo("문서 상신");
        }
    }


    @Nested
    @DisplayName("문서 승인 테스트")
    class createApproveTests {
        
        @Test
        @DisplayName("승인할 문서가 존재하지 않는다면 예외가 발생해야 한다")
        void 승인_문서_누락_시_예외() {
            // given
            DocumentApproveCommand command = DocumentApproveCommand.of(1L, 99L, "결재 승인 진행");

            given(documentRepository.findDetailById(1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> documentService.approveDocument(command))
                    .isInstanceOf(DocumentNotFoundException.class)
                    .hasMessage("문서가 존재하지 않습니다.");
        }
        
        
        @Test
        @DisplayName("결재 최종 승인 전에는 결재 이력만 쌓여야 한다")
        void 결재_최종_승인_전에는_결재_이력만_쌓인다() {
            // given
            User approver1 = mock(User.class);
            given(approver1.getId()).willReturn(1L);

            User approver2 = mock(User.class);

            ApprovalStepData stepData1 = ApprovalStepData.of(approver1, 1, "첫 번째 결재자");
            ApprovalStepData stepData2 = ApprovalStepData.of(approver2, 2, "두 번째 결재자");

            List<ApprovalStepData> approvers = List.of(stepData1, stepData2);

            Document doc = Document.createDraftWithApprovalLine(mock(User.class), "연차 사용", "연차 1일 사용 결재 부탁드립니다.", approvers);
            doc.submit();

            given(documentRepository.findDetailById(999L)).willReturn(Optional.of(doc));

            DocumentApproveCommand command = DocumentApproveCommand.of(999L, 1L, "첫 번째 결재자 승인 시작");

            // when
            documentService.approveDocument(command);

            // then
            then(documentHistoryRepository).should(never()).save(any(DocumentHistory.class));

            ArgumentCaptor<ApprovalHistory> captor = ArgumentCaptor.forClass(ApprovalHistory.class);
            then(approvalHistoryRepository).should().save(captor.capture());

            ApprovalHistory history = captor.getValue();
            assertThat(history.getActor()).isSameAs(approver1);
            assertThat(history.getActionType()).isEqualTo(ActionType.APPROVED);
            assertThat(history.getBeforeApprovalStatus()).isEqualTo(ApprovalStepStatus.PENDING);
            assertThat(history.getAfterApprovalStatus()).isEqualTo(ApprovalStepStatus.APPROVED);
            assertThat(history.getCommentText()).isEqualTo("첫 번째 결재자 승인 시작");
        }
        
        
        @Test
        @DisplayName("결재 최종 승인 시 문서 이력과 결재 이력이 같이 쌓여야 한다")
        void 최종_승인_시_문서이력과와_결재이력이_같이_쌓인다() {
            // given
            User approver1 = mock(User.class);
            given(approver1.getId()).willReturn(1L);

            User approver2 = mock(User.class);
            given(approver2.getId()).willReturn(2L);

            User approver3 = mock(User.class);
            given(approver3.getId()).willReturn(3L);

            ApprovalStepData stepData1 = ApprovalStepData.of(approver1, 1, "첫 번째 결재자");
            ApprovalStepData stepData2 = ApprovalStepData.of(approver2, 2, "두 번째 결재자");
            ApprovalStepData stepData3 = ApprovalStepData.of(approver3, 3, "마지막 번째 결재자");

            List<ApprovalStepData> approvers = List.of(stepData1, stepData2, stepData3);

            Document doc = Document.createDraftWithApprovalLine(mock(User.class), "휴가 기안서", "연차 1일 사용합니다.", approvers);
            doc.submit();

            // 앞 두 단계는 상태 준비용으로 엔티티에서 직접 승인한다(repository 안 거치므로 save 카운트에 안 잡힘)
            doc.approve(1L, "첫 번째 결재자 승인 완료");
            doc.approve(2L, "두 번째 결재자 승인 완료");

            given(documentRepository.findDetailById(1000L)).willReturn(Optional.of(doc));

            // 마지막 결재자 대상(마지막 결재자가 승인해야 문서 이력이 쌓이므로)
            DocumentApproveCommand command = DocumentApproveCommand.of(1000L, 3L, "마지막 결재자 승인");

            // when
            documentService.approveDocument(command);

            // then
            // 마지막 결재자의 승인이 완료됐다면 문서 이력 쌓여야 함
            ArgumentCaptor<DocumentHistory> docCaptor = ArgumentCaptor.forClass(DocumentHistory.class);
            then(documentHistoryRepository).should().save(docCaptor.capture());

            DocumentHistory docHistory = docCaptor.getValue();
            assertThat(docHistory.getActor()).isSameAs(approver3);
            assertThat(docHistory.getActionType()).isEqualTo(ActionType.APPROVED);
            assertThat(docHistory.getBeforeDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
            assertThat(docHistory.getAfterDocumentStatus()).isEqualTo(DocumentStatus.APPROVED);
            assertThat(docHistory.getMemo()).isEqualTo("문서 최종 승인");

            // 결재 이력
            ArgumentCaptor<ApprovalHistory> approvalCaptor = ArgumentCaptor.forClass(ApprovalHistory.class);
            then(approvalHistoryRepository).should().save(approvalCaptor.capture());

            ApprovalHistory approvalHistory = approvalCaptor.getValue();
            assertThat(approvalHistory.getActor()).isSameAs(approver3);
            assertThat(approvalHistory.getActionType()).isEqualTo(ActionType.APPROVED);
            assertThat(approvalHistory.getBeforeApprovalStatus()).isEqualTo(ApprovalStepStatus.PENDING);
            assertThat(approvalHistory.getAfterApprovalStatus()).isEqualTo(ApprovalStepStatus.APPROVED);
            assertThat(approvalHistory.getCommentText()).isEqualTo("마지막 결재자 승인");

        }
    }
}