package model;

/**
 * Created by mifercre on 10/02/15.
 */
public class User {

    private String fbName;
    private String fbId;

    public User(String fbName, String fbId) {
        this.fbName = fbName;
        this.fbId = fbId;
    }

    public String getFbName() {
        return fbName;
    }

    public void setFbName(String fbName) {
        this.fbName = fbName;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }
}
