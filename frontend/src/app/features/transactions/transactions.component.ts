import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TransactionService } from '../../core/services/transaction.service';
import { AccountService } from '../../core/services/account.service';
import { Transaction, Account } from '../../core/models/common.model';

@Component({
  selector: 'app-transactions',
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
        <h1 class="text-2xl font-bold text-gray-800 mb-6">Transactions</h1>
        <div class="glass-card p-6 mb-8">
          <h2 class="text-lg font-semibold text-gray-800 mb-4">Send Money</h2>
          <form (ngSubmit)="onTransfer()" class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">From Account</label>
              <select [(ngModel)]="transferData.fromAccountNumber" name="fromAccount" class="w-full px-4 py-3 rounded-lg border border-gray-300">
                <option value="">Select account</option>
                <option *ngFor="let acc of accounts" [value]="acc.accountNumber">{{ acc.accountNumber }} - {{ acc.balance | number:'1.2-2' }}</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">To Account</label>
              <input type="text" [(ngModel)]="transferData.toAccountNumber" name="toAccount" class="w-full px-4 py-3 rounded-lg border border-gray-300" placeholder="Account number">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Amount</label>
              <input type="number" [(ngModel)]="transferData.amount" name="amount" class="w-full px-4 py-3 rounded-lg border border-gray-300" placeholder="Amount">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
              <input type="text" [(ngModel)]="transferData.description" name="description" class="w-full px-4 py-3 rounded-lg border border-gray-300" placeholder="Description">
            </div>
            <div class="md:col-span-2">
              <button type="submit" [disabled]="transferring" class="bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 disabled:opacity-50">
                {{ transferring ? 'Processing...' : 'Send Money' }}
              </button>
            </div>
          </form>
        </div>
        <div class="glass-card p-6">
          <h2 class="text-lg font-semibold text-gray-800 mb-4">History</h2>
          <div *ngFor="let txn of transactions" class="flex justify-between items-center p-4 bg-gray-50 rounded-lg mb-3 last:mb-0">
            <div>
              <p class="font-medium text-gray-800">{{ txn.description || txn.transactionType }}</p>
              <p class="text-sm text-gray-500">{{ txn.referenceNumber }}</p>
              <p class="text-xs text-gray-400">{{ txn.transactionDate | date:'medium' }}</p>
            </div>
            <div class="text-right">
              <p class="font-bold" [class]="isCredit(txn.transactionType) ? 'text-green-600' : 'text-red-600'">
                {{ isCredit(txn.transactionType) ? '+' : '-' }}{{ txn.amount | number:'1.2-2' }}
              </p>
              <span class="text-xs px-2 py-1 rounded-full" [class]="txn.status === 'COMPLETED' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'">{{ txn.status }}</span>
            </div>
          </div>
          <p *ngIf="transactions.length === 0" class="text-gray-400 text-center py-4">No transactions</p>
        </div>
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
