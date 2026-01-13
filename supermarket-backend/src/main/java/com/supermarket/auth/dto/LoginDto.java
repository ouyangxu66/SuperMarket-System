package com.supermarket.auth.dto;

import java.io.Serializable;

public class LoginDto implements Serializable {
    private String username;
    private String password;

    // Getter & Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}