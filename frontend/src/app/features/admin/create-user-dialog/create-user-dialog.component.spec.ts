import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CreateUserDialogComponent } from './create-user-dialog.component';
import { MatDialogRef } from '@angular/material/dialog';
import { AdminService } from '../../../core/services/admin.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';

describe('CreateUserDialogComponent', () => {
  let component: CreateUserDialogComponent;
  let fixture: ComponentFixture<CreateUserDialogComponent>;
  let mockAdminService: jasmine.SpyObj<AdminService>;
  let mockDialogRef: jasmine.SpyObj<MatDialogRef<CreateUserDialogComponent>>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;

  beforeEach(async () => {
    mockAdminService = jasmine.createSpyObj('AdminService', ['createUser']);
    mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [CreateUserDialogComponent, NoopAnimationsModule, ReactiveFormsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: AdminService, useValue: mockAdminService },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateUserDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with empty values', () => {
    expect(component.createUserForm).toBeDefined();
    expect(component.createUserForm.get('username')?.value).toBe('');
    expect(component.createUserForm.get('email')?.value).toBe('');
    expect(component.createUserForm.get('password')?.value).toBe('');
  });

  it('should mark form as invalid when required fields are empty', () => {
    component.createUserForm.patchValue({ username: '', email: '', password: '', role: '' });
    expect(component.createUserForm.valid).toBeFalsy();
  });

  it('should mark form as valid when all required fields are filled', () => {
    component.createUserForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'Test123',
      role: 'User'
    });
    expect(component.createUserForm.valid).toBeTruthy();
  });

  it('should create user successfully', () => {
    const mockUser = {
      username: 'testuser',
      email: 'test@example.com',
      password: 'Test123',
      role: 'User'
    };
    
    mockAdminService.createUser.and.returnValue(of(mockUser as any));
    component.createUserForm.patchValue(mockUser);
    component.onSubmit();

    expect(mockAdminService.createUser).toHaveBeenCalled();
    expect(mockDialogRef.close).toHaveBeenCalledWith(true);
  });

  xit('should handle error when creating user', fakeAsync(() => {
    mockAdminService.createUser.and.returnValue(throwError(() => ({ error: { message: 'Error' } })));
    component.createUserForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'Test123',
      role: 'USER'
    });
    // Mark form as touched to trigger validations
    component.createUserForm.markAllAsTouched();
    fixture.detectChanges();
    
    component.onSubmit();
    tick();
    
    expect(mockSnackBar.open).toHaveBeenCalled();
  }));

  it('should validate email format', () => {
    const emailControl = component.createUserForm.get('email');
    emailControl?.setValue('invalid-email');
    expect(emailControl?.hasError('email')).toBeTruthy();
    
    emailControl?.setValue('valid@email.com');
    expect(emailControl?.hasError('email')).toBeFalsy();
  });

  it('should validate password min length', () => {
    const passwordControl = component.createUserForm.get('password');
    passwordControl?.setValue('123');
    expect(passwordControl?.hasError('minlength')).toBeTruthy();
    
    passwordControl?.setValue('Test123');
    expect(passwordControl?.hasError('minlength')).toBeFalsy();
  });

  it('should toggle password visibility', () => {
    expect(component.hidePassword).toBe(true);
    component.hidePassword = false;
    expect(component.hidePassword).toBe(false);
  });
});
