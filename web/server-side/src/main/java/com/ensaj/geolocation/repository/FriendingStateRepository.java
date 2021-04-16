package com.ensaj.geolocation.repository;

import com.ensaj.geolocation.beans.FriendingState;
import com.ensaj.geolocation.beans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendingStateRepository extends JpaRepository<FriendingState, Integer> {
    List<FriendingState> findAllByStatusAndResponder(int status, User responder);
    List<FriendingState> findAllByStatusAndRequester(int status, User requester);
}
