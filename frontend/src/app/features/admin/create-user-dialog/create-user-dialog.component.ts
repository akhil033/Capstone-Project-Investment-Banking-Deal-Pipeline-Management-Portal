import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AdminService } from '../../../core/services/admin.service';

@Component({
  selector: 'app-create-user-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './create-user-dialog.component.html',
  styleUrls: ['./create-user-dialog.component.css']
})
export class CreateUserDialogComponent {
  createUserForm: FormGroup;
  loading = false;
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<CreateUserDialogComponent>,
    private snackBar: MatSnackBar,
    private adminService: AdminService
  ) {
    this.createUserForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['USER', Validators.required]
    });
  }

  onSubmit() {
    if (this.createUserForm.valid && !this.loading) {
      this.loading = true;
      const userData = this.createUserForm.value;

      this.adminService.createUser(userData).subscribe({
        next: (response) => {
          this.snackBar.open('User created successfully', 'Close', { duration: 3000 });
          this.dialogRef.close(true);
          this.loading = false;
        },
        error: (error) => {
          console.error('Error creating user:', error);
          this.snackBar.open('Failed to create user', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    }
  }

  // Password requirement validation methods
  hasMinLength(): boolean {
    const password = this.createUserForm.get('password')?.value || '';
    return password.length >= 6;
  }

  hasLetter(): boolean {
    const password = this.createUserForm.get('password')?.value || '';
    return /[a-zA-Z]/.test(password);
  }

  hasNumber(): boolean {
    const password = this.createUserForm.get('password')?.value || '';
    return /\d/.test(password);
  }
}