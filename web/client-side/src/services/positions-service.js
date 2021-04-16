import axios from "axios";

const API_URL = "http://localhost:8081/positions/last";

const getLastPosition = (user) => {
  let id = user.id;
  return axios.post(API_URL, {
    id
  })
};

export default {getLastPosition};
