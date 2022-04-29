package com.bluebelt.fulfillment.controller;

import com.bluebelt.fulfillment.exception.AppException;
import com.bluebelt.fulfillment.exception.FulfillmentapiException;
import com.bluebelt.fulfillment.model.role.Role;
import com.bluebelt.fulfillment.model.role.RoleName;
import com.bluebelt.fulfillment.model.user.Info;
import com.bluebelt.fulfillment.model.user.User;
import com.bluebelt.fulfillment.payload.request.LoginRequest;
import com.bluebelt.fulfillment.payload.request.SignUpRequest;
import com.bluebelt.fulfillment.payload.response.ApiResponse;
import com.bluebelt.fulfillment.payload.response.JwtAuthenticationResponse;
import com.bluebelt.fulfillment.repository.RoleRepository;
import com.bluebelt.fulfillment.repository.UserRepository;
import com.bluebelt.fulfillment.security.JwtTokenProvider;
import com.bluebelt.fulfillment.security.UserPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bluebelt.fulfillment.utils.AppConstants.*;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleDAO;

    /**
     * Đánh dấu object với @Valid để validator tự động kiểm tra object đó có hợp lệ hay không
     */
    /**
     * [POST] /api/v1/auth/signin
     * @param loginRequest
     * @return ResponseEntity
     */
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // Xác thực từ username và password.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        // Nếu không xảy ra exception tức là thông tin hợp lệ
        // Set thông tin authentication vào Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Trả về jwt cho người dùng.
        String jwt = jwtTokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse(jwt, userPrincipal.getUsername(),
                userPrincipal.getEmail(), roles);
        return ResponseEntity
                .ok(jwtAuthenticationResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (Boolean.TRUE.equals(userDAO.existsByUsername(signUpRequest.getUsername()))) {
            throw new FulfillmentapiException(HttpStatus.BAD_REQUEST, USERNAME_IS_ALREADY);
        }

        if (Boolean.TRUE.equals(userDAO.existsByEmail(signUpRequest.getEmail()))) {
            throw new FulfillmentapiException(HttpStatus.BAD_REQUEST, EMAIL_IS_ALREADY);
        }

        String username = signUpRequest.getUsername().toLowerCase();

        String email = signUpRequest.getEmail().toLowerCase();

        String password = passwordEncoder.encode(signUpRequest.getPassword());

        User user = new User().builder().username(username).email(email).password(password).build();

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if(strRoles == null) {
            Role userRole = roleDAO.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new AppException(ROLE_IS_NOT_FOUND));

            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleDAO.findByName(RoleName.ROLE_ADMIN)
                                .orElseThrow(() -> new AppException(ROLE_IS_NOT_FOUND));

                        roles.add(adminRole);

                        break;

                    case "moderator":
                        Role moderatorRole = roleDAO.findByName(RoleName.ROLE_MODERATOR)
                                .orElseThrow(() -> new AppException(ROLE_IS_NOT_FOUND));

                        roles.add(moderatorRole);

                        break;

                    default:
                        Role userRole = roleDAO.findByName(RoleName.ROLE_USER)
                                .orElseThrow(() -> new AppException(ROLE_IS_NOT_FOUND));

                        roles.add(userRole);

                        break;
                }
            });
        }

        user.setRoles(roles);

        User result = userDAO.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{userId}")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(Boolean.TRUE, "User registered successfully"));
    }

}
