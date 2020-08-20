package com.davidlong.http.model.seq.multi;

public class LoginUserInfo {
    private String usernameKey="username";
    private String passwordKey="password";
    private String usernameVal;
    private String passwordVal;

    public LoginUserInfo(String usernameVal, String passwordVal) {
        this.usernameVal = usernameVal;
        this.passwordVal = passwordVal;
    }

    public LoginUserInfo(String usernameKey, String passwordKey, String usernameVal, String passwordVal) {
        this.usernameKey = usernameKey;
        this.passwordKey = passwordKey;
        this.usernameVal = usernameVal;
        this.passwordVal = passwordVal;
    }

    public LoginUserInfo() {
    }

    public String getUsernameKey() {
        return usernameKey;
    }

    public void setUsernameKey(String usernameKey) {
        this.usernameKey = usernameKey;
    }

    public String getPasswordKey() {
        return passwordKey;
    }

    public void setPasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
    }

    public String getUsernameVal() {
        return usernameVal;
    }

    public void setUsernameVal(String usernameVal) {
        this.usernameVal = usernameVal;
    }

    public String getPasswordVal() {
        return passwordVal;
    }

    public void setPasswordVal(String passwordVal) {
        this.passwordVal = passwordVal;
    }
}
