package pkg.comm;

//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-3, Date: 04/28/2022
//*********************************************************

// class to store authentication details securely
public class AuthCred {
    public enum NodeType {
        BBS,
        PUB,
        SUB
    }

    private NodeType nodeType;
    private String username;
    private char[] password;

    public AuthCred(NodeType nodeType, String username, char[] password) {
        this.username = username;
        this.password = password;
        this.nodeType = nodeType;
    }

    public String getUsername() {
        return username;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    // even with object reference you can't get the password but only match it
    // against this function
    public boolean matchPassword(String toMatch) {
        return (toMatch.equals(new String(this.password)));
    }
}