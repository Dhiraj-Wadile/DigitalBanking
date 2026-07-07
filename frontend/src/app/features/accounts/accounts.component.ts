import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountService } from '../../core/services/account.service';
import { Account } from '../../core/models/common.model';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="mb-8">
        <h1 class="page-title">My Accounts</h1>
        <p class="page-subtitle">View and manage your bank accounts</p>
      </div>

      <div *ngIf="error" class="flex items-center gap-2 bg-red-50 text-red-600 p-4 rounded-xl mb-6">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        {{ error }}
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div *ngFor="let a of accounts" class="card p-6 group">
          <div class="flex justify-between items-start mb-5">
            <div>
              <h3 class="font-semibold text-gray-900">{{ a.accountType }}</h3>
              <p class="text-sm text-gray-500 font-mono mt-0.5">{{ a.accountNumber }}</p>
            </div>
            <span [class]="a.status === 'ACTIVE' ? 'badge-green' : 'badge-red'">{{ a.status }}</span>
          </div>
          <div class="space-y-3">
            <div class="flex justify-between items-center py-2 border-b border-gray-50">
              <span class="text-sm text-gray-500">Balance</span>
              <span class="font-bold text-gray-900">{{ a.balance | number:'1.2-2' }}</span>
            </div>
            <div class="flex justify-between items-center py-2 border-b border-gray-50">
              <span class="text-sm text-gray-500">Available</span>
              <span class="font-semibold text-gray-900">{{ a.availableBalance | number:'1.2-2' }}</span>
            </div>
            <div class="flex justify-between items-center py-2">
              <span class="text-sm text-gray-500">IFSC</span>
              <span class="font-medium text-gray-700 font-mono text-sm">{{ a.ifscCode }}</span>
            </div>
          </div>
        </div>
      </div>

      <div *ngIf="accounts.length === 0 && !error" class="text-center py-16">
        <div class="w-16 h-16 bg-gray-100 rounded-2xl flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"/>
          </svg>
        </div>
        <p class="text-gray-500 font-medium">No accounts found</p>
      </div>
    </div>
  `
})
export class AccountsComponent implements OnInit {
  private accountService = inject(AccountService);
  accounts: Account[] = [];
  error = '';
  ngOnInit() {
    this.accountService.getMyAccounts().subscribe({
      next: (r) => this.accounts = r.data || [],
      error: () => this.error = 'Failed to load accounts.'
    });
  }
}
