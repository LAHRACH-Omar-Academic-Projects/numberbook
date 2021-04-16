package com.ensaj.geolocation.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

@Entity
public class FriendingState {
    @EmbeddedId
    FriendingStateKey id;

    @ManyToOne
    @MapsId("requesterId")
    @JoinColumn(name = "requester_id")
    @JsonIgnoreProperties({"requests", "responses"})
    User requester;

    @ManyToOne
    @MapsId("responderId")
    @JoinColumn(name = "responder_id")
    @JsonIgnoreProperties({"requests", "responses"})
    User responder;

    @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

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
        this.requester = requester;
    }

    public User getResponder() {
        return responder;
    }

    public void setResponder(User responder) {
        this.responder = responder;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
                ", date=" + date +
                ", status=" + status +
                '}';
    }
}
