package com.ensaj.geolocation.controllers;

import com.ensaj.geolocation.beans.FriendingState;
import com.ensaj.geolocation.beans.User;
import com.ensaj.geolocation.repository.FriendingStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("friendingStates")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FriendingStateController {
    @Autowired
    FriendingStateRepository friendingStateRepository;

    private final int NOT_FRIEND = 0;
    private final int WAITING_FOR_FRIENDSHIP_ACCEPTANCE = 1;
    private final int FRIEND = 2;
    private final int BLOCKED = 3;

    @PostMapping("/friends/all")
    public List<User> findAllFriendsByUser(@RequestBody User user) {
        List<FriendingState> friendingStates1 = friendingStateRepository.findAllByStatusAndRequester(FRIEND, user);
        List<FriendingState> friendingStates2 = friendingStateRepository.findAllByStatusAndResponder(FRIEND, user);

        List<User> friends = new ArrayList<>();

        for(FriendingState friendingState: friendingStates1) {
            friends.add(friendingState.getResponder());
        }
        for(FriendingState friendingState: friendingStates2) {
            friends.add(friendingState.getRequester());
        }

        System.out.println(friends.size());

        return friends;
    }

    @PostMapping("/save")
    public boolean addFriendingState(@RequestBody FriendingState friendingState){
        try {
            System.out.println(friendingState.getId().toString());
            friendingStateRepository.save(friendingState);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/delete")
    public boolean deleteFriendingState(@RequestBody FriendingState friendingState) {
        try {
            friendingStateRepository.delete(friendingState);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
