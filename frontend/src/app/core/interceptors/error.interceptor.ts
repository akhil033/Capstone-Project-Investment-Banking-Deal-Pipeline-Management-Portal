import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);
  
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Only logout and redirect for 401 errors on non-auth endpoints
      // Auth endpoints should handle their own errors
      if (error.status === 401 && !req.url.includes('/auth/')) {
        // Token is invalid or expired
        authService.logout();
        router.navigate(['/login']);
      }
      
      // For 403 Forbidden, show error but don't logout
      if (error.status === 403) {
        console.error('Access denied:', error.error?.message || 'You do not have permission to access this resource');
      }
      
      return throwError(() => error);
    })
  );
};
