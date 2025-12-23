import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { RouterTestingModule } from '@angular/router/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './core/services/auth.service';
import { of } from 'rxjs';

describe('AppComponent', () => {
  let mockAuthService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['isLoggedIn', 'getCurrentUser', 'isAuthenticated'], {
      currentUser$: of(null)
    });
    mockAuthService.isAuthenticated.and.returnValue(false);

    await TestBed.configureTestingModule({
      imports: [AppComponent, RouterTestingModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should have router-outlet', () => {
    mockAuthService.isAuthenticated.and.returnValue(false);
    mockAuthService.currentUser$ = of(null);
    const fixture = TestBed.createComponent(AppComponent);
    // Simulate being on login page
    Object.defineProperty(fixture.componentInstance['router'], 'url', {
      get: () => '/login',
      configurable: true
    });
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    const routerOutlet = compiled.querySelector('router-outlet');
    expect(routerOutlet).toBeTruthy();
    // Router outlet should be inside login-wrapper when not authenticated
    const loginWrapper = compiled.querySelector('.login-wrapper');
    expect(loginWrapper).toBeTruthy();
  });

  it('should render the layout structure', () => {
    mockAuthService.isAuthenticated.and.returnValue(false);
    mockAuthService.currentUser$ = of(null);
    const fixture = TestBed.createComponent(AppComponent);
    // Simulate being on login page
    Object.defineProperty(fixture.componentInstance['router'], 'url', {
      get: () => '/login',
      configurable: true
    });
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    // When not authenticated, should have login-wrapper
    const loginWrapper = compiled.querySelector('.login-wrapper');
    expect(loginWrapper).toBeTruthy();
  });
});
