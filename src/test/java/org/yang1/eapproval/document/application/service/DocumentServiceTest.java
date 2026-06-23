package org.yang1.eapproval.document.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yang1.eapproval.document.application.command.ApprovalStepDraftCommand;
import org.yang1.eapproval.document.application.command.DocumentDraftCommand;
import org.yang1.eapproval.document.domain.entity.Document;
import org.yang1.eapproval.document.domain.entity.DocumentHistory;
import org.yang1.eapproval.document.domain.repository.DocumentHistoryRepository;
import org.yang1.eapproval.document.domain.repository.DocumentRepository;
import org.yang1.eapproval.document.domain.status.ActionType;
import org.yang1.eapproval.document.domain.status.ApprovalStepStatus;
import org.yang1.eapproval.document.domain.status.DocumentStatus;
import org.yang1.eapproval.document.domain.vo.ApprovalStepData;
import org.yang1.eapproval.document.exception.DocumentNotFoundException;
import org.yang1.eapproval.document.presentation.api.dto.reponse.ApprovalStepResponse;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DocumentDetailResponse;
import org.yang1.eapproval.document.presentation.api.dto.reponse.DocumentDraftResponse;
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

            List<ApprovalStepDraftCommand> steps = List.of(ApprovalStepDraftCommand.of(approverId, 1, "첫 번째 결재자"));
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

            ApprovalStepDraftCommand step1 = ApprovalStepDraftCommand.of(approverId1, 1, "첫 번째 결재자");
            ApprovalStepDraftCommand step2 = ApprovalStepDraftCommand.of(approverId2, 2, "두 번째 결재자");

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
}