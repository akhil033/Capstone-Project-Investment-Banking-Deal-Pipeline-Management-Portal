import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { DealService } from '../../../core/services/deal.service';
import { AuthService } from '../../../core/services/auth.service';
import { Deal, DealStage, DealType, AddNoteRequest, UpdateDealStageRequest, UpdateDealValueRequest, UpdateDealRequest } from '../../../core/models/deal.model';
import { DeleteConfirmationDialogComponent } from '../../../shared/components/delete-confirmation-dialog/delete-confirmation-dialog.component';

@Component({
  selector: 'app-deal-detail',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDividerModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatDialogModule
  ],
  templateUrl: './deal-detail.component.html',
  styleUrls: ['./deal-detail.component.css']
})
export class DealDetailComponent implements OnInit {
  deal: Deal | null = null;
  loading = false;
  isAdmin = false;
  isEditMode = false;
  editForm!: FormGroup;
  dealTypes = Object.values(DealType);
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dealService: DealService,
    private authService: AuthService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
    this.isAdmin = this.authService.isAdmin();
  }
  
  ngOnInit(): void {
    this.initForms();
    this.loadDeal();
  }
  
  private initForms(): void {
    this.editForm = this.fb.group({
      clientName: ['', [Validators.required, Validators.minLength(2)]],
      dealType: ['', Validators.required],
      sector: ['', Validators.required],
      summary: ['', [Validators.required, Validators.minLength(10)]],
      assignedTo: ['']
    });
  }
  
  private loadDeal(): void {
    const dealId = this.route.snapshot.paramMap.get('id');
    if (!dealId) {
      this.router.navigate(['/deals']);
      return;
    }
    
    this.loading = true;
    this.dealService.getDealById(dealId).subscribe({
      next: (deal) => {
        this.deal = deal;
        this.editForm.patchValue({
          clientName: deal.clientName,
          dealType: deal.dealType,
          sector: deal.sector,
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
  
  onEdit(): void {
    this.isEditMode = true;
  }
  
  onSaveEdit(): void {
    if (this.editForm.invalid || !this.deal) {
      this.editForm.markAllAsTouched();
      this.snackBar.open('Please fill all required fields correctly', 'Close', { duration: 3000 });
      return;
    }
    
    const updateRequest: UpdateDealRequest = {
      clientName: this.editForm.value.clientName,
      dealType: this.editForm.value.dealType,
      sector: this.editForm.value.sector,
      summary: this.editForm.value.summary,
      assignedTo: this.editForm.value.assignedTo
    };
    
    this.dealService.updateDeal(this.deal.id, updateRequest).subscribe({
      next: (updatedDeal) => {
        this.deal = updatedDeal;
        this.isEditMode = false;
        this.snackBar.open('Deal updated successfully', 'Close', { duration: 3000 });
      },
      error: (error) => {
        this.snackBar.open(error.error?.message || 'Failed to update deal', 'Close', { duration: 3000 });
      }
    });
  }
  
  onCancelEdit(): void {
    if (this.deal) {
      this.editForm.patchValue({
        clientName: this.deal.clientName,
        dealType: this.deal.dealType,
        sector: this.deal.sector,
        summary: this.deal.summary,
        assignedTo: this.deal.assignedTo
      });
    }
    this.isEditMode = false;
  }
  
  onDelete(): void {
    if (!this.deal || !this.isAdmin) {
      return;
    }
    
    const dialogRef = this.dialog.open(DeleteConfirmationDialogComponent, {
      width: '500px',
      maxWidth: '95vw',
      data: {
        title: 'Delete Deal',
        message: 'Are you sure you want to delete this deal?',
        itemName: this.deal.clientName
      },
      disableClose: false,
      autoFocus: true
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed && this.deal) {
        this.dealService.deleteDeal(this.deal.id).subscribe({
          next: () => {
            this.snackBar.open('Deal deleted successfully', 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'bottom'
            });
            this.router.navigate(['/deals']);
          },
          error: (error) => {
            this.snackBar.open(error.error?.message || 'Failed to delete deal', 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'bottom'
            });
          }
        });
      }
    });
  }
  
  onBack(): void {
    this.router.navigate(['/deals']);
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

  getStageIcon(stage: string): string {
    const iconMap: { [key: string]: string } = {
      'Prospect': 'visibility',
      'UnderEvaluation': 'rate_review',
      'TermSheetSubmitted': 'description',
      'Closed': 'check_circle',
      'Lost': 'cancel'
    };
    return iconMap[stage] || 'label';
  }

  formatStageName(stage: string): string {
    const nameMap: { [key: string]: string } = {
      'Prospect': 'Prospect',
      'UnderEvaluation': 'Under Evaluation',
      'TermSheetSubmitted': 'Term Sheet Submitted',
      'Closed': 'Closed',
      'Lost': 'Lost'
    };
    return nameMap[stage] || stage;
  }
  
  formatDate(date: string): string {
    return new Date(date).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
