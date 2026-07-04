import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PaymentService } from '../../core/services/payment.service';
import { AccountService } from '../../core/services/account.service';
import { Payment, Account } from '../../core/models/common.model';

@Component({
  selector: 'app-payments',
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
        <h1 class="text-2xl font-bold text-gray-800 mb-6">Payments</h1>
        <div *ngIf="error" class="bg-red-50 text-red-600 p-4 rounded-lg mb-6">{{ error }}</div>
        <div class="glass-card p-6 mb-8">
          <h2 class="text-lg font-semibold text-gray-800 mb-4">Make a Payment</h2>
          <form (ngSubmit)="onPayment()" class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Payment Type</label>
              <select [(ngModel)]="paymentData.paymentType" name="paymentType" class="w-full px-4 py-3 rounded-lg border border-gray-300">
                <option value="P2P">Person to Person</option>
                <option value="P2M">Person to Merchant</option>
                <option value="BILL_PAYMENT">Bill Payment</option>
                <option value="RECHARGE">Recharge</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Payment Method</label>
              <select [(ngModel)]="paymentData.paymentMethod" name="paymentMethod" class="w-full px-4 py-3 rounded-lg border border-gray-300">
                <option value="UPI">UPI</option>
                <option value="NEFT">NEFT</option>
                <option value="RTGS">RTGS</option>
                <option value="IMPS">IMPS</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">From Account</label>
              <select [(ngModel)]="paymentData.accountNumber" name="accountNumber" class="w-full px-4 py-3 rounded-lg border border-gray-300">
                <option value="">Select account</option>
                <option *ngFor="let acc of accounts" [value]="acc.accountNumber">{{ acc.accountNumber }} - {{ acc.balance | number:'1.2-2' }}</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Amount</label>
              <input type="number" [(ngModel)]="paymentData.amount" name="amount" class="w-full px-4 py-3 rounded-lg border border-gray-300" placeholder="Amount">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Beneficiary Name</label>
              <input type="text" [(ngModel)]="paymentData.beneficiaryName" name="beneficiaryName" class="w-full px-4 py-3 rounded-lg border border-gray-300">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Beneficiary Account</label>
              <input type="text" [(ngModel)]="paymentData.beneficiaryAccount" name="beneficiaryAccount" class="w-full px-4 py-3 rounded-lg border border-gray-300">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">IFSC Code</label>
              <input type="text" [(ngModel)]="paymentData.beneficiaryIfsc" name="beneficiaryIfsc" class="w-full px-4 py-3 rounded-lg border border-gray-300">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
              <input type="text" [(ngModel)]="paymentData.description" name="description" class="w-full px-4 py-3 rounded-lg border border-gray-300">
            </div>
            <div class="md:col-span-2">
              <button type="submit" [disabled]="processing" class="bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 disabled:opacity-50">
                {{ processing ? 'Processing...' : 'Make Payment' }}
              </button>
            </div>
          </form>
        </div>
        <div class="glass-card p-6">
          <h2 class="text-lg font-semibold text-gray-800 mb-4">Payment History</h2>
          <div *ngFor="let p of payments" class="flex justify-between items-center p-4 bg-gray-50 rounded-lg mb-3 last:mb-0">
            <div>
              <p class="font-medium text-gray-800">{{ p.paymentType }} - {{ p.paymentMethod }}</p>
              <p class="text-sm text-gray-500">{{ p.paymentReference }}</p>
              <p class="text-xs text-gray-400">{{ p.paymentDate | date:'medium' }}</p>
            </div>
            <div class="text-right">
              <p class="font-bold text-red-600">-{{ p.amount | number:'1.2-2' }}</p>
              <span class="text-xs px-2 py-1 rounded-full" [class]="p.status === 'COMPLETED' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'">{{ p.status }}</span>
            </div>
          </div>
          <p *ngIf="payments.length === 0" class="text-gray-400 text-center py-4">No payments</p>
        </div>
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
