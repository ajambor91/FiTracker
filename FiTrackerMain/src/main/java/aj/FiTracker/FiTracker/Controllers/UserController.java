package aj.FiTracker.FiTracker.Controllers;

import aj.FiTracker.FiTracker.DTO.REST.LoginRequest;
import aj.FiTracker.FiTracker.DTO.REST.LoginResponseSuccess;
import aj.FiTracker.FiTracker.DTO.REST.RegisterSuccess;
import aj.FiTracker.FiTracker.DTO.REST.RegisterUserRequest;
import aj.FiTracker.FiTracker.Entities.User;

import aj.FiTracker.FiTracker.Services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<RegisterSuccess> register(@Valid @RequestBody RegisterUserRequest userRequest) {
        logger.info("Received new register request for user with name: {}, and login: {}", userRequest.getName(), userRequest.getLogin());
        User user = this.userService.registerUser(userRequest);
        logger.info("User registered successfully: name={}, login={}, id={}", user.getName(), user.getLogin(), user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterSuccess(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseSuccess> login(@Valid @RequestBody LoginRequest userRequest) {
        logger.info("Login attempt for user: login={}", userRequest.getLogin());
        User authUser = this.userService.loginUser(userRequest);
        logger.info("User logged in successfully: login={}, JWT token generated", authUser.getLogin());
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, authUser.getJwt())
                .body(new LoginResponseSuccess(authUser, "Login successfully"));
    }
}
