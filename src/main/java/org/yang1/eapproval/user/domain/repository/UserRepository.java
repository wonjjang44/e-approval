package org.yang1.eapproval.user.domain.repository;

import org.yang1.eapproval.user.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    User save(User user);

    List<User> findAll();

    /**
     * 사용자 loginId 중복 체크
     */
    boolean existsByLoginId(String loginId);

}
