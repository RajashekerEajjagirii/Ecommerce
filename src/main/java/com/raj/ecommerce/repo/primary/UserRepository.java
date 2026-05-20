package com.raj.ecommerce.repo.primary;

import com.raj.ecommerce.domain.primary.Role;
import com.raj.ecommerce.domain.primary.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Custom query to fetch roles by user email
     @Query("SELECT r FROM User u JOIN u.roles r WHERE u.email = :email")
     Set<Role> findRolesByEmail(@Param("email") String email);

    Optional<User> findByOtp(int emailOtp);
}
