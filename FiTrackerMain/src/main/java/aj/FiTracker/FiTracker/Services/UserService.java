package aj.FiTracker.FiTracker.Services;

import aj.FiTracker.FiTracker.DTO.REST.*;
import aj.FiTracker.FiTracker.Entities.User;
import aj.FiTracker.FiTracker.Exceptions.InternalServerException;
import aj.FiTracker.FiTracker.Exceptions.UserAlreadyExistsException;
import aj.FiTracker.FiTracker.Exceptions.UserDoesntExistException;
import aj.FiTracker.FiTracker.Exceptions.UserUnauthorizedException;
import aj.FiTracker.FiTracker.Models.MemberTemplate;
import aj.FiTracker.FiTracker.Models.UserImpl;
import aj.FiTracker.FiTracker.Repositories.UserRepository;
import aj.FiTracker.FiTracker.Security.JWTClaimsUtil;
import aj.FiTracker.FiTracker.Security.JWTService;
import aj.FiTracker.FiTracker.Security.PasswordEncoder;
import aj.FiTracker.FiTracker.Interfaces.UserInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class UserService {
    private final Logger logger;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public UserService(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            JWTService jwtService,
            KafkaProducerService kafkaProducerService
    ) {
        this.logger = LoggerFactory.getLogger(UserService.class);
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Transactional
    public RegisterUserRequestResponse registerUser(RegisterUserRequestRequest registerUserRequest) {
        Objects.requireNonNull(registerUserRequest, "RegisterUserRequestRequest cannot be null (programmer error)");
        User user = new User(registerUserRequest);
        logger.info("Created new User: name={}, login={}", user.getName(), user.getLogin());
        user = this.passwordEncoder.prepareForRegister(user);
        logger.info("Password hashed for User: name={}, login={}", user.getName(), user.getLogin());
        try {
            User savedUser = this.userRepository.saveAndFlush(user);
            logger.info("User saved successfully: name={}, login={}, userId={}", user.getName(), user.getLogin(), savedUser.getId());
            return new RegisterUserRequestResponse(user);
        } catch (DataIntegrityViolationException e) {
            logger.error("User already exists: name={}, login={}. Error: {}", user.getName(), user.getLogin(), e.getMessage());
            throw new UserAlreadyExistsException(e);
        } catch (Exception e) {
            logger.error("Failed to save user: name={}, login={}. Error: {}", user.getName(), user.getLogin(), e.getMessage());
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public LoginResponse loginUser(LoginRequest loginRequest) {
        Objects.requireNonNull(loginRequest, "LoginRequest cannot be null (programmer error)");
        UserInterface userToAuth = new UserImpl(loginRequest);
        logger.info("Attempting to log in user with login: {}", userToAuth.getLogin());
        try {
            logger.info("Searching for user in the database: login={}", userToAuth.getLogin());
            Optional<User> savedUser = this.userRepository.findOneByLogin(userToAuth.getLogin());
            if (savedUser.isEmpty()) {
                logger.warn("User not found in the database: login={}", userToAuth.getLogin());
                throw new UserDoesntExistException("User with login " + userToAuth.getLogin() + " does not exist.");
            }
            User user = savedUser.get();
            logger.info("User found in the database: login={}, userId={}", user.getLogin(), user.getId());
            logger.info("Verifying password for user: login={}", user.getLogin());
            if (!this.passwordEncoder.checkPass(userToAuth, user)) {
                logger.warn("Password verification failed for user: login={}", user.getLogin());
                throw new UserUnauthorizedException("Incorrect password for user " + user.getLogin());
            }
            logger.info("Password verified successfully for user: login={}. Generating JWT token...", user.getLogin());
            user = this.jwtService.generateToken(user);
            logger.info("JWT token generated successfully for user: login={}", user.getLogin());
            return new LoginResponse(user);

        } catch (UserDoesntExistException | UserUnauthorizedException e) {
            logger.error("Login failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during login for user: login={}. Error: {}", userToAuth.getLogin(), e.getMessage(), e);
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public FindUserResponse findUsersByEmail(String email) {
        logger.info("Finding users by email: email={}", email);
        try {
            String regexp = "^" + email;
             List<UserInterface> userList = this.userRepository.findUsersByEmail(regexp).stream().map(user -> (UserInterface) user).toList();

            return new FindUserResponse(userList);
        } catch (Exception e) {
            logger.error("Error while finding users by email: email={}. Error: {}", email, e.getMessage(), e);
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public FindUserResponse findUsersByIds(List<Long> ids) {
        logger.info("Finding users by IDs: ids={}", ids);
        try {
            List<UserInterface> users = this.userRepository.findUsersByIds(ids).stream().map(user -> (UserInterface) user).toList();
            return new FindUserResponse(users);
        } catch (Exception e) {
            logger.error("Error while finding users by IDs: ids={}. Error: {}", ids, e.getMessage(), e);
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public GetUserResponse getUser(long id) {
        logger.info("Getting user {}", id);
        try {
            Optional<User> optionalUser = this.userRepository.findOneById(id);
            if (optionalUser.isEmpty()) {
                throw new UserDoesntExistException("Cannot find user " + id);
            }
            return new GetUserResponse(optionalUser.get());

        } catch (UserDoesntExistException userDoesntExistException) {
            logger.error("Cannot find user with id {}", id);
            throw  userDoesntExistException;
        } catch (Exception e) {
            logger.error("Unexpected error {}" , e.getMessage());
            throw new InternalServerException(e);
        }
    }

    @Transactional()
    public UpdateUserResponse updateUser(UpdateUserRequest userRequest, Authentication authentication) {
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            Optional<User> optionalUser = this.userRepository.findOneById(jwtClaims.userId());
            if (optionalUser.isEmpty()) {
                throw new UserDoesntExistException("Cannot find user " + jwtClaims.userId());
            }
            User user = optionalUser.get();
            UserInterface userToUpdate = new UserImpl(userRequest);
            if (!this.passwordEncoder.checkPass(userToUpdate, user)) {
                throw new UserUnauthorizedException("Incorrect password for user " + jwtClaims.userId());
            }
            user.updateUser(userToUpdate);
            this.userRepository.save(user);
            return new UpdateUserResponse(user);
        } catch (UserDoesntExistException | UserUnauthorizedException exception) {
            logger.error("User credentials error {}",exception.getMessage());
            throw exception;
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage());
            throw new InternalServerException(e);
        }
    }

    @Transactional()
    public void deleteUser(DeleteUserRequest deleteUserRequest, Authentication authentication) {
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            Optional<User> optionalUser = this.userRepository.findOneById(jwtClaims.userId());
            if (optionalUser.isEmpty()) {
                throw new UserDoesntExistException("Cannot find user " + jwtClaims.userId());
            }
            User user = optionalUser.get();
            UserInterface userToDelete = new UserImpl(deleteUserRequest, jwtClaims.userId());
            if (!this.passwordEncoder.checkPass(userToDelete, user)) {
                throw new UserUnauthorizedException("Incorrect password for user " + jwtClaims.userId());
            }
            this.userRepository.deleteById(jwtClaims.userId());
            this.kafkaProducerService.sendDeletedMember(new MemberTemplate(userToDelete));
        } catch (UserDoesntExistException | UserUnauthorizedException exception) {
            logger.error("User credentials error {}",exception.getMessage());
            throw exception;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }
}
