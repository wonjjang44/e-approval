package org.yang1.eapproval.document.domain.repository;

import org.yang1.eapproval.document.domain.entity.DocumentHistory;

public interface DocumentHistoryRepository {

    DocumentHistory save(DocumentHistory documentHistory);

}
