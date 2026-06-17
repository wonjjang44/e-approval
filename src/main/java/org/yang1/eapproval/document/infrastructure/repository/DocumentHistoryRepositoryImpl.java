package org.yang1.eapproval.document.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.yang1.eapproval.document.domain.entity.DocumentHistory;
import org.yang1.eapproval.document.domain.repository.DocumentHistoryRepository;
import org.yang1.eapproval.document.infrastructure.repository.jpa.DocumentHistoryJpaRepository;

@Repository
@RequiredArgsConstructor
public class DocumentHistoryRepositoryImpl implements DocumentHistoryRepository {

    private final DocumentHistoryJpaRepository documentHistoryJpaRepository;


    @Override
    public DocumentHistory save(DocumentHistory documentHistory) {
        return documentHistoryJpaRepository.save(documentHistory);
    }
}
