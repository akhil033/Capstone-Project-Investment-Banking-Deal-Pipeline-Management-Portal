export enum Role {
  USER = 'USER',
  ADMIN = 'ADMIN'
}

export interface User {
  id: string;
  username: string;
  email: string;
  role: Role;
  active: boolean;
  createdAt: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  email: string;
  role: string;
}

export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  role: Role;
}
