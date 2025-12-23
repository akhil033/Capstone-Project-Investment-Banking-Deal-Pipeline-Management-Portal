import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { LoginComponent } from './features/auth/login/login.component';
import { DealListComponent } from './features/deals/deal-list/deal-list.component';
import { DealFormComponent } from './features/deals/deal-form/deal-form.component';
import { DealDetailComponent } from './features/deals/deal-detail/deal-detail.component';
import { UserManagementComponent } from './features/admin/user-management/user-management.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { 
    path: 'deals', 
    component: DealListComponent,
    canActivate: [authGuard]
  },
  { 
    path: 'deals/new', 
    component: DealFormComponent,
    canActivate: [authGuard]
  },
  { 
    path: 'deals/:id', 
    component: DealDetailComponent,
    canActivate: [authGuard]
  },
  { 
    path: 'deals/:id/edit', 
    component: DealFormComponent,
    canActivate: [authGuard]
  },
  { 
    path: 'admin/users', 
    component: UserManagementComponent,
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMIN' }
  },
  { path: '**', redirectTo: '/login' }
];
