package com.raj.ecommerce.repo;

import com.raj.ecommerce.domain.Role;
import com.raj.ecommerce.domain.User;
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
}
