import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TransactionService } from '../../core/services/transaction.service';
import { AccountService } from '../../core/services/account.service';
import { Transaction, Account } from '../../core/models/common.model';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="mb-8">
        <h1 class="page-title">Transactions</h1>
        <p class="page-subtitle">Send money and view your transaction history</p>
      </div>

      <!-- Transfer Form -->
      <div class="card p-6 mb-8">
        <div class="flex items-center gap-3 mb-5">
          <div class="w-10 h-10 bg-blue-50 rounded-xl flex items-center justify-center">
            <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"/>
            </svg>
          </div>
          <h2 class="text-lg font-semibold text-gray-900">Send Money</h2>
        </div>
        <form (ngSubmit)="onTransfer()" class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">From Account</label>
            <select [(ngModel)]="transferData.fromAccountNumber" name="fromAccount" class="select-field">
              <option value="">Select account</option>
              <option *ngFor="let acc of accounts" [value]="acc.accountNumber">{{ acc.accountNumber }} - {{ acc.balance | number:'1.2-2' }}</option>
            </select>
          </div>
          <div>
            <label class="label">To Account</label>
            <input type="text" [(ngModel)]="transferData.toAccountNumber" name="toAccount" class="input-field" placeholder="Account number">
          </div>
          <div>
            <label class="label">Amount</label>
            <input type="number" [(ngModel)]="transferData.amount" name="amount" class="input-field" placeholder="0.00" step="0.01">
          </div>
          <div>
            <label class="label">Description</label>
            <input type="text" [(ngModel)]="transferData.description" name="description" class="input-field" placeholder="What's this for?">
          </div>
          <div class="md:col-span-2">
            <button type="submit" [disabled]="transferring" class="btn-primary">
              <span *ngIf="transferring" class="inline-flex items-center gap-2">
                <svg class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path></svg>
                Processing...
              </span>
              <span *ngIf="!transferring">Send Money</span>
            </button>
          </div>
        </form>
      </div>

      <!-- Transaction History -->
      <div class="card p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-5">Transaction History</h2>
        <div class="space-y-3">
          <div *ngFor="let txn of transactions" class="flex justify-between items-center p-4 hover:bg-gray-50 rounded-xl transition">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-xl flex items-center justify-center"
                   [ngClass]="isCredit(txn.transactionType) ? 'bg-emerald-50' : 'bg-red-50'">
                <svg *ngIf="isCredit(txn.transactionType)" class="w-5 h-5 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4"/>
                </svg>
                <svg *ngIf="!isCredit(txn.transactionType)" class="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
                </svg>
              </div>
              <div>
                <p class="font-medium text-gray-900">{{ txn.description || txn.transactionType }}</p>
                <p class="text-xs text-gray-400 mt-0.5 font-mono">{{ txn.referenceNumber }}</p>
                <p class="text-xs text-gray-400 mt-0.5">{{ txn.transactionDate | date:'medium' }}</p>
              </div>
            </div>
            <div class="text-right">
              <p class="font-bold" [class]="isCredit(txn.transactionType) ? 'text-emerald-600' : 'text-red-600'">
                {{ isCredit(txn.transactionType) ? '+' : '-' }}{{ txn.amount | number:'1.2-2' }}
              </p>
              <span [class]="txn.status === 'COMPLETED' ? 'badge-green' : 'badge-yellow'">{{ txn.status }}</span>
            </div>
          </div>
        </div>
        <p *ngIf="transactions.length === 0" class="text-gray-400 text-center py-8">No transactions yet</p>
      </div>
    </div>
  `
})
export class TransactionsComponent implements OnInit {
  private transactionService = inject(TransactionService);
  private accountService = inject(AccountService);
  transactions: Transaction[] = [];
  accounts: Account[] = [];
  transferring = false;
  transferData = { fromAccountNumber: '', toAccountNumber: '', amount: 0, transferType: 'INTERNAL', description: '' };

  ngOnInit() {
    this.accountService.getMyAccounts().subscribe({
      next: (r) => {
        this.accounts = r.data || [];
        if (this.accounts.length > 0) this.loadTransactions(this.accounts[0].accountNumber);
      }
    });
  }

  loadTransactions(accountNumber: string) {
    this.transactionService.getAccountTransactions(accountNumber).subscribe({
      next: (r) => this.transactions = r.data?.content || []
    });
  }

  onTransfer() {
    this.transferring = true;
    this.transactionService.transfer(this.transferData).subscribe({
      next: () => {
        this.transferring = false;
        if (this.accounts.length > 0) this.loadTransactions(this.accounts[0].accountNumber);
        this.accountService.getMyAccounts().subscribe({ next: (r) => this.accounts = r.data || [] });
      },
      error: () => this.transferring = false
    });
  }

  isCredit(type: string): boolean {
    return ['DEPOSIT', 'TRANSFER_IN', 'INTEREST_CREDIT'].includes(type);
  }
}
