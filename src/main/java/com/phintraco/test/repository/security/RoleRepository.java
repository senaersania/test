package com.phintraco.test.repository.security;

import com.phintraco.test.models.security.EnumRole;
import com.phintraco.test.models.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(EnumRole name);
}
