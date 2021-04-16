package ma.ensaj.geolocation.services;

import java.util.List;

import ma.ensaj.geolocation.beans.Config;
import ma.ensaj.geolocation.beans.FriendingState;
import ma.ensaj.geolocation.beans.Position;
import ma.ensaj.geolocation.beans.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DataService {

    @GET("/users/findUserByDeviceImei/{deviceImei}")
    Call<User> getUserByDeviceImei(@Path("deviceImei") String deviceImei);

    @GET("/users/findUserByPhoneNumber/{phoneNumber}")
    Call<User> getUserByTelephone(@Path("phoneNumber") String phoneNumber);

    @POST("/users/save")
    Call<User> saveUser(@Body User user);

    @PUT("/users/edit")
    Call<User> updateUser(@Body User user);

    @POST("/friendingStates/save")
    Call<Boolean> addFriend(@Body FriendingState friendingState);

    @POST("/friendingStates/refuse")
    Call<Boolean> refuseFriend(@Body FriendingState friendingState);


    @POST("/positions/save")
    Call<Boolean> savePosition(@Body Position position);

    @POST("/positions/last")
    Call<Position> getLastPosition(@Body User user);

    @POST("/friendingStates/friends/all")
    Call<List<User>> getAllFriends(@Body User user);

    @POST("/config/save")
    Call<Boolean> saveConfig(@Body Config config);

    @GET("/config/{id}")
    Call<Config> getConfigById(@Path("id") int id);
}
