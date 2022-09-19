package com.phintraco.test.controllers.security;

import com.phintraco.test.models.security.EnumRole;
import com.phintraco.test.models.security.Role;
import com.phintraco.test.models.security.User;
import com.phintraco.test.payload.request.LoginRequest;
import com.phintraco.test.payload.request.SignupRequest;
import com.phintraco.test.payload.response.JwtResponse;
import com.phintraco.test.payload.response.MessageResponse;
import com.phintraco.test.repository.security.RoleRepository;
import com.phintraco.test.repository.security.UserRepository;
import com.phintraco.test.security.JwtUtils;
import com.phintraco.test.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("[AuthControllerLogin::start]");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        log.info("[AuthControllerLogin::end]");
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        log.info("[AuthControllerRegister::start]");
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            log.info("AuthController :: Null Value {} ", strRoles);
            Role userDefaultRole = roleRepository.findByRole(EnumRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userDefaultRole);
            log.info("AuthController :: Roles  {}", roles);
        } else {
            log.info("AuthController :: Value {} ", strRoles);
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRole(EnumRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        user.setRole(adminRole);
                        log.info("AuthController :: Admin {}", roles);

                        break;
                    default:
                        Role userRole = roleRepository.findByRole(EnumRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                        user.setRole(userRole);
                        log.info("AuthController :: Default {}", roles);
                }
            });
        }

        user.setRole(user.getRole());
        userRepository.save(user);

        log.info("[AuthControllerSignUp::end]");
        return ResponseEntity.ok(new MessageResponse("User Registered Successfully!"));
    }
}
