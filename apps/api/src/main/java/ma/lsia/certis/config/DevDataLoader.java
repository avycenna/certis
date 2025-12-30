package ma.lsia.certis.config;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import ma.lsia.certis.entities.Certificate;
import ma.lsia.certis.entities.Organization;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.enums.Role;
import ma.lsia.certis.entities.Course;
import ma.lsia.certis.repos.CertificateRepository;
import ma.lsia.certis.repos.OrganizationRepository;
import ma.lsia.certis.repos.UserRepository;
import ma.lsia.certis.repos.CourseRepository;

/**
 * Development data loader for seeding the database with test data.
 * 
 * Note: JPA Auditing is automatically handled by AuditorAware.
 * During data loading, there's no authenticated user, so createdBy will be null.
 * This is expected behavior for seed data.
 * 
 * Activated with profile: dev1
 */
@Configuration
@Profile("test")
@Slf4j
public class DevDataLoader {

  @Bean
  CommandLineRunner loadDevData(
      UserRepository userRepository,
      CertificateRepository certificateRepository,
      OrganizationRepository organizationRepository,
      CourseRepository courseRepository,
      PasswordEncoder passwordEncoder,
      EntityManager entityManager) {
    return args -> {
      // Clear existing data
      certificateRepository.deleteAll();
      courseRepository.deleteAll();
      organizationRepository.deleteAll();
      userRepository.deleteAll();
      entityManager.clear(); // Clear persistence context
      log.info("Cleared existing data from H2 database");

      // Note: createdAt/updatedAt will be set automatically by @CreatedDate/@LastModifiedDate
      // createdBy will be null since there's no authenticated user during data loading

      User admin = new User();
      admin.setFirstName("Admin");
      admin.setLastName("User");
      admin.setEmail("admin@certis.ma");
      admin.setPassword(passwordEncoder.encode("Test123!@$"));
      admin.setVerifiedAt(LocalDateTime.now());
      admin.setRole(Role.USER); // Will be updated to OWNER when org is created
      admin = userRepository.save(admin);
      log.info("Created admin user: {}", admin.getEmail());

      User john = new User();
      john.setFirstName("John");
      john.setLastName("Doe");
      john.setEmail("john.doe@example.com");
      john.setPassword(passwordEncoder.encode("Test123!@$"));
      john.setVerifiedAt(LocalDateTime.now());
      john.setRole(Role.USER); // Will be updated to OWNER when org is created
      john = userRepository.save(john);
      log.info("Created test user: {}", john.getEmail());

      User jane = new User();
      jane.setFirstName("Jane");
      jane.setLastName("Smith");
      jane.setEmail("jane.smith@example.com");
      jane.setPassword(passwordEncoder.encode("Test123!@$"));
      jane.setRole(Role.USER);
      jane.setVerifiedAt(LocalDateTime.now());
      jane = userRepository.save(jane);
      log.info("Created test user: {}", jane.getEmail());

      User bob = new User();
      bob.setFirstName("Bob");
      bob.setLastName("Johnson");
      bob.setEmail("bob.johnson@example.com");
      bob.setRole(Role.USER);
      bob.setPassword(passwordEncoder.encode("Test123!@$"));
      bob = userRepository.save(bob);
      log.info("Created unverified test user: {}", bob.getEmail());

      // Create sample organizations
      Organization exampleOrg = new Organization();
      exampleOrg.setName("Example Organization");
      exampleOrg.setDesc("Example organization for development testing");
      exampleOrg.setDomain("example.com");
      exampleOrg = organizationRepository.save(exampleOrg);
      
      // Set admin's organization reference and role
      admin.setOrganization(exampleOrg);
      admin.setRole(Role.OWNER);
      admin.setJoinedAt(LocalDateTime.now());
      admin = userRepository.save(admin);
      log.info("Created organization: {} with owner: {}", exampleOrg.getName(), admin.getEmail());
      
      Organization certisOrg = new Organization();
      certisOrg.setName("Certis Platform");
      certisOrg.setDesc("Certis certificate management platform");
      certisOrg.setDomain("certis.ma");
      certisOrg = organizationRepository.save(certisOrg);
      
      // Set john's organization reference and role
      john.setOrganization(certisOrg);
      john.setRole(Role.OWNER);
      john.setJoinedAt(LocalDateTime.now());
      john = userRepository.save(john);
      log.info("Created organization: {} with owner: {}", certisOrg.getName(), john.getEmail());

      // Create sample courses for each org
      // Note: createdBy will be null since no user is authenticated during data loading
      // This is expected for seed data. In production, courses are created via authenticated requests.
      Course course1 = new Course();
      course1.setTitle("Intro to PKI");
      course1.setDescription("Public Key Infrastructure basics");
      course1.setSlug("intro-to-pki");
      course1.setIsActive(true);
      course1.setOrganization(exampleOrg);
      // Don't set createdBy, createdAt, updatedAt - they're handled by JPA auditing
      course1 = courseRepository.save(course1);
      log.info("Created course: {} (createdBy will be null for seed data)", course1.getTitle());

      Course course2 = new Course();
      course2.setTitle("Advanced Cert Management");
      course2.setDescription("Managing certificates at scale");
      course2.setSlug("advanced-cert-mgmt");
      course2.setIsActive(true);
      course2.setOrganization(certisOrg);
      // Don't set createdBy, createdAt, updatedAt - they're handled by JPA auditing
      course2 = courseRepository.save(course2);
      log.info("Created course: {} (createdBy will be null for seed data)", course2.getTitle());

      // Create sample certificates
      // Note: createdBy will be null since no user is authenticated during data loading
      Certificate cert1 = new Certificate();
      cert1.setSerialNumber(generateSerialNumber());
      cert1.setIssuer(admin);
      cert1.setOrganization(exampleOrg);
      cert1.setCourse(course1);
      cert1.setSubject("CN=example.com, O=Example Org, C=US");
      cert1.setActiveFrom(LocalDateTime.now().minusDays(30));
      cert1.setActiveTo(LocalDateTime.now().plusYears(1));
      cert1.setIsRevoked(false);
      certificateRepository.save(cert1);
      log.info("Created certificate: {} (createdBy will be null for seed data)", cert1.getSerialNumber());

      Certificate cert2 = new Certificate();
      cert2.setSerialNumber(generateSerialNumber());
      cert2.setIssuer(john);
      cert2.setSubject("CN=api.example.com, O=Example Org, C=US");
      cert2.setActiveFrom(LocalDateTime.now().minusDays(15));
      cert2.setActiveTo(LocalDateTime.now().plusMonths(6));
      cert2.setOrganization(exampleOrg);
      cert2.setCourse(course1);
      cert2.setIsRevoked(false);
      certificateRepository.save(cert2);
      log.info("Created certificate: {} (createdBy will be null for seed data)", cert2.getSerialNumber());

      Certificate cert3 = new Certificate();
      cert3.setSerialNumber(generateSerialNumber());
      cert3.setOrganization(certisOrg);
      cert3.setIssuer(admin);
      cert3.setCourse(course2);
      cert3.setSubject("CN=old.example.com, O=Example Org, C=US");
      cert3.setActiveFrom(LocalDateTime.now().minusYears(2));
      cert3.setActiveTo(LocalDateTime.now().minusDays(10));
      cert3.setIsRevoked(true);
      certificateRepository.save(cert3);
      cert3.setRevocationReason("Certificate expired and revoked");
      certificateRepository.save(cert3);
      log.info("Created revoked certificate: {} (createdBy will be null for seed data)", cert3.getSerialNumber());
      log.info("=====================OWNER, verified)");
      log.info("  - john.doe@example.===================");
      log.info("Test Accounts (password: Test123!@$):");
      log.info("  - admin@certis.ma (OWNER, verified)");
      log.info("  - john.doe@example.com (OWNER, verified)");
      log.info("  - jane.smith@example.com (USER, verified)");
      log.info("  - bob.johnson@example.com (USER, not verified)");
      log.info("========================================");
      log.info("Organizations created: 2");
      log.info("  - Example Organization (example.com) - Owner: admin@certis.ma");
      log.info("  - Certis Platform (certis.ma) - Owner: john.doe@example.com");
      log.info("========================================");
      log.info("Courses created: 2");
      log.info("  - Intro to PKI (Example Organization)");
      log.info("  - Advanced Cert Management (Certis Platform)");
      log.info("========================================");
      log.info("Certificates created: 3 (2 active, 1 revoked)");
      log.info("========================================");
      log.info("NOTE: Seed data has null createdBy fields (no authenticated user during loading)");
      log.info("========================================");
    };
  }

  private String generateSerialNumber() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
  }

}
