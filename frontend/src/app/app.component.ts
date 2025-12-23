import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from './core/services/auth.service';
import { UserService } from './core/services/user.service';
import { LoginResponse } from './core/models/user.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule, 
    RouterOutlet, 
    RouterModule,
    MatSidenavModule,
    MatIconModule,
    MatButtonModule,
    MatToolbarModule,
    MatMenuModule,
    MatDividerModule
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Deal Pipeline Management Portal';
  currentUser: LoginResponse | null = null;
  isAdmin = false;
  
  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    // Subscribe to current user changes
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isAdmin = this.authService.isAdmin();
    });
    
    // Validate and refresh user session on app init
    if (this.authService.isAuthenticated()) {
      this.refreshUserSession();
    }
  }
  
  private refreshUserSession(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        // User session is valid, update auth service
        const currentUser = this.authService.getCurrentUser();
        if (currentUser) {
          // Update user info but keep existing token
          this.authService.setCurrentUser({
            ...currentUser,
            username: user.username,
            email: user.email,
            role: user.role
          });
        }
      },
      error: (error) => {
        // Token is invalid or expired, logout
        console.error('Session validation failed:', error);
        this.authService.logout();
      }
    });
  }
  
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
  
  getRoleBadgeText(): string {
    return this.isAdmin ? 'Admin' : 'User';
  }
  
  isLoginPage(): boolean {
    return this.router.url === '/login';
  }
}
