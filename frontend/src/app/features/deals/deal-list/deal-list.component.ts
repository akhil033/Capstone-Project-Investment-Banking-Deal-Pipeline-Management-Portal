import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DealService } from '../../../core/services/deal.service';
import { AuthService } from '../../../core/services/auth.service';
import { Deal, DealStage } from '../../../core/models/deal.model';
import { DealEditDialogComponent } from '../deal-edit-dialog/deal-edit-dialog.component';
import { DeleteConfirmationDialogComponent } from '../../../shared/components/delete-confirmation-dialog/delete-confirmation-dialog.component';

@Component({
  selector: 'app-deal-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatTooltipModule,
    MatSnackBarModule
  ],
  templateUrl: './deal-list.component.html',
  styleUrls: ['./deal-list.component.css']
})
export class DealListComponent implements OnInit {
  deals: Deal[] = [];
  displayedColumns: string[] = [];
  loading = false;
  isAdmin = false;
  
  constructor(
    private dealService: DealService,
    private authService: AuthService,
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {
    this.isAdmin = this.authService.isAdmin();
    // All users can see deal values
    this.displayedColumns = ['clientName', 'dealType', 'stage', 'dealValue', 'updatedAt', 'actions'];
  }
  
  ngOnInit(): void {
    this.loadDeals();
  }
  
  loadDeals(): void {
    this.loading = true;
    this.dealService.getAllDeals().subscribe({
      next: (deals) => {
        this.deals = deals;
        this.loading = false;
      },
      error: (error) => {
        console.error('Failed to load deals:', error);
        this.loading = false;
      }
    });
  }
  
  viewDeal(deal: Deal): void {
    this.router.navigate(['/deals', deal.id]);
  }
  
  editDeal(event: Event, deal: Deal): void {
    event?.stopPropagation(); // Prevent row click
    
    const dialogRef = this.dialog.open(DealEditDialogComponent, {
      width: '800px',
      maxWidth: '95vw',
      maxHeight: '90vh',
      data: deal,
      disableClose: false,
      autoFocus: true
    });
    
    dialogRef.afterClosed().subscribe(updatedDeal => {
      if (updatedDeal) {
        // Refresh the list to show updated data
        this.loadDeals();
        this.snackBar.open('Deal updated successfully', 'Close', { 
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'bottom'
        });
      }
    });
  }
  
  deleteDeal(event: Event, deal: Deal): void {
    event?.stopPropagation(); // Prevent row click
    
    const dialogRef = this.dialog.open(DeleteConfirmationDialogComponent, {
      width: '500px',
      maxWidth: '95vw',
      data: {
        title: 'Delete Deal',
        message: 'Are you sure you want to delete this deal?',
        itemName: deal.clientName
      },
      disableClose: false,
      autoFocus: true
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.dealService.deleteDeal(deal.id).subscribe({
          next: () => {
            this.loadDeals();
            this.snackBar.open('Deal deleted successfully', 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'bottom'
            });
          },
          error: (error) => {
            console.error('Failed to delete deal:', error);
            this.snackBar.open('Failed to delete deal', 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'bottom'
            });
          }
        });
      }
    });
  }
  
  createDeal(): void {
    this.router.navigate(['/deals/new']);
  }
  
  getStageClass(stage: string): string {
    const stageMap: { [key: string]: string } = {
      'Prospect': 'stage-prospect',
      'UnderEvaluation': 'stage-under-evaluation', 
      'TermSheetSubmitted': 'stage-termsheet-submitted',
      'Closed': 'stage-closed',
      'Lost': 'stage-lost'
    };
    return stageMap[stage] || '';
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
}
