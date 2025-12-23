package ma.lsia.certis.config;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;
import ma.lsia.certis.entities.Certificate;
import ma.lsia.certis.entities.Organization;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.entities.Course;
import ma.lsia.certis.repos.CertificateRepository;
import ma.lsia.certis.repos.OrganizationRepository;
import ma.lsia.certis.repos.UserRepository;
import ma.lsia.certis.repos.CourseRepository;

@Configuration
@Profile("dev")
@Slf4j
public class DevDataLoader {

  @Bean
  CommandLineRunner loadDevData(
      UserRepository userRepository,
      CertificateRepository certificateRepository,
      OrganizationRepository organizationRepository,
      CourseRepository courseRepository,
      PasswordEncoder passwordEncoder) {
    return args -> {
      // Clear existing data
      certificateRepository.deleteAll();
      organizationRepository.deleteAll();
      userRepository.deleteAll();
      log.info("Cleared existing data from H2 database");

      User admin = new User();
      admin.setFirstName("Admin");
      admin.setLastName("User");
      admin.setEmail("admin@certis.ma");
      admin.setPassword(passwordEncoder.encode("Test123!@$"));
      admin.setIsVerified(LocalDateTime.now());
      admin = userRepository.save(admin);
      log.info("Created admin user: {}", admin.getEmail());

      User john = new User();
      john.setFirstName("John");
      john.setLastName("Doe");
      john.setEmail("john.doe@example.com");
      john.setPassword(passwordEncoder.encode("Test123!@$"));
      john.setIsVerified(LocalDateTime.now());
      john = userRepository.save(john);
      log.info("Created test user: {}", john.getEmail());

      User jane = new User();
      jane.setFirstName("Jane");
      jane.setLastName("Smith");
      jane.setEmail("jane.smith@example.com");
      jane.setPassword(passwordEncoder.encode("Test123!@$"));
      jane.setIsVerified(LocalDateTime.now());
      jane = userRepository.save(jane);
      log.info("Created test user: {}", jane.getEmail());

      User bob = new User();
      bob.setFirstName("Bob");
      bob.setLastName("Johnson");
      bob.setEmail("bob.johnson@example.com");
      bob.setPassword(passwordEncoder.encode("Test123!@$"));
      bob = userRepository.save(bob);
      log.info("Created unverified test user: {}", bob.getEmail());

      // Create sample organizations
      Organization exampleOrg = new Organization();
      exampleOrg.setName("Example Organization");
      exampleOrg.setDesc("Example organization for development testing");
      exampleOrg.setDomain("example.com");
      exampleOrg.setOwner(admin);
      exampleOrg = organizationRepository.save(exampleOrg);
      log.info("Created organization: {}", exampleOrg.getName());

      Organization certisOrg = new Organization();
      certisOrg.setName("Certis Platform");
      certisOrg.setDesc("Certis certificate management platform");
      certisOrg.setDomain("certis.ma");
      certisOrg.setOwner(john);
      certisOrg = organizationRepository.save(certisOrg);
      log.info("Created organization: {}", certisOrg.getName());

      // Create sample courses for each org
      Course course1 = new Course();
      course1.setTitle("Intro to PKI");
      course1.setDescription("Public Key Infrastructure basics");
      course1.setSlug("intro-to-pki");
      course1.setIsActive(true);
      course1.setOrganization(exampleOrg);
      course1.setCreatedBy(admin);
      course1.setCreatedAt(LocalDateTime.now());
      course1.setUpdatedAt(LocalDateTime.now());
      course1 = courseRepository.save(course1);
      log.info("Created course: {}", course1.getTitle());

      Course course2 = new Course();
      course2.setTitle("Advanced Cert Management");
      course2.setDescription("Managing certificates at scale");
      course2.setSlug("advanced-cert-mgmt");
      course2.setIsActive(true);
      course2.setOrganization(certisOrg);
      course2.setCreatedBy(john);
      course2.setCreatedAt(LocalDateTime.now());
      course2.setUpdatedAt(LocalDateTime.now());
      course2 = courseRepository.save(course2);
      log.info("Created course: {}", course2.getTitle());

      // Create sample certificates
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
      log.info("Created certificate: {}", cert1.getSerialNumber());

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
      log.info("Created certificate: {}", cert2.getSerialNumber());

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
      log.info("Created revoked certificate: {}", cert3.getSerialNumber());

      log.info("========================================");
      log.info("Dev data loaded successfully!");
      log.info("========================================");
      log.info("Test Accounts (password: Test123!@$):");
      log.info("  - admin@certis.ma (verified)");
      log.info("  - john.doe@example.com (verified)");
      log.info("  - jane.smith@example.com (verified)");
      log.info("  - bob.johnson@example.com (not verified)");
      log.info("Organizations created: 2");
      log.info("  - Example Organization (example.com)");
      log.info("  - Certis Platform (certis.ma)");
      log.info("========================================");
      log.info("Courses created: 2");
      log.info("  - Intro to PKI (Example Organization)");
      log.info("  - Advanced Cert Management (Certis Platform)");
      log.info("========================================");
      log.info("Certificates created: 3 (2====");
      log.info("Certificates created: 3 (1 active, 1 active, 1 revoked)");
      log.info("========================================");
    };
  }

  private String generateSerialNumber() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
  }

}
