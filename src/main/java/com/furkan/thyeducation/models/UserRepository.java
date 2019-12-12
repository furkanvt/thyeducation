package com.furkan.thyeducation.models;

import com.furkan.thyeducation.models.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);
    String findByEmail(String email);
}
