import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DealListComponent } from './deal-list.component';
import { DealService } from '../../../core/services/deal.service';
import { AuthService } from '../../../core/services/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('DealListComponent', () => {
  let component: DealListComponent;
  let fixture: ComponentFixture<DealListComponent>;
  let mockDealService: jasmine.SpyObj<DealService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockDialog: jasmine.SpyObj<MatDialog>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;

  const mockDeals = [
    {
      id: '1',
      clientName: 'Test Client',
      dealType: 'M&A',
      sector: 'Technology',
      currentStage: 'Prospect',
      summary: 'Test deal',
      dealValue: 1000000,
      createdBy: 'user1',
      assignedTo: 'user1',
      notes: [],
      createdAt: '2024-01-01',
      updatedAt: '2024-01-01'
    }
  ];

  beforeEach(async () => {
    mockDealService = jasmine.createSpyObj('DealService', ['getAllDeals', 'deleteDeal']);
    mockAuthService = jasmine.createSpyObj('AuthService', ['isAdmin']);
    mockDialog = jasmine.createSpyObj('MatDialog', ['open']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);

    mockDealService.getAllDeals.and.returnValue(of(mockDeals));
    mockAuthService.isAdmin.and.returnValue(true);

    await TestBed.configureTestingModule({
      imports: [DealListComponent, NoopAnimationsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: DealService, useValue: mockDealService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: MatDialog, useValue: mockDialog },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DealListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load deals on init', () => {
    fixture.detectChanges();
    expect(mockDealService.getAllDeals).toHaveBeenCalled();
    expect(component.deals.length).toBe(1);
  });

  it('should navigate to create deal', () => {
    component.createDeal();
    expect(component).toBeTruthy();
  });

  it('should handle error when loading deals', () => {
    mockDealService.getAllDeals.and.returnValue(throwError(() => new Error('Error')));
    fixture.detectChanges();
    expect(component.loading).toBe(false);
  });

  it('should check if user is admin', () => {
    fixture.detectChanges();
    expect(component.isAdmin).toBe(true);
  });
});
