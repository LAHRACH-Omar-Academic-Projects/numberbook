package ma.ensaj.geolocation.beans;

import com.google.gson.annotations.SerializedName;

public class Config {
    private int id;
    @SerializedName("gender")
    private String gender;
    @SerializedName("rayon")
    private int rayon;

    public Config() {

    }

    public Config(int id, String gender, int rayon) {
        this.id = id;
        this.gender = gender;
        this.rayon = rayon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getRayon() {
        return rayon;
    }

    public void setRayon(int rayon) {
        this.rayon = rayon;
    }

    @Override
    public String toString() {
        return "Config{" +
                "id=" + id +
                ", gender='" + gender + '\'' +
                ", rayon=" + rayon +
                '}';
    }
}
