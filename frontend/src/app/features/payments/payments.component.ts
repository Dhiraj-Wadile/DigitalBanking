import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentService } from '../../core/services/payment.service';
import { AccountService } from '../../core/services/account.service';
import { Payment, Account } from '../../core/models/common.model';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="mb-8">
        <h1 class="page-title">Payments</h1>
        <p class="page-subtitle">Make payments and view history</p>
      </div>

      <div *ngIf="error" class="flex items-center gap-2 bg-red-50 text-red-600 p-4 rounded-xl mb-6">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        {{ error }}
      </div>

      <!-- Payment Form -->
      <div class="card p-6 mb-8">
        <div class="flex items-center gap-3 mb-5">
          <div class="w-10 h-10 bg-violet-50 rounded-xl flex items-center justify-center">
            <svg class="w-5 h-5 text-violet-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z"/>
            </svg>
          </div>
          <h2 class="text-lg font-semibold text-gray-900">Make a Payment</h2>
        </div>
        <form (ngSubmit)="onPayment()" class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Payment Type</label>
            <select [(ngModel)]="paymentData.paymentType" name="paymentType" class="select-field">
              <option value="P2P">Person to Person</option>
              <option value="P2M">Person to Merchant</option>
              <option value="BILL_PAYMENT">Bill Payment</option>
              <option value="RECHARGE">Recharge</option>
            </select>
          </div>
          <div>
            <label class="label">Payment Method</label>
            <select [(ngModel)]="paymentData.paymentMethod" name="paymentMethod" class="select-field">
              <option value="UPI">UPI</option>
              <option value="NEFT">NEFT</option>
              <option value="RTGS">RTGS</option>
              <option value="IMPS">IMPS</option>
            </select>
          </div>
          <div>
            <label class="label">From Account</label>
            <select [(ngModel)]="paymentData.accountNumber" name="accountNumber" class="select-field">
              <option value="">Select account</option>
              <option *ngFor="let acc of accounts" [value]="acc.accountNumber">{{ acc.accountNumber }} - {{ acc.balance | number:'1.2-2' }}</option>
            </select>
          </div>
          <div>
            <label class="label">Amount</label>
            <input type="number" [(ngModel)]="paymentData.amount" name="amount" class="input-field" placeholder="0.00" step="0.01">
          </div>
          <div>
            <label class="label">Beneficiary Name</label>
            <input type="text" [(ngModel)]="paymentData.beneficiaryName" name="beneficiaryName" class="input-field" placeholder="Recipient name">
          </div>
          <div>
            <label class="label">Beneficiary Account</label>
            <input type="text" [(ngModel)]="paymentData.beneficiaryAccount" name="beneficiaryAccount" class="input-field" placeholder="Account number">
          </div>
          <div>
            <label class="label">IFSC Code</label>
            <input type="text" [(ngModel)]="paymentData.beneficiaryIfsc" name="beneficiaryIfsc" class="input-field" placeholder="IFSC code">
          </div>
          <div>
            <label class="label">Description</label>
            <input type="text" [(ngModel)]="paymentData.description" name="description" class="input-field" placeholder="Payment note">
          </div>
          <div class="md:col-span-2">
            <button type="submit" [disabled]="processing" class="btn-primary">
              {{ processing ? 'Processing...' : 'Make Payment' }}
            </button>
          </div>
        </form>
      </div>

      <!-- Payment History -->
      <div class="card p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-5">Payment History</h2>
        <div class="space-y-3">
          <div *ngFor="let p of payments" class="flex justify-between items-center p-4 hover:bg-gray-50 rounded-xl transition">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-red-50 rounded-xl flex items-center justify-center">
                <svg class="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
                </svg>
              </div>
              <div>
                <p class="font-medium text-gray-900">{{ p.paymentType }} - {{ p.paymentMethod }}</p>
                <p class="text-xs text-gray-400 mt-0.5 font-mono">{{ p.paymentReference }}</p>
                <p class="text-xs text-gray-400 mt-0.5">{{ p.paymentDate | date:'medium' }}</p>
              </div>
            </div>
            <div class="text-right">
              <p class="font-bold text-red-600">-{{ p.amount | number:'1.2-2' }}</p>
              <span [class]="p.status === 'COMPLETED' ? 'badge-green' : 'badge-yellow'">{{ p.status }}</span>
            </div>
          </div>
        </div>
        <p *ngIf="payments.length === 0" class="text-gray-400 text-center py-8">No payments yet</p>
      </div>
    </div>
  `
})
export class PaymentsComponent implements OnInit {
  private paymentService = inject(PaymentService);
  private accountService = inject(AccountService);
  payments: Payment[] = [];
  accounts: Account[] = [];
  processing = false;
  error = '';
  paymentData = { paymentType: 'P2P', paymentMethod: 'UPI', accountNumber: '', amount: 0, beneficiaryName: '', beneficiaryAccount: '', beneficiaryIfsc: '', description: '' };

  ngOnInit() {
    this.accountService.getMyAccounts().subscribe({ next: (r) => this.accounts = r.data || [], error: () => {} });
    this.paymentService.getMyPayments().subscribe({ next: (r) => this.payments = r.data?.content || [], error: () => {} });
  }

  onPayment() {
    this.processing = true;
    this.error = '';
    this.paymentService.processPayment(this.paymentData).subscribe({
      next: () => { this.processing = false; this.ngOnInit(); },
      error: (err) => { this.processing = false; this.error = err.error?.message || 'Payment failed.'; }
    });
  }
}
