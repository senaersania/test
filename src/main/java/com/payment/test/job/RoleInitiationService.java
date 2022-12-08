package com.payment.test.job;

import com.payment.test.models.security.EnumRole;
import com.payment.test.models.security.Role;
import com.payment.test.repository.security.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Component
@Slf4j
public class RoleInitiationService {

    @Autowired
    RoleRepository roleRepository;

    Role roleAdmin = new Role(EnumRole.ROLE_ADMIN);
    Role roleUser = new Role(EnumRole.ROLE_USER);

    @PostConstruct
    public void initRole() {
        log.info("[RoleInitiationService::PostConstruct] start");
        try {
            Optional<Role> roleAdminData = roleRepository.findByRole(roleAdmin.getRole());
            Optional<Role> roleUserData = roleRepository.findByRole(roleUser.getRole());

            if (!roleAdminData.isPresent()) {
                roleRepository.save(roleAdmin);
            }
            if (!roleUserData.isPresent()) {
                roleRepository.save(roleUser);
            }
        } catch (Exception exception) {
            log.info("[RoleInitiationService::Exception] {}", exception);
        }
        log.info("[RoleInitiationService::PostConstruct] end");
    }
}
