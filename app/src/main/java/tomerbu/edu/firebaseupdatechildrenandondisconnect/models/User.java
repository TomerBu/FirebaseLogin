package tomerbu.edu.firebaseupdatechildrenandondisconnect.models;

/**
 * Created by stud27 on 20/07/16.
 */
public class User extends BaseModel{
    private boolean connected;
    private String email;
    private String UID;




    public User(String uid, String email, boolean connected) {
        this.UID = uid;
        this.email = email;
        this.connected = connected;
    }

    public User(String UID, String email) {
        this.UID = UID;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public boolean getConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}