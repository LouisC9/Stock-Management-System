

public class UserInfo {
    private String firstName;
    private String surName;
    private String completeName;
    private String userId;

    public UserInfo(String firstName, String surName) {
        this.firstName = firstName;
        this.surName = surName;
        this.completeName = firstName + " " + surName;
        buildUserId();
    }

    private void buildUserId() {
        if (firstName.contains(" ")) {
            userId = firstName.substring(0,1) + surName;
        } else {
            userId = "guest";
        }
    }

    public String getFirstName()   { return firstName; }
    public String getSurName()     { return surName; }
    public String getCompleteName(){ return completeName; }
    public String getUserId()      { return userId; }

    /** Console preview of user info **/
    public String getInfoString() {
        return String.format("Complete Name: %s%nUser ID: %s", completeName, userId);
    }
}
