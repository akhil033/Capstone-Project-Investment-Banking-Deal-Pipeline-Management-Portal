import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth.service';
import { LoginRequest, LoginResponse } from '../models/user.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  const API_URL = 'http://localhost:8080/api/auth';

  beforeEach(() => {
    localStorage.clear(); // Clear before creating service
    
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('login', () => {
    it('should login successfully and store token', () => {
      const loginRequest: LoginRequest = { username: 'admin', password: 'admin123' };
      const mockResponse: LoginResponse = {
        token: 'test-jwt-token',
        username: 'admin',
        email: 'admin@test.com',
        role: 'ADMIN'
      };

      service.login(loginRequest).subscribe(response => {
        expect(response).toEqual(mockResponse);
        expect(localStorage.getItem('auth_token')).toBe('test-jwt-token');
        expect(service.getCurrentUser()?.username).toBe('admin');
      });

      const req = httpMock.expectOne(`${API_URL}/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(loginRequest);
      req.flush(mockResponse);
    });

    it('should handle login error', () => {
      const loginRequest: LoginRequest = { username: 'wrong', password: 'wrong' };

      service.login(loginRequest).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne(`${API_URL}/login`);
      req.flush('Invalid credentials', { status: 401, statusText: 'Unauthorized' });
    });
  });

  describe('logout', () => {
    it('should clear local storage on logout', () => {
      localStorage.setItem('auth_token', 'test-token');
      localStorage.setItem('current_user', JSON.stringify({ username: 'testuser', email: 'test@test.com', role: 'USER', token: 'test-token' }));

      service.logout();

      expect(localStorage.getItem('auth_token')).toBeNull();
      expect(localStorage.getItem('current_user')).toBeNull();
      expect(service.getCurrentUser()).toBeNull();
    });
  });

  describe('isAuthenticated', () => {
    it('should return true when token exists', () => {
      localStorage.setItem('auth_token', 'test-token');
      expect(service.isAuthenticated()).toBe(true);
    });

    it('should return false when token does not exist', () => {
      expect(service.isAuthenticated()).toBe(false);
    });
  });

  describe('getToken', () => {
    it('should return token from localStorage', () => {
      localStorage.setItem('auth_token', 'test-token');
      expect(service.getToken()).toBe('test-token');
    });

    it('should return null when no token exists', () => {
      expect(service.getToken()).toBeNull();
    });
  });

  describe('getCurrentUser', () => {
    it('should return user from storage', () => {
      const mockUser = { username: 'testuser', email: 'test@test.com', role: 'USER', token: 'test-token' };
      service.setCurrentUser(mockUser);
      
      expect(service.getCurrentUser()?.username).toBe('testuser');
    });

    it('should return null when no user exists', () => {
      expect(service.getCurrentUser()).toBeNull();
    });
  });

  describe('hasRole', () => {
    it('should return true when user has specified role', () => {
      const mockUser = { username: 'admin', email: 'admin@test.com', role: 'ADMIN', token: 'test-token' };
      service.setCurrentUser(mockUser);
      
      expect(service.hasRole('ADMIN')).toBe(true);
    });

    it('should return false when user has different role', () => {
      const mockUser = { username: 'user', email: 'user@test.com', role: 'USER', token: 'test-token' };
      service.setCurrentUser(mockUser);
      
      expect(service.hasRole('ADMIN')).toBe(false);
    });
  });

  describe('isAdmin', () => {
    it('should return true when role is ADMIN', () => {
      const mockUser = { username: 'admin', email: 'admin@test.com', role: 'ADMIN', token: 'test-token' };
      service.setCurrentUser(mockUser);
      
      expect(service.isAdmin()).toBe(true);
    });

    it('should return false when role is USER', () => {
      const mockUser = { username: 'user', email: 'user@test.com', role: 'USER', token: 'test-token' };
      service.setCurrentUser(mockUser);
      
      expect(service.isAdmin()).toBe(false);
    });

    it('should return false when no user exists', () => {
      expect(service.isAdmin()).toBe(false);
    });
  });
});
