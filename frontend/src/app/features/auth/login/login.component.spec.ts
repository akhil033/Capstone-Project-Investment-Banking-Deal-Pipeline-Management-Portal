import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';
import { LoginResponse } from '../../../core/models/user.model';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  const mockAuthResponse: LoginResponse = {
    token: 'test-token',
    username: 'admin',
    email: 'admin@test.com',
    role: 'ADMIN'
  };

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [
        LoginComponent,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        provideAnimations()
      ]
    }).compileComponents();

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with invalid form', () => {
    expect(component.loginForm.valid).toBe(false);
  });

  it('should validate required fields', () => {
    const username = component.loginForm.get('username');
    const password = component.loginForm.get('password');

    expect(username?.hasError('required')).toBe(true);
    expect(password?.hasError('required')).toBe(true);

    username?.setValue('admin');
    password?.setValue('admin123');

    expect(username?.valid).toBe(true);
    expect(password?.valid).toBe(true);
  });

  it('should login successfully', () => {
    authService.login.and.returnValue(of(mockAuthResponse));

    component.loginForm.patchValue({
      username: 'admin',
      password: 'admin123'
    });

    component.onSubmit();

    expect(authService.login).toHaveBeenCalledWith({
      username: 'admin',
      password: 'admin123'
    });
    expect(router.navigate).toHaveBeenCalledWith(['/deals']);
  });

  it('should handle login error', fakeAsync(() => {
    const errorResponse = { error: { message: 'Invalid credentials' } };
    authService.login.and.returnValue(throwError(() => errorResponse));

    component.loginForm.patchValue({
      username: 'wrong',
      password: 'wrongpassword'
    });

    component.onSubmit();
    tick();

    expect(component.errorMessage).toBe('Invalid credentials');
    expect(component.loading).toBe(false);
  }));

  it('should not submit invalid form', () => {
    component.onSubmit();
    expect(authService.login).not.toHaveBeenCalled();
  });
});
