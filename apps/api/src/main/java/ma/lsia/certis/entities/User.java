package ma.lsia.certis.entities;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(min = 3, max = 50)
  private String firstName;
  
  @NotBlank
  @Size(min = 3, max = 50)
  private String lastName;
  
  @NotBlank
  @Email
  @Column(unique = true)
  private String email;

  @NotBlank
  @Size(min = 6, max = 255)
  private String password;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDateTime isVerified;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDateTime updatedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDateTime lastLogin;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
