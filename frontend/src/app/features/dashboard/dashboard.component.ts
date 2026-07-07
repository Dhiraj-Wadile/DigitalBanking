import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DashboardService } from '../../core/services/dashboard.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="page-title">Welcome back, {{ userName }}</h1>
        <p class="page-subtitle">Here's your financial overview</p>
      </div>

      <div *ngIf="error" class="flex items-center gap-2 bg-red-50 text-red-600 p-4 rounded-xl mb-6">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        {{ error }}
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 mb-8">
        <div class="stat-card">
          <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-blue-500 to-blue-600 rounded-t-2xl"></div>
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-500">Total Balance</p>
              <p class="text-2xl font-bold text-gray-900 mt-1">{{ (dashboardData?.totalBalance || 0) | number:'1.2-2' }}</p>
            </div>
            <div class="w-12 h-12 bg-blue-50 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
            </div>
          </div>
        </div>
        <div class="stat-card">
          <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-emerald-500 to-emerald-600 rounded-t-2xl"></div>
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-500">Accounts</p>
              <p class="text-2xl font-bold text-gray-900 mt-1">{{ dashboardData?.totalAccounts || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-emerald-50 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"/>
              </svg>
            </div>
          </div>
        </div>
        <div class="stat-card">
          <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-violet-500 to-violet-600 rounded-t-2xl"></div>
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-500">Transactions</p>
              <p class="text-2xl font-bold text-gray-900 mt-1">{{ dashboardData?.totalTransactions || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-violet-50 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-violet-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4"/>
              </svg>
            </div>
          </div>
        </div>
        <div class="stat-card">
          <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-amber-500 to-amber-600 rounded-t-2xl"></div>
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-500">Pending</p>
              <p class="text-2xl font-bold text-gray-900 mt-1">{{ dashboardData?.pendingPayments?.length || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-amber-50 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="mb-8">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h2>
        <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
          <a routerLink="/payments" class="card p-5 text-center hover:shadow-md group cursor-pointer">
            <div class="w-12 h-12 bg-blue-50 rounded-xl flex items-center justify-center mx-auto mb-3 group-hover:bg-blue-100 transition">
              <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"/>
              </svg>
            </div>
            <p class="text-sm font-semibold text-gray-700">Send Money</p>
          </a>
          <a routerLink="/payments" class="card p-5 text-center hover:shadow-md group cursor-pointer">
            <div class="w-12 h-12 bg-emerald-50 rounded-xl flex items-center justify-center mx-auto mb-3 group-hover:bg-emerald-100 transition">
              <svg class="w-6 h-6 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 14l6-6m-5.5.5h.01m4.99 5h.01M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16l3.5-2 3.5 2 3.5-2 3.5 2z"/>
              </svg>
            </div>
            <p class="text-sm font-semibold text-gray-700">Pay Bills</p>
          </a>
          <a routerLink="/transactions" class="card p-5 text-center hover:shadow-md group cursor-pointer">
            <div class="w-12 h-12 bg-violet-50 rounded-xl flex items-center justify-center mx-auto mb-3 group-hover:bg-violet-100 transition">
              <svg class="w-6 h-6 text-violet-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
            </div>
            <p class="text-sm font-semibold text-gray-700">History</p>
          </a>
          <a routerLink="/beneficiaries" class="card p-5 text-center hover:shadow-md group cursor-pointer">
            <div class="w-12 h-12 bg-amber-50 rounded-xl flex items-center justify-center mx-auto mb-3 group-hover:bg-amber-100 transition">
              <svg class="w-6 h-6 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z"/>
              </svg>
            </div>
            <p class="text-sm font-semibold text-gray-700">Beneficiaries</p>
          </a>
        </div>
      </div>

      <!-- Accounts & Transactions -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="card p-6">
          <div class="flex items-center justify-between mb-5">
            <h2 class="text-lg font-semibold text-gray-900">My Accounts</h2>
            <a routerLink="/accounts" class="text-sm font-medium text-blue-600 hover:text-blue-700">View All</a>
          </div>
          <div class="space-y-3">
            <div *ngFor="let a of dashboardData?.accounts" class="p-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition">
              <div class="flex justify-between items-center">
                <div>
                  <p class="font-semibold text-gray-900">{{ a.accountNumber }}</p>
                  <p class="text-sm text-gray-500 mt-0.5">{{ a.accountType }}</p>
                </div>
                <div class="text-right">
                  <p class="font-bold text-gray-900">{{ (a.balance || 0) | number:'1.2-2' }}</p>
                  <span [class]="a.status === 'ACTIVE' ? 'badge-green' : 'badge-red'">{{ a.status }}</span>
                </div>
              </div>
            </div>
          </div>
          <p *ngIf="!dashboardData?.accounts?.length" class="text-gray-400 text-center py-8">No accounts found</p>
        </div>

        <div class="card p-6">
          <div class="flex items-center justify-between mb-5">
            <h2 class="text-lg font-semibold text-gray-900">Recent Transactions</h2>
            <a routerLink="/transactions" class="text-sm font-medium text-blue-600 hover:text-blue-700">View All</a>
          </div>
          <div class="space-y-3">
            <div *ngFor="let t of dashboardData?.recentTransactions" class="flex justify-between items-center p-4 hover:bg-gray-50 rounded-xl transition">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl flex items-center justify-center"
                     [ngClass]="t.amount > 0 ? 'bg-emerald-50' : 'bg-red-50'">
                  <svg *ngIf="t.amount > 0" class="w-5 h-5 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4"/>
                  </svg>
                  <svg *ngIf="t.amount <= 0" class="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
                  </svg>
                </div>
                <div>
                  <p class="font-medium text-gray-900">{{ t.description }}</p>
                  <p class="text-xs text-gray-400 mt-0.5">{{ t.date | date:'short' }}</p>
                </div>
              </div>
              <p class="font-bold" [class]="t.amount > 0 ? 'text-emerald-600' : 'text-red-600'">
                {{ t.amount > 0 ? '+' : '' }}{{ t.amount | number:'1.2-2' }}
              </p>
            </div>
          </div>
          <p *ngIf="!dashboardData?.recentTransactions?.length" class="text-gray-400 text-center py-8">No recent transactions</p>
        </div>
      </div>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  private authService = inject(AuthService);

  dashboardData: any = null;
  userName = '';
  error = '';

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.userName = user.fullName || user.username;
    }
    this.dashboardService.getDashboard().subscribe({
      next: (res) => this.dashboardData = res.data,
      error: () => this.error = 'Could not load dashboard data.'
    });
  }
}
