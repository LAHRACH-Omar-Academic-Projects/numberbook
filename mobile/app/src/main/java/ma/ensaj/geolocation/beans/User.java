package ma.ensaj.geolocation.beans;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class User {
    private int id;
    @SerializedName("lastName")
    private String nom;
    @SerializedName("firstName")
    private String prenom;
    @SerializedName("email")
    private String email;
    @SerializedName("phoneNumber")
    private String telephone;
    @SerializedName("deviceImei")
    private String imei;
    @SerializedName("birthday")
    private String dateNaissance;
    @SerializedName("gender")
    private String sexe;
    @SerializedName("online")
    private boolean online;
    @SerializedName("responses")
    private List<FriendingState> responses;
    @SerializedName("requests")
    private List<FriendingState> requests;
    @SerializedName("positions")
    private List<Position> positions;

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
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
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", imei='" + imei + '\'' +
                ", dateNaissance='" + dateNaissance + '\'' +
                ", sexe='" + sexe + '\'' +
                ", online=" + online +
                ", responses=" + responses +
                ", requests=" + requests +
                ", positions=" + positions +
                '}';
    }
}

