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

    User user = new User();
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    
    return userRepo.save(user);
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
