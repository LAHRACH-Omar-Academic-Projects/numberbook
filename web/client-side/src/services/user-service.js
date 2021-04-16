import axios from "axios";

const API_URL = "http://localhost:8081/users/";

const getAllUsers = () => {
  return axios.get(API_URL + "all")
};

export default {getAllUsers};
