package pkg.broker;

// class to store authentication details securely
class AuthCred {
    private String nodeType;
    private String username;
    private char[] password;

    public AuthCred(String nodeType, String username, char[] password) {
        this.username = username;
        this.password = password;
        this.nodeType = nodeType;
    }

    public String getUsername() {
        return username;
    }

    public String getnodeType() {
        return nodeType;
    }

    // even with object reference you can't get the password but only match it
    // against this function
    public boolean matchPassword(String toMatch) {
        return (toMatch.equals(new String(this.password)));
    }
}