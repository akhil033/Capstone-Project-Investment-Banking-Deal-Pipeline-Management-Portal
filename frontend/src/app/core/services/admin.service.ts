import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  role: string;
}

export interface User {
  id: string;
  username: string;
  email: string;
  role: string;
  active: boolean;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/users`);
  }

  createUser(userData: CreateUserRequest): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/users`, userData);
  }

  updateUserStatus(userId: string, active: boolean): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/users/${userId}/status?active=${active}`, {});
  }
}
