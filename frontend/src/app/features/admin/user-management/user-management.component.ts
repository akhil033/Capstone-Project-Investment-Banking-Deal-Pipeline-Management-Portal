import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AdminService } from '../../../core/services/admin.service';
import { CreateUserDialogComponent } from '../create-user-dialog/create-user-dialog.component';

export interface User {
  id: string;
  username: string;
  email: string;
  role: string;
  active: boolean;
  createdAt: string;
}

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatSlideToggleModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTooltipModule
  ],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  displayedColumns: string[] = ['user', 'role', 'status', 'actions'];
  loading = false;

  constructor(
    private adminService: AdminService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading users:', error);
        this.snackBar.open('Failed to load users', 'Close', { duration: 3000 });
        this.loading = false;
        // Fallback to empty array instead of mock data
        this.users = [];
      }
    });
  }

  toggleUserStatus(user: User) {
    if (this.loading) return;
    
    this.loading = true;
    this.adminService.updateUserStatus(user.id, !user.active).subscribe({
      next: () => {
        user.active = !user.active;
        this.snackBar.open(
          `User ${user.active ? 'activated' : 'deactivated'} successfully`, 
          'Close', 
          { duration: 3000 }
        );
        this.loading = false;
      },
      error: (error) => {
        console.error('Error updating user status:', error);
        this.snackBar.open('Failed to update user status', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  openCreateUserDialog() {
    const dialogRef = this.dialog.open(CreateUserDialogComponent, {
      width: '500px',
      disableClose: false,
      panelClass: 'modern-dialog-panel'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadUsers(); // Reload users after creation
      }
    });
  }
}