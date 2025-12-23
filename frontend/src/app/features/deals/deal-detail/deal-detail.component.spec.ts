import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DealDetailComponent } from './deal-detail.component';
import { DealService } from '../../../core/services/deal.service';
import { AuthService } from '../../../core/services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('DealDetailComponent', () => {
  let component: DealDetailComponent;
  let fixture: ComponentFixture<DealDetailComponent>;
  let mockDealService: jasmine.SpyObj<DealService>;
  let mockActivatedRoute: any;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  const mockDeal = {
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
  };

  beforeEach(async () => {
    mockDealService = jasmine.createSpyObj('DealService', ['getDealById']);
    mockAuthService = jasmine.createSpyObj('AuthService', ['isAdmin']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: jasmine.createSpy('get').and.returnValue('1')
        }
      }
    };

    mockDealService.getDealById.and.returnValue(of(mockDeal));
    mockAuthService.isAdmin.and.returnValue(false);

    await TestBed.configureTestingModule({
      imports: [DealDetailComponent, NoopAnimationsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: DealService, useValue: mockDealService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DealDetailComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load deal on init', () => {
    fixture.detectChanges();
    expect(mockDealService.getDealById).toHaveBeenCalledWith('1');
    expect(component.deal).toEqual(mockDeal);
  });

  it('should handle error when loading deal', () => {
    mockDealService.getDealById.and.returnValue(throwError(() => new Error('Error')));
    fixture.detectChanges();
    expect(component.loading).toBe(false);
  });

  it('should set loading state correctly', (done) => {
    fixture.detectChanges();
    setTimeout(() => {
      expect(component.loading).toBe(false);
      done();
    }, 100);
  });
});
