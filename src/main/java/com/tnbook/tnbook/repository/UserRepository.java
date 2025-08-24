package com.tnbook.tnbook.repository;

import com.tnbook.tnbook.model.entity.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @NonNull
    Optional<User> findById(@NonNull Long id);
}
