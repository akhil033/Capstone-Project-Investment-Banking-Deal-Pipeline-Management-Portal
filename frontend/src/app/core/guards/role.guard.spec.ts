import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot } from '@angular/router';
import { roleGuard } from './role.guard';
import { AuthService } from '../services/auth.service';

describe('roleGuard', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;
  let mockRoute: Partial<ActivatedRouteSnapshot>;

  beforeEach(() => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['hasRole']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    
    mockRoute = {
      data: { role: 'ADMIN' }
    };
  });

  it('should allow access when user has required role', () => {
    authService.hasRole.and.returnValue(true);

    TestBed.runInInjectionContext(() => {
      const result = roleGuard(mockRoute as ActivatedRouteSnapshot, null as any);
      expect(result).toBe(true);
      expect(authService.hasRole).toHaveBeenCalledWith('ADMIN');
    });
  });

  it('should redirect to deals when user does not have required role', () => {
    authService.hasRole.and.returnValue(false);

    TestBed.runInInjectionContext(() => {
      const result = roleGuard(mockRoute as ActivatedRouteSnapshot, null as any);
      expect(result).toBe(false);
      expect(router.navigate).toHaveBeenCalledWith(['/deals']);
    });
  });
});
