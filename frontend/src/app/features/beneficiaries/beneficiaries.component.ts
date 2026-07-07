import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BeneficiaryService } from '../../core/services/beneficiary.service';
import { Beneficiary } from '../../core/models/common.model';

@Component({
  selector: 'app-beneficiaries',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="flex justify-between items-center mb-8">
        <div>
          <h1 class="page-title">Beneficiaries</h1>
          <p class="page-subtitle">Manage your saved recipients</p>
        </div>
        <button (click)="showAddForm = true" class="btn-primary flex items-center gap-2">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
          </svg>
          Add Beneficiary
        </button>
      </div>

      <div *ngIf="error" class="flex items-center gap-2 bg-red-50 text-red-600 p-4 rounded-xl mb-6">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        {{ error }}
      </div>

      <!-- Add Form -->
      <div *ngIf="showAddForm" class="card p-6 mb-8">
        <h2 class="text-lg font-semibold text-gray-900 mb-5">Add New Beneficiary</h2>
        <form (ngSubmit)="onAdd()" class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Name</label>
            <input type="text" [(ngModel)]="newB.name" name="name" class="input-field" placeholder="Beneficiary name" required>
          </div>
          <div>
            <label class="label">Account Number</label>
            <input type="text" [(ngModel)]="newB.accountNumber" name="accountNumber" class="input-field" placeholder="Account number" required>
          </div>
          <div>
            <label class="label">IFSC Code</label>
            <input type="text" [(ngModel)]="newB.ifscCode" name="ifscCode" class="input-field" placeholder="IFSC code" required>
          </div>
          <div>
            <label class="label">Bank Name</label>
            <input type="text" [(ngModel)]="newB.bankName" name="bankName" class="input-field" placeholder="Bank name">
          </div>
          <div class="md:col-span-2 flex gap-3">
            <button type="submit" class="btn-primary">Save Beneficiary</button>
            <button type="button" (click)="showAddForm = false" class="btn-secondary">Cancel</button>
          </div>
        </form>
      </div>

      <!-- Beneficiaries Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div *ngFor="let b of beneficiaries" class="card p-6 group">
          <div class="flex items-start justify-between mb-4">
            <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center text-white font-bold text-lg shadow-sm">
              {{ b.name.charAt(0) }}
            </div>
            <span [class]="b.verified ? 'badge-green' : 'badge-yellow'">{{ b.verified ? 'Verified' : 'Pending' }}</span>
          </div>
          <h3 class="font-semibold text-gray-900 mb-1">{{ b.name }}</h3>
          <p class="text-sm text-gray-500 font-mono">{{ b.accountNumber }}</p>
          <div class="mt-3 pt-3 border-t border-gray-100 flex justify-between items-center">
            <div>
              <p class="text-xs text-gray-400">IFSC</p>
              <p class="text-sm font-medium text-gray-700 font-mono">{{ b.ifscCode }}</p>
            </div>
            <button (click)="onDelete(b.id)" class="btn-danger text-sm">Delete</button>
          </div>
        </div>
      </div>

      <div *ngIf="beneficiaries.length === 0 && !error" class="text-center py-16">
        <div class="w-16 h-16 bg-gray-100 rounded-2xl flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z"/>
          </svg>
        </div>
        <p class="text-gray-500 font-medium">No beneficiaries yet</p>
        <p class="text-sm text-gray-400 mt-1">Add a beneficiary to start sending money</p>
      </div>
    </div>
  `
})
export class BeneficiariesComponent implements OnInit {
  private beneficiaryService = inject(BeneficiaryService);
  beneficiaries: Beneficiary[] = [];
  showAddForm = false;
  error = '';
  newB = { name: '', accountNumber: '', ifscCode: '', bankName: '' };

  ngOnInit() {
    this.beneficiaryService.getBeneficiaries().subscribe({ next: (r) => this.beneficiaries = r.data || [], error: () => {} });
  }

  onAdd() {
    this.beneficiaryService.addBeneficiary(this.newB).subscribe({
      next: () => { this.showAddForm = false; this.newB = { name: '', accountNumber: '', ifscCode: '', bankName: '' }; this.ngOnInit(); },
      error: (err) => this.error = err.error?.message || 'Failed.'
    });
  }

  onDelete(id: number) {
    if (confirm('Delete this beneficiary?')) {
      this.beneficiaryService.deleteBeneficiary(id).subscribe({ next: () => this.ngOnInit() });
    }
  }
}
