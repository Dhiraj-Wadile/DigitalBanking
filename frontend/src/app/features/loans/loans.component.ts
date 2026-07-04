import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LoanService } from '../../core/services/loan.service';
import { Loan } from '../../core/models/common.model';

@Component({
  selector: 'app-loans',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="min-h-screen bg-gray-50">
      <nav class="bg-white shadow-sm border-b">
        <div class="max-w-7xl mx-auto px-4 h-16 flex items-center">
          <a routerLink="/dashboard" class="text-blue-600 hover:underline font-medium">&larr; Back to Dashboard</a>
        </div>
      </nav>
      <div class="max-w-7xl mx-auto px-4 py-8">
        <h1 class="text-2xl font-bold text-gray-800 mb-6">Loans</h1>
        <div *ngIf="error" class="bg-red-50 text-red-600 p-4 rounded-lg mb-6">{{ error }}</div>
        <div class="glass-card p-6 mb-8">
          <h2 class="text-lg font-semibold text-gray-800 mb-4">Apply for Loan</h2>
          <form (ngSubmit)="onApply()" class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <select [(ngModel)]="loanData.loanType" name="loanType" class="w-full px-4 py-3 rounded-lg border border-gray-300">
              <option value="PERSONAL">Personal</option>
              <option value="HOME">Home</option>
              <option value="CAR">Car</option>
              <option value="EDUCATION">Education</option>
              <option value="BUSINESS">Business</option>
            </select>
            <input type="text" [(ngModel)]="loanData.accountNumber" name="accountNumber" class="w-full px-4 py-3 rounded-lg border border-gray-300" placeholder="Account Number">
            <input type="number" [(ngModel)]="loanData.requestedAmount" name="amount" class="w-full px-4 py-3 rounded-lg border border-gray-300" placeholder="Amount">
            <input type="number" [(ngModel)]="loanData.tenureMonths" name="tenure" class="w-full px-4 py-3 rounded-lg border border-gray-300" placeholder="Tenure (months)">
            <div class="md:col-span-2">
              <button type="submit" [disabled]="applying" class="bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 disabled:opacity-50">
                {{ applying ? 'Applying...' : 'Apply Now' }}
              </button>
            </div>
          </form>
        </div>
        <div class="glass-card p-6">
          <h2 class="text-lg font-semibold text-gray-800 mb-4">My Loans</h2>
          <div *ngFor="let loan of loans" class="p-4 bg-gray-50 rounded-lg mb-3 last:mb-0">
            <div class="flex justify-between items-start">
              <div>
                <h3 class="font-semibold text-gray-800">{{ loan.loanType }} Loan</h3>
                <p class="text-sm text-gray-500">{{ loan.loanAccountNumber }}</p>
              </div>
              <span class="text-xs px-2 py-1 rounded-full" [class]="loan.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'">{{ loan.status }}</span>
            </div>
            <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mt-3">
              <div><p class="text-xs text-gray-500">Sanctioned</p><p class="font-semibold">{{ loan.sanctionedAmount | number:'1.0-0' }}</p></div>
              <div><p class="text-xs text-gray-500">Outstanding</p><p class="font-semibold">{{ loan.outstandingAmount | number:'1.0-0' }}</p></div>
              <div><p class="text-xs text-gray-500">EMI</p><p class="font-semibold">{{ loan.emiAmount | number:'1.0-0' }}</p></div>
              <div><p class="text-xs text-gray-500">Tenure</p><p class="font-semibold">{{ loan.tenureMonths }} months</p></div>
            </div>
          </div>
          <p *ngIf="loans.length === 0 && !error" class="text-gray-400 text-center py-4">No loans</p>
        </div>
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
