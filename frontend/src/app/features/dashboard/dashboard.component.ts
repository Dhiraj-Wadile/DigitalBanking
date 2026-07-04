import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { DashboardService } from '../../core/services/dashboard.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen bg-gray-50">
      <nav class="bg-white shadow-sm border-b">
        <div class="max-w-7xl mx-auto px-4 h-16 flex justify-between items-center">
          <span class="text-xl font-bold text-blue-600">Digital Banking</span>
          <div class="flex items-center gap-4">
            <a routerLink="/notifications" class="text-gray-500 hover:text-gray-700">&#128276;</a>
            <div class="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
              <span class="text-white text-sm font-semibold">{{ userInitial }}</span>
            </div>
            <a routerLink="/profile" class="text-sm font-medium text-gray-700">{{ userName }}</a>
            <button (click)="logout()" class="text-gray-500 hover:text-red-600">&#8592;</button>
          </div>
        </div>
      </nav>

      <div class="max-w-7xl mx-auto px-4 py-8">
        <h1 class="text-2xl font-bold text-gray-800 mb-1">Welcome, {{ userName }}!</h1>
        <p class="text-gray-500 mb-6">Financial overview</p>

        <div *ngIf="error" class="bg-red-50 border border-red-200 text-red-600 p-4 rounded-lg mb-6">{{ error }}</div>

        <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          <div class="glass-card p-5">
            <p class="text-sm text-gray-500">Total Balance</p>
            <p class="text-2xl font-bold text-gray-800 mt-1">{{ dashboardData?.totalBalance | number:'1.2-2' }}</p>
          </div>
          <div class="glass-card p-5">
            <p class="text-sm text-gray-500">Accounts</p>
            <p class="text-2xl font-bold text-gray-800 mt-1">{{ dashboardData?.totalAccounts || 0 }}</p>
          </div>
          <div class="glass-card p-5">
            <p class="text-sm text-gray-500">This Month</p>
            <p class="text-2xl font-bold text-green-600 mt-1">{{ dashboardData?.totalDeposits | number:'1.2-2' }}</p>
          </div>
          <div class="glass-card p-5">
            <p class="text-sm text-gray-500">Pending</p>
            <p class="text-2xl font-bold text-orange-600 mt-1">{{ dashboardData?.pendingPayments?.length || 0 }}</p>
          </div>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          <a routerLink="/payments" class="glass-card p-4 text-center hover:shadow-md transition">
            <div class="text-2xl mb-1">&#128176;</div>
            <p class="text-sm font-medium text-gray-700">Send Money</p>
          </a>
          <a routerLink="/payments" class="glass-card p-4 text-center hover:shadow-md transition">
            <div class="text-2xl mb-1">&#128196;</div>
            <p class="text-sm font-medium text-gray-700">Pay Bills</p>
          </a>
          <a routerLink="/transactions" class="glass-card p-4 text-center hover:shadow-md transition">
            <div class="text-2xl mb-1">&#128336;</div>
            <p class="text-sm font-medium text-gray-700">History</p>
          </a>
          <a routerLink="/beneficiaries" class="glass-card p-4 text-center hover:shadow-md transition">
            <div class="text-2xl mb-1">&#128101;</div>
            <p class="text-sm font-medium text-gray-700">Beneficiaries</p>
          </a>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div class="glass-card p-6">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">My Accounts</h2>
            <div *ngFor="let a of dashboardData?.accounts" class="p-3 bg-gray-50 rounded-lg mb-3 last:mb-0">
              <div class="flex justify-between items-center">
                <div>
                  <p class="font-medium text-gray-800">{{ a.accountNumber }}</p>
                  <p class="text-sm text-gray-500">{{ a.accountType }}</p>
                </div>
                <div class="text-right">
                  <p class="font-semibold text-gray-800">{{ a.balance | number:'1.2-2' }}</p>
                  <span class="text-xs px-2 py-0.5 rounded-full" [class]="a.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">{{ a.status }}</span>
                </div>
              </div>
            </div>
            <p *ngIf="!dashboardData?.accounts?.length" class="text-gray-400 text-center py-4">No accounts</p>
          </div>

          <div class="glass-card p-6">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">Recent Transactions</h2>
            <div *ngFor="let t of dashboardData?.recentTransactions" class="flex justify-between items-center p-3 hover:bg-gray-50 rounded-lg mb-2 last:mb-0">
              <div>
                <p class="font-medium text-gray-800">{{ t.description }}</p>
                <p class="text-sm text-gray-500">{{ t.date | date:'short' }}</p>
              </div>
              <p class="font-semibold" [class]="t.amount > 0 ? 'text-green-600' : 'text-red-600'">
                {{ t.amount > 0 ? '+' : '' }}{{ t.amount | number:'1.2-2' }}
              </p>
            </div>
            <p *ngIf="!dashboardData?.recentTransactions?.length" class="text-gray-400 text-center py-4">No transactions</p>
          </div>
        </div>
      </div>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  private authService = inject(AuthService);
  private router = inject(Router);

  dashboardData: any = null;
  userName = '';
  userInitial = '';
  error = '';

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.userName = user.fullName || user.username;
      this.userInitial = (user.fullName || user.username).charAt(0).toUpperCase();
    }
    this.dashboardService.getDashboard().subscribe({
      next: (res) => this.dashboardData = res.data,
      error: () => this.error = 'Could not load dashboard data.'
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
