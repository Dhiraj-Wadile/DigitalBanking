import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LoanService } from '../../core/services/loan.service';
import { Loan } from '../../core/models/common.model';

@Component({
  selector: 'app-loans',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="mb-8">
        <h1 class="page-title">Loans</h1>
        <p class="page-subtitle">Apply for loans and manage existing ones</p>
      </div>

      <div *ngIf="error" class="flex items-center gap-2 bg-red-50 text-red-600 p-4 rounded-xl mb-6">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        {{ error }}
      </div>

      <!-- Apply Form -->
      <div class="card p-6 mb-8">
        <div class="flex items-center gap-3 mb-5">
          <div class="w-10 h-10 bg-emerald-50 rounded-xl flex items-center justify-center">
            <svg class="w-5 h-5 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
            </svg>
          </div>
          <h2 class="text-lg font-semibold text-gray-900">Apply for Loan</h2>
        </div>
        <form (ngSubmit)="onApply()" class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Loan Type</label>
            <select [(ngModel)]="loanData.loanType" name="loanType" class="select-field">
              <option value="PERSONAL">Personal Loan</option>
              <option value="HOME">Home Loan</option>
              <option value="CAR">Car Loan</option>
              <option value="EDUCATION">Education Loan</option>
              <option value="BUSINESS">Business Loan</option>
            </select>
          </div>
          <div>
            <label class="label">Account Number</label>
            <input type="text" [(ngModel)]="loanData.accountNumber" name="accountNumber" class="input-field" placeholder="Your account number">
          </div>
          <div>
            <label class="label">Amount</label>
            <input type="number" [(ngModel)]="loanData.requestedAmount" name="amount" class="input-field" placeholder="0.00" step="0.01">
          </div>
          <div>
            <label class="label">Tenure (months)</label>
            <input type="number" [(ngModel)]="loanData.tenureMonths" name="tenure" class="input-field" placeholder="12">
          </div>
          <div class="md:col-span-2">
            <button type="submit" [disabled]="applying" class="btn-primary">
              {{ applying ? 'Applying...' : 'Apply Now' }}
            </button>
          </div>
        </form>
      </div>

      <!-- My Loans -->
      <div class="card p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-5">My Loans</h2>
        <div class="space-y-4">
          <div *ngFor="let loan of loans" class="p-5 bg-gray-50 rounded-xl">
            <div class="flex justify-between items-start mb-4">
              <div>
                <h3 class="font-semibold text-gray-900">{{ loan.loanType }} Loan</h3>
                <p class="text-sm text-gray-500 font-mono mt-0.5">{{ loan.loanAccountNumber }}</p>
              </div>
              <span [class]="loan.status === 'ACTIVE' ? 'badge-green' : 'badge-yellow'">{{ loan.status }}</span>
            </div>
            <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div class="p-3 bg-white rounded-lg">
                <p class="text-xs text-gray-400 mb-1">Sanctioned</p>
                <p class="font-bold text-gray-900">{{ loan.sanctionedAmount | number:'1.0-0' }}</p>
              </div>
              <div class="p-3 bg-white rounded-lg">
                <p class="text-xs text-gray-400 mb-1">Outstanding</p>
                <p class="font-bold text-gray-900">{{ loan.outstandingAmount | number:'1.0-0' }}</p>
              </div>
              <div class="p-3 bg-white rounded-lg">
                <p class="text-xs text-gray-400 mb-1">EMI</p>
                <p class="font-bold text-gray-900">{{ loan.emiAmount | number:'1.0-0' }}</p>
              </div>
              <div class="p-3 bg-white rounded-lg">
                <p class="text-xs text-gray-400 mb-1">Tenure</p>
                <p class="font-bold text-gray-900">{{ loan.tenureMonths }} months</p>
              </div>
            </div>
          </div>
        </div>
        <p *ngIf="loans.length === 0 && !error" class="text-gray-400 text-center py-8">No active loans</p>
      </div>
    </div>
  `
})
export class LoansComponent implements OnInit {
  private loanService = inject(LoanService);
  loans: Loan[] = [];
  applying = false;
  error = '';
  loanData = { loanType: 'PERSONAL', accountNumber: '', requestedAmount: 0, tenureMonths: 12 };

  ngOnInit() {
    this.loanService.getMyLoans().subscribe({ next: (r) => this.loans = r.data?.content || [], error: () => {} });
  }

  onApply() {
    this.applying = true;
    this.loanService.applyForLoan(this.loanData).subscribe({
      next: () => { this.applying = false; this.ngOnInit(); },
      error: (err) => { this.applying = false; this.error = err.error?.message || 'Failed.'; }
    });
  }
}
