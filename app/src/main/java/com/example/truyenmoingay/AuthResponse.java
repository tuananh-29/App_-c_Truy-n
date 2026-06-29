package com.example.truyenmoingay;
public class AuthResponse {
    private boolean success;
    private String message;
    private String token; // Khớp với trường 'token' JWT mà Laravel trả về

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
