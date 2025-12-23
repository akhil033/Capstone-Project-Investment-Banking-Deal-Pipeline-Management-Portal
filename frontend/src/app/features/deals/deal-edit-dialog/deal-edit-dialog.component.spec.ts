import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { DealEditDialogComponent } from './deal-edit-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DealService } from '../../../core/services/deal.service';
import { AuthService } from '../../../core/services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('DealEditDialogComponent', () => {
  let component: DealEditDialogComponent;
  let fixture: ComponentFixture<DealEditDialogComponent>;
  let mockDealService: jasmine.SpyObj<DealService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockDialogRef: jasmine.SpyObj<MatDialogRef<DealEditDialogComponent>>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;

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
    mockDealService = jasmine.createSpyObj('DealService', ['updateDeal', 'updateDealStage', 'updateDealValue', 'addNote']);
    mockAuthService = jasmine.createSpyObj('AuthService', ['isAdmin']);
    mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);

    mockAuthService.isAdmin.and.returnValue(true);

    await TestBed.configureTestingModule({
      imports: [DealEditDialogComponent, NoopAnimationsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: MAT_DIALOG_DATA, useValue: mockDeal },
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: DealService, useValue: mockDealService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DealEditDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize forms with deal data', () => {
    expect(component.basicForm.get('clientName')?.value).toBe('Test Client');
    expect(component.basicForm.get('dealType')?.value).toBe('M&A');
  });

  it('should update basic info successfully', () => {
    mockDealService.updateDeal.and.returnValue(of(mockDeal));
    component.basicForm.patchValue({
      clientName: 'Updated Client',
      dealType: 'M&A',
      sector: 'Technology',
      summary: 'Updated summary'
    });
    component.onUpdateBasic();
    expect(mockDealService.updateDeal).toHaveBeenCalled();
  });

  it('should update stage successfully', () => {
    mockDealService.updateDealStage.and.returnValue(of(mockDeal));
    component.stageForm.patchValue({ stage: 'Closed' });
    component.onUpdateStage();
    expect(mockDealService.updateDealStage).toHaveBeenCalled();
  });

  it('should update deal value if admin', () => {
    mockDealService.updateDealValue.and.returnValue(of(mockDeal));
    component.onUpdateValue();
    expect(mockDealService.updateDealValue).toHaveBeenCalled();
  });

  it('should add note successfully', () => {
    mockDealService.addNote.and.returnValue(of(mockDeal));
    component.noteForm.patchValue({ note: 'Test note' });
    component.onAddNote();
    expect(mockDealService.addNote).toHaveBeenCalled();
  });

  it('should check if user is admin', () => {
    expect(component.isAdmin).toBe(true);
  });

  xit('should handle errors when updating', fakeAsync(() => {
    mockDealService.updateDeal.and.returnValue(throwError(() => ({ error: { message: 'Error' } })));
    component.basicForm.patchValue({
      clientName: 'Updated Client',
      dealType: 'M&A',
      sector: 'Technology',
      summary: 'Updated summary'
    });
    fixture.detectChanges();
    
    component.onUpdateBasic();
    tick();
    
    expect(mockSnackBar.open).toHaveBeenCalled();
  }));
});
