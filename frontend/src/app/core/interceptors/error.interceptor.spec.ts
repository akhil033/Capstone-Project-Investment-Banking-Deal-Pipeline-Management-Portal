import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse, HttpEvent, HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { errorInterceptor } from './error.interceptor';
import { of, throwError } from 'rxjs';

describe('errorInterceptor', () => {
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(() => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['logout']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    });
  });

  it('should handle 401 unauthorized error', () => {
    const request = new HttpRequest('GET', '/api/test');
    const error = new HttpErrorResponse({
      error: 'Unauthorized',
      status: 401,
      statusText: 'Unauthorized'
    });

    const next: HttpHandlerFn = () => throwError(() => error);

    TestBed.runInInjectionContext(() => {
      errorInterceptor(request, next).subscribe({
        error: (err) => {
          expect(err.status).toBe(401);
        },
        complete: () => fail('should have errored')
      });
    });
  });

  it('should handle 403 forbidden error', () => {
    const request = new HttpRequest('GET', '/api/test');
    const error = new HttpErrorResponse({
      error: 'Forbidden',
      status: 403,
      statusText: 'Forbidden'
    });

    const next: HttpHandlerFn = () => throwError(() => error);

    TestBed.runInInjectionContext(() => {
      errorInterceptor(request, next).subscribe({
        error: (err) => {
          expect(err.status).toBe(403);
        },
        complete: () => fail('should have errored')
      });
    });
  });

  it('should handle other HTTP errors', () => {
    const request = new HttpRequest('GET', '/api/test');
    const error = new HttpErrorResponse({
      error: 'Server Error',
      status: 500,
      statusText: 'Internal Server Error'
    });

    const next: HttpHandlerFn = () => throwError(() => error);

    TestBed.runInInjectionContext(() => {
      errorInterceptor(request, next).subscribe({
        error: (err) => {
          expect(err.status).toBe(500);
        },
        complete: () => fail('should have errored')
      });
    });
  });

  it('should pass through successful requests', () => {
    const request = new HttpRequest('GET', '/api/test');
    const response = { data: 'test' };
    const next: HttpHandlerFn = () => of(response as any);

    TestBed.runInInjectionContext(() => {
      errorInterceptor(request, next).subscribe({
        next: (result: any) => {
          expect(result.data).toBe('test');
        }
      });
    });
  });
});
