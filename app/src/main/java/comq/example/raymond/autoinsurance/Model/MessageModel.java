package comq.example.raymond.autoinsurance.Model;

public class MessageModel {
    private String uId, userName, message;
    private long dateSent;

    public MessageModel() {
    }

    public MessageModel(String uId, String userName, String message, long dateSent) {
        this.uId = uId;
        this.userName = userName;
        this.message = message;
        this.dateSent = dateSent;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDateSent() {
        return dateSent;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }
}
