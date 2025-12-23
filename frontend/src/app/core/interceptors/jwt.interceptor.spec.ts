import { TestBed } from '@angular/core/testing';
import { HttpRequest, HttpHandlerFn, HttpResponse } from '@angular/common/http';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { jwtInterceptor } from './jwt.interceptor';
import { of } from 'rxjs';

describe('jwtInterceptor', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should add Authorization header when token exists', (done) => {
    const token = 'test-jwt-token';
    localStorage.setItem('auth_token', token);

    const request = new HttpRequest('GET', '/api/test');
    
    const next: HttpHandlerFn = (req) => {
      try {
        expect(req.headers.has('Authorization')).toBeTruthy();
        expect(req.headers.get('Authorization')).toBe(`Bearer ${token}`);
        done();
      } catch (e: any) {
        done.fail(e);
      }
      return of(new HttpResponse({ status: 200 }));
    };

    TestBed.runInInjectionContext(() => {
      jwtInterceptor(request, next).subscribe({
        next: () => {},
        error: (e: any) => done.fail(e)
      });
    });
  });

  it('should not add Authorization header when token does not exist', (done) => {
    const request = new HttpRequest('GET', '/api/test');
    
    const next: HttpHandlerFn = (req) => {
      try {
        expect(req.headers.has('Authorization')).toBeFalsy();
        done();
      } catch (e: any) {
        done.fail(e);
      }
      return of({} as any);
    };

    TestBed.runInInjectionContext(() => {
      jwtInterceptor(request, next).subscribe({
        error: (e: any) => done.fail(e)
      });
    });
  });

  it('should add token to POST requests', (done) => {
    const token = 'test-jwt-token';
    localStorage.setItem('auth_token', token);

    const request = new HttpRequest('POST', '/api/deals', {});
    
    const next: HttpHandlerFn = (req) => {
      try {
        expect(req.headers.has('Authorization')).toBeTruthy();
        expect(req.headers.get('Authorization')).toBe(`Bearer ${token}`);
        done();
      } catch (e: any) {
        done.fail(e);
      }
      return of(new HttpResponse({ status: 200 }));
    };

    TestBed.runInInjectionContext(() => {
      jwtInterceptor(request, next).subscribe({
        next: () => {},
        error: (e: any) => done.fail(e)
      });
    });
  });

  it('should clone request with Authorization header', (done) => {
    const token = 'test-jwt-token';
    localStorage.setItem('auth_token', token);

    const request = new HttpRequest('GET', '/api/test');
    
    const next: HttpHandlerFn = (req) => {
      try {
        // Check that the headers are modified (which means it was cloned)
        expect(req.headers.has('Authorization')).toBeTruthy();
        expect(req.url).toBe(request.url);
        expect(req.method).toBe(request.method);
        done();
      } catch (e: any) {
        done.fail(e);
      }
      return of(new HttpResponse({ status: 200 }));
    };

    TestBed.runInInjectionContext(() => {
      jwtInterceptor(request, next).subscribe({
        next: () => {},
        error: (e: any) => done.fail(e)
      });
    });
  });
});
