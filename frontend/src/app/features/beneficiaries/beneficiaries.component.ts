import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentService } from '../../core/services/payment.service';
import { Beneficiary } from '../../core/models/common.model';

@Component({
  selector: 'app-beneficiaries',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 py-8">
        <div class="flex justify-between items-center mb-6">
          <h1 class="text-2xl font-bold text-gray-800">Beneficiaries</h1>
          <button (click)="showAddForm = true" class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700">+ Add</button>
        </div>
        <div *ngIf="error" class="bg-red-50 text-red-600 p-4 rounded-lg mb-6">{{ error }}</div>
        <div *ngIf="showAddForm" class="glass-card p-6 mb-8">
          <h2 class="text-lg font-semibold text-gray-800 mb-4">Add Beneficiary</h2>
          <form (ngSubmit)="onAdd()" class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <input type="text" [(ngModel)]="newB.name" name="name" class="w-full px-4 py-3 rounded-lg border border-gray-300" placeholder="Name" required>
            <input type="text" [(ngModel)]="newB.accountNumber" name="accountNumber" class="w-full px-4 py-3 rounded-lg border border-gray-300" placeholder="Account Number" required>
            <input type="text" [(ngModel)]="newB.ifscCode" name="ifscCode" class="w-full px-4 py-3 rounded-lg border border-gray-300" placeholder="IFSC Code" required>
            <input type="text" [(ngModel)]="newB.bankName" name="bankName" class="w-full px-4 py-3 rounded-lg border border-gray-300" placeholder="Bank Name">
            <div class="md:col-span-2 flex gap-4">
              <button type="submit" class="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700">Save</button>
              <button type="button" (click)="showAddForm = false" class="bg-gray-300 text-gray-700 px-6 py-2 rounded-lg hover:bg-gray-400">Cancel</button>
            </div>
          </form>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div *ngFor="let b of beneficiaries" class="glass-card p-6">
            <h3 class="font-semibold text-gray-800">{{ b.name }}</h3>
            <p class="text-sm text-gray-500">{{ b.accountNumber }}</p>
            <p class="text-sm text-gray-500">IFSC: {{ b.ifscCode }}</p>
            <p class="text-sm text-gray-500">Bank: {{ b.bankName }}</p>
            <div class="mt-4 pt-3 border-t flex justify-between items-center">
              <button (click)="onDelete(b.id)" class="text-red-600 hover:text-red-800 text-sm">Delete</button>
              <span class="text-xs px-2 py-1 rounded-full" [class]="b.verified ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'">{{ b.verified ? 'Verified' : 'Pending' }}</span>
            </div>
          </div>
        </div>
        <div *ngIf="beneficiaries.length === 0 && !error" class="text-center py-12 text-gray-500">No beneficiaries yet</div>
      </div>
  `
})
export class BeneficiariesComponent implements OnInit {
  private paymentService = inject(PaymentService);
  beneficiaries: Beneficiary[] = [];
  showAddForm = false;
  error = '';
  newB = { name: '', accountNumber: '', ifscCode: '', bankName: '' };

  ngOnInit() {
    this.paymentService.getBeneficiaries().subscribe({ next: (r) => this.beneficiaries = r.data || [], error: () => {} });
  }

  onAdd() {
    this.paymentService.addBeneficiary(this.newB).subscribe({
      next: () => { this.showAddForm = false; this.newB = { name: '', accountNumber: '', ifscCode: '', bankName: '' }; this.ngOnInit(); },
      error: (err) => this.error = err.error?.message || 'Failed.'
    });
  }

  onDelete(id: number) {
    if (confirm('Delete this beneficiary?')) {
      this.paymentService.deleteBeneficiary(id).subscribe({ next: () => this.ngOnInit() });
    }
  }
}
