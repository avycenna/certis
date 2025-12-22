package ma.lsia.certis.enums;

public enum Role {
  // System roles - platform management
  SUDOER,
  STAFF,
  
  // Organization roles - org-level access
  OWNER,
  ADMIN,
  USER;
  
  /**
   * Check if this role is a system-level role
   */
  public boolean isSystemRole() {
    return this == SUDOER || this == STAFF;
  }
  
  /**
   * Check if this role is an organization-level role
   */
  public boolean isOrgRole() {
    return this == OWNER || this == ADMIN || this == USER;
  }
  
  /**
   * Check if this role can manage users (invite, remove, change roles)
   */
  public boolean canManageUsers() {
    return this == SUDOER || this == STAFF || this == OWNER || this == ADMIN;
  }
  
  /**
   * Check if this role can revoke certificates
   * ADMIN and OWNER can revoke any cert in their org
   * System roles can revoke any cert
   */
  public boolean canRevokeCertificates() {
    return this == SUDOER || this == STAFF || this == OWNER || this == ADMIN;
  }
  
  /**
   * Check if this role can modify platform-level settings
   */
  public boolean canManagePlatform() {
    return this == SUDOER || this == STAFF;
  }
  
  /**
   * Check if this role can create certificates
   * All organization roles can create certificates
   */
  public boolean canCreateCertificates() {
    return isOrgRole();
  }
}
