package ma.ensaj.geolocation.beans;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class FriendingState {
    @SerializedName("id")
    private FriendingStateKey id;
    @SerializedName("requester")
    private User requester;
    @SerializedName("responder")
    private User responder;
    @SerializedName("date")
    private String date;
    @SerializedName("status")
    private int status;

    public FriendingState() {
        id = new FriendingStateKey();
    }

    public FriendingStateKey getId() {
        return id;
    }

    public void setId(FriendingStateKey id) {
        this.id = id;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        id.setRequesterId(requester.getId());
        this.requester = requester;
    }

    public User getResponder() {
        return responder;
    }

    public void setResponder(User responder) {
        id.setResponderId(responder.getId());
        this.responder = responder;
    }

    public String getDate() {
        return date;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDate() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = myDateObj.format(dateTimeFormatter);
        this.date = String.valueOf(formattedDate);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FriendingState{" +
                "id=" + id +
                ", requester=" + requester +
                ", responder=" + responder +
                ", date='" + date + '\'' +
                ", status=" + status +
                '}';
    }
}
