package com.ensaj.geolocation.beans;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class FriendingStateKey implements Serializable {
    @Column(name = "requester_id")
    int requesterId;

    @Column(name = "responder_id")
    int responderId;

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public int getResponderId() {
        return responderId;
    }

    public void setResponderId(int responderId) {
        this.responderId = responderId;
    }

    @Override
    public String toString() {
        return "FriendingStateKey{" +
                "requesterId=" + requesterId +
                ", responderId=" + responderId +
                '}';
    }
}
