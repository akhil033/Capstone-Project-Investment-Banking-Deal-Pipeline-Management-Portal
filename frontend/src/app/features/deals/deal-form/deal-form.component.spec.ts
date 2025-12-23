import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { DealFormComponent } from './deal-form.component';
import { DealService } from '../../../core/services/deal.service';
import { Router, ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';

describe('DealFormComponent', () => {
  let component: DealFormComponent;
  let fixture: ComponentFixture<DealFormComponent>;
  let mockDealService: jasmine.SpyObj<DealService>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;

  beforeEach(async () => {
    mockDealService = jasmine.createSpyObj('DealService', ['createDeal']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);

    const mockActivatedRoute = {
      params: of({}),
      snapshot: { 
        params: {},
        paramMap: {
          get: (key: string) => null
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [DealFormComponent, NoopAnimationsModule, ReactiveFormsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: DealService, useValue: mockDealService },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DealFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with empty values', () => {
    expect(component.dealForm).toBeDefined();
    expect(component.dealForm.get('clientName')?.value).toBe('');
    expect(component.dealForm.get('dealType')?.value).toBe('');
  });

  it('should mark form as invalid when required fields are empty', () => {
    expect(component.dealForm.valid).toBeFalsy();
  });

  it('should mark form as valid when all required fields are filled', () => {
    component.dealForm.patchValue({
      clientName: 'Test Client',
      dealType: 'M&A',
      sector: 'Technology',
      summary: 'Test summary',
      currentStage: 'Prospect'
    });
    expect(component.dealForm.valid).toBeTruthy();
  });

  it('should create deal successfully', () => {
    const mockDeal = {
      clientName: 'Test Client',
      dealType: 'M&A',
      sector: 'Technology',
      summary: 'Test summary',
      currentStage: 'Prospect'
    };
    
    mockDealService.createDeal.and.returnValue(of(mockDeal as any));
    component.dealForm.patchValue(mockDeal);
    component.onSubmit();

    expect(mockDealService.createDeal).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/deals']);
  });

  xit('should handle error when creating deal', fakeAsync(() => {
    mockDealService.createDeal.and.returnValue(throwError(() => ({ error: { message: 'Error' } })));
    component.dealForm.patchValue({
      clientName: 'Test Client',
      dealType: 'M&A',
      sector: 'Technology',
      summary: 'Test summary',
      currentStage: 'Prospect'
    });
    fixture.detectChanges();
    
    component.onSubmit();
    tick();
    
    expect(mockSnackBar.open).toHaveBeenCalled();
  }));
});
