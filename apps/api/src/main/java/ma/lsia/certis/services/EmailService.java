package ma.lsia.certis.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ma.lsia.certis.enums.Role;

@Service
@Slf4j
public class EmailService {
  
  private final JavaMailSender mailSender;
  
  @Value("${mail.from}")
  private String fromEmail;
  
  @Value("${invitation.base-url}")
  private String baseUrl;
  
  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }
  
  /**
   * Send an invitation email to a user
   */
  public void sendInvitationEmail(String toEmail, String token, String organizationName, Role role) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(fromEmail);
      message.setTo(toEmail);
      message.setSubject("Invitation to join " + organizationName + " on Certis");
      message.setText(buildInvitationEmailBody(token, organizationName, role));
      
      mailSender.send(message);
      log.info("Invitation email sent to: {}", toEmail);
    } catch (Exception e) {
      log.error("Failed to send invitation email to: {}", toEmail, e);
      throw new RuntimeException("Failed to send invitation email", e);
    }
  }
  
  /**
   * Build the invitation email body
   */
  private String buildInvitationEmailBody(String token, String organizationName, Role role) {
    String acceptUrl = baseUrl + "/invitations/accept?token=" + token;
    
    return String.format(
      "Hello,\n\n" +
      "You have been invited to join %s as a %s on Certis.\n\n" +
      "Click the link below to accept this invitation:\n" +
      "%s\n\n" +
      "This invitation will expire in 7 days.\n\n" +
      "If you did not expect this invitation, please ignore this email.\n\n" +
      "Best regards,\n" +
      "The Certis Team",
      organizationName,
      role.name(),
      acceptUrl
    );
  }
  
  /**
   * Send a welcome email after user accepts invitation
   */
  public void sendWelcomeEmail(String toEmail, String organizationName) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(fromEmail);
      message.setTo(toEmail);
      message.setSubject("Welcome to " + organizationName);
      message.setText(String.format(
        "Welcome to %s!\n\n" +
        "You have successfully joined the organization on Certis.\n\n" +
        "You can now log in and start using the platform.\n\n" +
        "Best regards,\n" +
        "The Certis Team",
        organizationName
      ));
      
      mailSender.send(message);
      log.info("Welcome email sent to: {}", toEmail);
    } catch (Exception e) {
      log.error("Failed to send welcome email to: {}", toEmail, e);
      // Don't throw - welcome email is not critical
    }
  }
}
