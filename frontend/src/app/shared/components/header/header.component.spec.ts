import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HeaderComponent } from './header.component';
import { AuthService } from '../../../core/services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let router: Router;

  const mockUser = {
    id: '1',
    username: 'testuser',
    email: 'test@example.com',
    role: 'USER',
    token: 'mock-token'
  };

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['logout', 'isAdmin'], {
      currentUser$: of(mockUser)
    });

    mockAuthService.isAdmin.and.returnValue(false);

    const mockActivatedRoute = {
      params: of({}),
      snapshot: { params: {} }
    };

    await TestBed.configureTestingModule({
      imports: [HeaderComponent, NoopAnimationsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([
          { path: 'deals', component: HeaderComponent },
          { path: 'admin/users', component: HeaderComponent }
        ]),
        { provide: AuthService, useValue: mockAuthService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set current user on init', () => {
    fixture.detectChanges();
    expect(component.currentUser).toEqual(mockUser);
  });

  it('should check if user is admin', () => {
    fixture.detectChanges();
    expect(component.isAdmin).toBe(false);
  });

  it('should return correct role badge text for user', () => {
    component.isAdmin = false;
    expect(component.getRoleBadgeText()).toBe('USER');
  });

  it('should return correct role badge text for admin', () => {
    component.isAdmin = true;
    expect(component.getRoleBadgeText()).toBe('ADMIN');
  });

  it('should logout and navigate to login page', () => {
    spyOn(router, 'navigate');
    component.logout();
    expect(mockAuthService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should display header only when user is logged in', () => {
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('mat-toolbar')).toBeTruthy();
  });

  it('should hide admin menu item for non-admin users', () => {
    mockAuthService.isAdmin.and.returnValue(false);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    const adminButton = Array.from(compiled.querySelectorAll('button')).find(
      button => button.textContent?.includes('User Management')
    );
    expect(adminButton).toBeFalsy();
  });
});
