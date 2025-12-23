import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AdminService, User, CreateUserRequest } from './admin.service';

describe('AdminService', () => {
  let service: AdminService;
  let httpMock: HttpTestingController;
  const API_URL = 'http://localhost:8080/api/admin';

  const mockUser: User = {
    id: '1',
    username: 'testuser',
    email: 'test@test.com',
    role: 'USER',
    active: true,
    createdAt: '2024-01-01T00:00:00'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AdminService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AdminService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAllUsers', () => {
    it('should fetch all users', () => {
      const mockUsers: User[] = [mockUser];

      service.getAllUsers().subscribe(users => {
        expect(users).toEqual(mockUsers);
        expect(users.length).toBe(1);
      });

      const req = httpMock.expectOne(`${API_URL}/users`);
      expect(req.request.method).toBe('GET');
      req.flush(mockUsers);
    });
  });

  describe('createUser', () => {
    it('should create a new user', () => {
      const createRequest: CreateUserRequest = {
        username: 'newuser',
        email: 'new@test.com',
        password: 'password123',
        role: 'USER'
      };

      service.createUser(createRequest).subscribe(user => {
        expect(user.username).toBe('newuser');
        expect(user.email).toBe('new@test.com');
      });

      const req = httpMock.expectOne(`${API_URL}/users`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(createRequest);
      req.flush({ ...mockUser, username: 'newuser', email: 'new@test.com' });
    });
  });
});
