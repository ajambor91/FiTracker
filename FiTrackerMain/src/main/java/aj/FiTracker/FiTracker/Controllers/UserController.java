package aj.FiTracker.FiTracker.Controllers;

import aj.FiTracker.FiTracker.DTO.REST.*;
import aj.FiTracker.FiTracker.Services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger logger;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.logger = LoggerFactory.getLogger(UserController.class);
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserRequestResponse> register(@Valid @RequestBody RegisterUserRequestRequest userRequest) {
        logger.info("Received new register request for user with name: {}, and login: {}", userRequest.getName(), userRequest.getLogin());
        RegisterUserRequestResponse user = this.userService.registerUser(userRequest);
        logger.info("User registered successfully: name={}, login={}, id={}", user.getName(), user.getLogin(), user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest userRequest) {
        logger.info("Login attempt for user: login={}", userRequest.getLogin());
        LoginResponse authUser =  this.userService.loginUser(userRequest);
        logger.info("User logged in successfully: login={}, JWT token generated", authUser.getLogin());
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, authUser.getJwt())
                .body(authUser);
    }
    @GetMapping("/user/{id}")
    public ResponseEntity<GetUserResponse> getUser(@PathVariable("id") @NotNull Long id) {
        logger.info("Getting user {}", id);
        GetUserResponse user = this.userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PatchMapping("/user")
    public ResponseEntity<UpdateUserResponse> updateUser(
            @Valid @RequestBody UpdateUserRequest userRequest,
            Authentication authentication) {
        logger.info("Received update user request for: {}", userRequest.getLogin());
        UpdateUserResponse updateUserResponse = this.userService.updateUser(userRequest, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(updateUserResponse);

    }
    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestBody @Valid DeleteUserRequest deleteUserRequest, Authentication authentication) {
        logger.info("Received request for user remove");
        this.userService.deleteUser(deleteUserRequest, authentication);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/find")
    public ResponseEntity<FindUserResponse> findUserByEmail(@RequestParam("email") @NotBlank String email) {
        logger.info("Received new request for finding user by email{}", email);
        FindUserResponse users = this.userService.findUsersByEmail(email);
        logger.info("Found {} users matches with email {}", users.getUserData().size(), email);

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/user/find/multi")
    public ResponseEntity<FindUserResponse> findUsersByIds(@RequestParam("ids") @NotEmpty List<Long> ids) {
        logger.info("Received new request for finding user by ids {}", ids);
        FindUserResponse users = this.userService.findUsersByIds(ids);
        logger.info("Found {} users matches ids {}", users.getUserData().size(), ids);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }



}
