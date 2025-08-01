import axios from "axios";
const jwtToken = localStorage.getItem("jwt")
export const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:5000';


export const api = axios.create({
  baseURL: API_BASE_URL, 
  headers: {
    'Authorization':`Bearer ${jwtToken}`,
    'Content-Type': 'application/json',
  },
});