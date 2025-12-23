import { TestBed } from '@angular/core/testing';
import { UserService } from './user.service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const mockUser = {
    id: '1',
    username: 'testuser',
    email: 'test@example.com',
    role: 'USER',
    active: true,
    createdAt: '2024-01-01'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UserService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get current user profile', () => {
    service.getCurrentUser().subscribe(user => {
      expect(user.username).toBe(mockUser.username);
      expect(user.email).toBe(mockUser.email);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/users/me`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);
  });

  it('should handle error when getting current user', () => {
    service.getCurrentUser().subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(404);
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/users/me`);
    req.flush('Not Found', { status: 404, statusText: 'Not Found' });
  });
});
