package ma.lsia.certis.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ma.lsia.certis.dto.RegisterRequest;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.repos.UserRepository;

@Service
public class UserService {
  private final UserRepository userRepo;
  private final PasswordEncoder passwordEncoder;
  
  public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }
  
  @Transactional
  public User createUser(RegisterRequest request) {
    // Check if user already exists
    if (userRepo.findByEmail(request.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Email already registered");
    }

    // Additional password validation (belt-and-suspenders approach)
    validatePassword(request.getPassword());

    User user = new User();
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    
    return userRepo.save(user);
  }

  private void validatePassword(String password) {
    if (password == null || password.length() < 8) {
      throw new IllegalArgumentException("Password must be at least 8 characters long");
    }
    
    if (!password.matches(".*[A-Z].*")) {
      throw new IllegalArgumentException("Password must contain at least one uppercase letter");
    }
    
    if (!password.matches(".*[a-z].*")) {
      throw new IllegalArgumentException("Password must contain at least one lowercase letter");
    }
    
    if (!password.matches(".*\\d.*")) {
      throw new IllegalArgumentException("Password must contain at least one number");
    }
    
    if (!password.matches(".*[@$!%*?&].*")) {
      throw new IllegalArgumentException("Password must contain at least one special character (@$!%*?&)");
    }
  }

  @Transactional(readOnly = true)
  public Optional<User> getUserById(@NonNull Long id) {
    return userRepo.findById(id);
  }

  @Transactional(readOnly = true)
  public Optional<User> getUserByEmail(@NonNull String email) {
    return userRepo.findByEmail(email);
  }

  @Transactional(readOnly = true)
  public Optional<User> getVerifiedUserById(@NonNull Long id) {
    return userRepo.findByIdAndIsVerifiedIsNotNull(id);
  }

  @Transactional(readOnly = true)
  public Optional<User> getVerifiedUserByEmail(@NonNull String email) {
    return userRepo.findByEmailAndIsVerifiedIsNotNull(email);
  }

  @Transactional
  public void updateLastLogin(@NonNull Long userId) {
    userRepo.findById(userId).ifPresent(user -> {
      user.setLastLogin(LocalDateTime.now());
      userRepo.save(user);
    });
  }

  @Transactional
  public User updateUser(@NonNull User user) {
    return userRepo.save(user);
  }

  @Transactional
  public void deleteUser(@NonNull Long userId) {
    userRepo.deleteById(userId);
  }
}
