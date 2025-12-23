import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserManagementComponent } from './user-management.component';
import { AdminService } from '../../../core/services/admin.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('UserManagementComponent', () => {
  let component: UserManagementComponent;
  let fixture: ComponentFixture<UserManagementComponent>;
  let mockAdminService: jasmine.SpyObj<AdminService>;
  let mockDialog: jasmine.SpyObj<MatDialog>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;

  const mockUsers = [
    {
      id: '1',
      username: 'user1',
      email: 'user1@test.com',
      role: 'USER',
      active: true,
      createdAt: '2024-01-01'
    },
    {
      id: '2',
      username: 'admin',
      email: 'admin@test.com',
      role: 'ADMIN',
      active: true,
      createdAt: '2024-01-01'
    }
  ];

  beforeEach(async () => {
    mockAdminService = jasmine.createSpyObj('AdminService', ['getAllUsers', 'updateUserStatus']);
    mockDialog = jasmine.createSpyObj('MatDialog', ['open']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);

    mockAdminService.getAllUsers.and.returnValue(of(mockUsers));

    await TestBed.configureTestingModule({
      imports: [UserManagementComponent, NoopAnimationsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AdminService, useValue: mockAdminService },
        { provide: MatDialog, useValue: mockDialog },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserManagementComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load users on init', () => {
    fixture.detectChanges();
    expect(mockAdminService.getAllUsers).toHaveBeenCalled();
    expect(component.users.length).toBe(2);
  });

  it('should toggle user status successfully', () => {
    const user = { ...mockUsers[0] };
    mockAdminService.updateUserStatus.and.returnValue(of(null as any));
    
    fixture.detectChanges();
    component.toggleUserStatus(user);

    expect(mockAdminService.updateUserStatus).toHaveBeenCalledWith('1', false);
    expect(user.active).toBe(false);
  });

  it('should handle error when loading users', () => {
    mockAdminService.getAllUsers.and.returnValue(throwError(() => new Error('Error')));
    fixture.detectChanges();
    expect(component.users.length).toBe(0);
  });

  it('should have create user dialog method', () => {
    expect(component.openCreateUserDialog).toBeDefined();
  });

  it('should display correct number of columns', () => {
    expect(component.displayedColumns.length).toBe(4);
    expect(component.displayedColumns).toContain('user');
    expect(component.displayedColumns).toContain('role');
    expect(component.displayedColumns).toContain('status');
    expect(component.displayedColumns).toContain('actions');
  });
});
