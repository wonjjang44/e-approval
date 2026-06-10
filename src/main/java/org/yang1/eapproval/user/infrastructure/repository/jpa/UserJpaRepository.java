package org.yang1.eapproval.user.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yang1.eapproval.user.domain.entity.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
