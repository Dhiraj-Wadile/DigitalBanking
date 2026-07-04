import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountService } from '../../core/services/account.service';
import { Account } from '../../core/models/common.model';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 py-8">
        <h1 class="text-2xl font-bold text-gray-800 mb-6">My Accounts</h1>
        <div *ngIf="error" class="bg-red-50 text-red-600 p-4 rounded-lg mb-6">{{ error }}</div>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div *ngFor="let a of accounts" class="glass-card p-6">
            <div class="flex justify-between items-start mb-4">
              <div>
                <h3 class="font-semibold text-gray-800">{{ a.accountType }}</h3>
                <p class="text-sm text-gray-500">{{ a.accountNumber }}</p>
              </div>
              <span class="text-xs px-2 py-1 rounded-full" [class]="a.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">{{ a.status }}</span>
            </div>
            <div class="space-y-2">
              <div class="flex justify-between"><span class="text-gray-500">Balance</span><span class="font-bold">{{ a.balance | number:'1.2-2' }}</span></div>
              <div class="flex justify-between"><span class="text-gray-500">Available</span><span class="font-semibold">{{ a.availableBalance | number:'1.2-2' }}</span></div>
              <div class="flex justify-between"><span class="text-gray-500">IFSC</span><span class="font-medium">{{ a.ifscCode }}</span></div>
            </div>
          </div>
        </div>
        <div *ngIf="accounts.length === 0 && !error" class="text-center py-12 text-gray-500">No accounts found</div>
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
