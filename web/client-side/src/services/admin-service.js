import axios from "axios";

const API_URL = "http://localhost:8081/admins/auth/";

const login = (username, password) => {
  return axios
    .post(API_URL + "login", {
      username,
      password
    })
    .then((response) => {
      localStorage.setItem("admin", JSON.stringify(response.data));
    });
};

const logout = () => {
  localStorage.removeItem("admin");
};

const auth = {
    login,
    logout
  }

export default auth;
