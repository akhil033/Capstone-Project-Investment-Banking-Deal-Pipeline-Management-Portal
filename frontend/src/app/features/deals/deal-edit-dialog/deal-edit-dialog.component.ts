import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DealService } from '../../../core/services/deal.service';
import { AuthService } from '../../../core/services/auth.service';
import { Deal, DealType, DealStage, UpdateDealRequest, UpdateDealStageRequest, UpdateDealValueRequest, AddNoteRequest } from '../../../core/models/deal.model';

@Component({
  selector: 'app-deal-edit-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatSnackBarModule
  ],
  templateUrl: './deal-edit-dialog.component.html',
  styleUrls: ['./deal-edit-dialog.component.css']
})
export class DealEditDialogComponent implements OnInit {
  basicForm!: FormGroup;
  stageForm!: FormGroup;
  valueForm!: FormGroup;
  noteForm!: FormGroup;
  
  dealTypes = Object.values(DealType);
  dealStages = Object.values(DealStage);
  isAdmin = false;
  saving = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public deal: Deal,
    private dialogRef: MatDialogRef<DealEditDialogComponent>,
    private fb: FormBuilder,
    private dealService: DealService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.isAdmin = this.authService.isAdmin();
  }

  ngOnInit(): void {
    this.initForms();
  }

  private initForms(): void {
    this.basicForm = this.fb.group({
      clientName: [this.deal.clientName, [Validators.required, Validators.minLength(2)]],
      dealType: [this.deal.dealType, Validators.required],
      sector: [this.deal.sector, Validators.required],
      summary: [this.deal.summary, [Validators.required, Validators.minLength(10)]],
      assignedTo: [this.deal.assignedTo]
    });

    this.stageForm = this.fb.group({
      stage: [this.deal.currentStage, Validators.required]
    });

    this.valueForm = this.fb.group({
      dealValue: [this.deal.dealValue || '', [Validators.required, Validators.min(1)]]
    });

    this.noteForm = this.fb.group({
      note: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(500)]]
    });
  }

  onUpdateBasic(): void {
    if (this.basicForm.invalid) {
      this.basicForm.markAllAsTouched();
      return;
    }

    this.saving = true;
    const request: UpdateDealRequest = this.basicForm.value;

    this.dealService.updateDeal(this.deal.id, request).subscribe({
      next: (updatedDeal) => {
        this.snackBar.open('Basic information updated successfully', 'Close', { duration: 3000 });
        this.dialogRef.close(updatedDeal);
      },
      error: (error) => {
        this.snackBar.open(error.error?.message || 'Failed to update deal', 'Close', { duration: 3000 });
        this.saving = false;
      }
    });
  }

  onUpdateStage(): void {
    if (this.stageForm.invalid) {
      return;
    }

    const newStage = this.stageForm.value.stage;
    if (newStage === this.deal.currentStage) {
      this.snackBar.open('Stage is already set to this value', 'Close', { duration: 2000 });
      return;
    }

    this.saving = true;
    const request: UpdateDealStageRequest = { stage: newStage };

    this.dealService.updateDealStage(this.deal.id, request).subscribe({
      next: (updatedDeal) => {
        this.snackBar.open('Deal stage updated successfully', 'Close', { duration: 3000 });
        this.dialogRef.close(updatedDeal);
      },
      error: (error) => {
        this.snackBar.open(error.error?.message || 'Failed to update stage', 'Close', { duration: 3000 });
        this.saving = false;
      }
    });
  }

  onUpdateValue(): void {
    if (!this.isAdmin) {
      this.snackBar.open('Only admins can update deal value', 'Close', { duration: 3000 });
      return;
    }

    if (this.valueForm.invalid) {
      this.valueForm.markAllAsTouched();
      return;
    }

    this.saving = true;
    const request: UpdateDealValueRequest = {
      dealValue: Number(this.valueForm.value.dealValue)
    };

    this.dealService.updateDealValue(this.deal.id, request).subscribe({
      next: (updatedDeal) => {
        this.snackBar.open('Deal value updated successfully', 'Close', { duration: 3000 });
        this.dialogRef.close(updatedDeal);
      },
      error: (error) => {
        this.snackBar.open(error.error?.message || 'Failed to update deal value', 'Close', { duration: 3000 });
        this.saving = false;
      }
    });
  }

  onAddNote(): void {
    if (this.noteForm.invalid) {
      this.noteForm.markAllAsTouched();
      return;
    }

    this.saving = true;
    const request: AddNoteRequest = {
      note: this.noteForm.value.note
    };

    this.dealService.addNote(this.deal.id, request).subscribe({
      next: (updatedDeal) => {
        this.snackBar.open('Note added successfully', 'Close', { duration: 3000 });
        this.noteForm.reset();
        this.deal.notes = updatedDeal.notes;
        this.saving = false;
      },
      error: (error) => {
        this.snackBar.open(error.error?.message || 'Failed to add note', 'Close', { duration: 3000 });
        this.saving = false;
      }
    });
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

  getStageClass(stage: string): string {
    const classMap: { [key: string]: string } = {
      'Prospect': 'stage-prospect',
      'UnderEvaluation': 'stage-under-evaluation',
      'TermSheetSubmitted': 'stage-termsheet-submitted',
      'Closed': 'stage-closed',
      'Lost': 'stage-lost'
    };
    return classMap[stage] || '';
  }

  getStageIcon(stage: DealStage | string): string {
    const iconMap: { [key: string]: string } = {
      'Prospect': 'visibility',
      'UnderEvaluation': 'rate_review', 
      'TermSheetSubmitted': 'description',
      'Closed': 'check_circle',
      'Lost': 'cancel'
    };
    return iconMap[String(stage)] || 'radio_button_unchecked';
  }

  formatCurrency(value?: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(value || 0);
  }

  formatDate(date: string | Date): string {
    if (!date) return '';
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    return dateObj.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
