 package com.vendora.user.dto;
 
public class AuthUserDetailsResponse {
 
    private Long id;
    private String email;
    private String passwordHash;
    private String role;
    private String accountStatus;
 
    public AuthUserDetailsResponse(Long id, String email,
            String passwordHash, String role, String accountStatus) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.accountStatus = accountStatus;
    }
 
    public Long getId() {
        return id;
    }
 
    public String getEmail() {
        return email;
    }
 
    public String getPasswordHash() {
        return passwordHash;
    }
 
    public String getRole() {
        return role;
    }
 
    public String getAccountStatus() {
        return accountStatus;
    }
}
 