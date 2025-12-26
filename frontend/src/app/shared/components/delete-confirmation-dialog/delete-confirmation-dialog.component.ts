import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

export interface DeleteDialogData {
  title: string;
  message: string;
  itemName: string;
}

@Component({
  selector: 'app-delete-confirmation-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule
  ],
  template: `
    <div class="delete-dialog">
      <div class="dialog-header">
        <div class="header-icon">
          <mat-icon>warning</mat-icon>
        </div>
        <h2 class="dialog-title">{{ data.title }}</h2>
        <button mat-icon-button (click)="onCancel()" class="close-button">
          <mat-icon>close</mat-icon>
        </button>
      </div>

      <div class="dialog-content">
        <p class="warning-message">{{ data.message }}</p>
        <div class="item-name">{{ data.itemName }}</div>
        <p class="warning-note">This action cannot be undone.</p>
      </div>

      <div class="dialog-actions">
        <button mat-button (click)="onCancel()" class="cancel-button">
          Cancel
        </button>
        <button mat-raised-button (click)="onConfirm()" class="delete-button">
          <mat-icon>delete</mat-icon>
          <span>Delete</span>
        </button>
      </div>
    </div>
  `,
  styles: [`
    .delete-dialog {
      display: flex;
      flex-direction: column;
      max-width: 500px;
      background: white;
      border-radius: 16px;
      overflow: hidden;
    }

    .dialog-header {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 24px 24px 16px;
      background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
      border-bottom: 1px solid #93c5fd;
      position: relative;
    }

    .header-icon {
      width: 48px;
      height: 48px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
      border-radius: 12px;
      box-shadow: 0 4px 8px rgba(239, 68, 68, 0.2);
    }

    .header-icon mat-icon {
      color: white;
      font-size: 28px;
      width: 28px;
      height: 28px;
    }

    .dialog-title {
      margin: 0;
      font-size: 24px;
      font-weight: 700;
      color: #1e3a8a;
      line-height: 1.2;
      flex: 1;
    }

    .close-button {
      width: 36px;
      height: 36px;
      background: rgba(255, 255, 255, 0.8);
      border: 1px solid #93c5fd;
      border-radius: 8px;
      position: absolute;
      top: 16px;
      right: 16px;
    }

    .close-button:hover {
      background: white;
      border-color: #60a5fa;
    }

    .close-button mat-icon {
      color: #1e40af;
    }

    .dialog-content {
      padding: 24px;
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .warning-message {
      margin: 0;
      font-size: 16px;
      color: #475569;
      line-height: 1.5;
    }

    .item-name {
      padding: 12px 16px;
      background: #f8fafc;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      font-size: 15px;
      font-weight: 600;
      color: #0f172a;
    }

    .warning-note {
      margin: 0;
      font-size: 14px;
      color: #dc2626;
      font-weight: 500;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .warning-note::before {
      content: '⚠️';
      font-size: 16px;
    }

    .dialog-actions {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      padding: 16px 24px 24px;
      border-top: 1px solid #e2e8f0;
    }

    .cancel-button {
      padding: 10px 24px !important;
      border-radius: 8px !important;
      font-weight: 600 !important;
      color: #475569 !important;
      background-color: #f1f5f9 !important;
      border: 1px solid #cbd5e1 !important;
      transition: all 0.15s ease !important;
    }

    .cancel-button:hover {
      background-color: #e2e8f0 !important;
      border-color: #94a3b8 !important;
      transform: translateY(-1px);
    }

    .delete-button {
      background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%) !important;
      color: white !important;
      padding: 10px 24px !important;
      border-radius: 8px !important;
      font-weight: 600 !important;
      box-shadow: 0 4px 8px rgba(220, 38, 38, 0.2) !important;
      transition: all 0.15s ease !important;
      display: flex !important;
      align-items: center !important;
      gap: 8px !important;
    }

    .delete-button:hover {
      transform: translateY(-1px);
      box-shadow: 0 6px 12px rgba(220, 38, 38, 0.3) !important;
    }

    .delete-button mat-icon {
      font-size: 18px;
      width: 18px;
      height: 18px;
    }
  `]
})
export class DeleteConfirmationDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DeleteDialogData,
    private dialogRef: MatDialogRef<DeleteConfirmationDialogComponent>
  ) {}

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
