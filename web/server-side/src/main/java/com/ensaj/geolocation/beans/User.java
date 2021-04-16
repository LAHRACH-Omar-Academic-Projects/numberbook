package com.ensaj.geolocation.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String lastName;
	private String firstName;
	private String email;
	private String phoneNumber;
	private String deviceImei;
	private String birthday;
	private String gender;
	private boolean online;

	@OneToMany(mappedBy = "requester", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnoreProperties({"id", "requester"})
	List<FriendingState> responses;

	@OneToMany(mappedBy = "responder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnoreProperties({"id", "responder"})
	List<FriendingState> requests;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	List<Position> positions;
	
	public User() {
		this.requests = new ArrayList<>();
		this.responses = new ArrayList<>();
		this.positions = new ArrayList<>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDeviceImei() {
		return deviceImei;
	}

	public void setDeviceImei(String deviceImei) {
		this.deviceImei = deviceImei;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public List<FriendingState> getResponses() {
		return responses;
	}

	public void setResponses(List<FriendingState> responses) {
		this.responses = responses;
	}

	public List<FriendingState> getRequests() {
		return requests;
	}

	public void setRequests(List<FriendingState> requests) {
		this.requests = requests;
	}

	public List<Position> getPositions() {
		return positions;
	}

	public void setPositions(List<Position> positions) {
		this.positions = positions;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", lastName='" + lastName + '\'' +
				", firstName='" + firstName + '\'' +
				", email='" + email + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", deviceImei='" + deviceImei + '\'' +
				", birthday='" + birthday + '\'' +
				", gender='" + gender + '\'' +
				", online=" + online +
				", responses=" + responses +
				", requests=" + requests +
				", positions=" + positions +
				'}';
	}
}

