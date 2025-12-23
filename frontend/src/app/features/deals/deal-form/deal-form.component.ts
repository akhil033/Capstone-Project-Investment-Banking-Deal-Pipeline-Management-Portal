import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { DealService } from '../../../core/services/deal.service';
import { AuthService } from '../../../core/services/auth.service';
import { DealType, DealStage, CreateDealRequest, UpdateDealRequest } from '../../../core/models/deal.model';

@Component({
  selector: 'app-deal-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatCardModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatIconModule
  ],
  templateUrl: './deal-form.component.html',
  styleUrls: ['./deal-form.component.css']
})
export class DealFormComponent implements OnInit {
  dealForm!: FormGroup;
  isEditMode = false;
  dealId: string | null = null;
  loading = false;
  isAdmin = false;
  
  dealTypes = Object.values(DealType);
  dealStages = Object.values(DealStage);
  
  constructor(
    private fb: FormBuilder,
    private dealService: DealService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.isAdmin = this.authService.isAdmin();
  }
  
  ngOnInit(): void {
    this.dealId = this.route.snapshot.paramMap.get('id');
    this.isEditMode = !!this.dealId;
    
    this.initForm();
    
    if (this.isEditMode && this.dealId) {
      this.loadDeal(this.dealId);
    }
  }
  
  private initForm(): void {
    this.dealForm = this.fb.group({
      clientName: ['', [Validators.required, Validators.minLength(2)]],
      dealType: ['', Validators.required],
      sector: ['', Validators.required],
      dealValue: [{ value: '', disabled: !this.isAdmin }, [Validators.required, Validators.min(0)]],
      currentStage: [{ value: DealStage.Prospect, disabled: this.isEditMode }, Validators.required],
      summary: ['', [Validators.required, Validators.minLength(10)]],
      assignedTo: ['']
    });
  }
  
  private loadDeal(id: string): void {
    this.loading = true;
    this.dealService.getDealById(id).subscribe({
      next: (deal) => {
        this.dealForm.patchValue({
          clientName: deal.clientName,
          dealType: deal.dealType,
          sector: deal.sector,
          dealValue: deal.dealValue,
          currentStage: deal.currentStage,
          summary: deal.summary,
          assignedTo: deal.assignedTo
        });
        this.loading = false;
      },
      error: (error) => {
        this.snackBar.open('Failed to load deal', 'Close', { duration: 3000 });
        this.loading = false;
        this.router.navigate(['/deals']);
      }
    });
  }
  
  onSubmit(): void {
    if (this.dealForm.invalid) {
      Object.keys(this.dealForm.controls).forEach(key => {
        this.dealForm.get(key)?.markAsTouched();
      });
      return;
    }
    
    this.loading = true;
    const formValue = this.dealForm.getRawValue(); // getRawValue gets disabled fields too
    
    if (this.isEditMode && this.dealId) {
      const updateRequest: UpdateDealRequest = {
        clientName: formValue.clientName,
        dealType: formValue.dealType,
        sector: formValue.sector,
        summary: formValue.summary,
        assignedTo: formValue.assignedTo
      };
      
      this.dealService.updateDeal(this.dealId, updateRequest).subscribe({
        next: () => {
          this.snackBar.open('Deal updated successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/deals']);
        },
        error: (error) => {
          this.snackBar.open(error.error?.message || 'Failed to update deal', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    } else {
      const createRequest: CreateDealRequest = {
        clientName: formValue.clientName,
        dealType: formValue.dealType,
        sector: formValue.sector,
        dealValue: formValue.dealValue,
        currentStage: formValue.currentStage,
        summary: formValue.summary
      };
      
      this.dealService.createDeal(createRequest).subscribe({
        next: () => {
          this.snackBar.open('Deal created successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/deals']);
        },
        error: (error) => {
          this.snackBar.open(error.error?.message || 'Failed to create deal', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    }
  }
  
  onCancel(): void {
    this.router.navigate(['/deals']);
  }

  formatStageName(stage: DealStage | string): string {
    const nameMap: { [key: string]: string } = {
      'Prospect': 'Prospect',
      'UnderEvaluation': 'Under Evaluation',
      'TermSheetSubmitted': 'Term Sheet Submitted',
      'Closed': 'Closed',
      'Lost': 'Lost'
    };
    return nameMap[String(stage)] || String(stage);
  }
  
  getErrorMessage(fieldName: string): string {
    const control = this.dealForm.get(fieldName);
    if (control?.hasError('required')) {
      return `${fieldName} is required`;
    }
    if (control?.hasError('minlength')) {
      const minLength = control.errors?.['minlength'].requiredLength;
      return `${fieldName} must be at least ${minLength} characters`;
    }
    if (control?.hasError('min')) {
      return `${fieldName} must be greater than 0`;
    }
    return '';
  }
}
