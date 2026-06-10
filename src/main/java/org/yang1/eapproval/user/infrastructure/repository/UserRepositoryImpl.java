package org.yang1.eapproval.user.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.yang1.eapproval.user.domain.entity.User;
import org.yang1.eapproval.user.domain.repository.UserRepository;
import org.yang1.eapproval.user.infrastructure.repository.jpa.UserJpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;



    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }


    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }


    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }
}
