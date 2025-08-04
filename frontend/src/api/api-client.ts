import axios, { type AxiosRequestConfig } from 'axios';

if (!import.meta.env.VITE_API_URL) {
  throw new Error('VITE_API_URL is not defined');
}

const options: AxiosRequestConfig = {
  baseURL: import.meta.env.VITE_API_URL,
  withCredentials: true,
};

const API = axios.create(options);

export default API;
