import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <div class="min-h-screen bg-slate-50 flex">
      <!-- Sidebar -->
      <aside class="hidden lg:flex lg:flex-col w-64 bg-white border-r border-gray-100 fixed inset-y-0 left-0 z-40">
        <!-- Logo -->
        <div class="h-16 flex items-center gap-3 px-6 border-b border-gray-100">
          <div class="w-9 h-9 bg-gradient-to-br from-blue-600 to-indigo-700 rounded-xl flex items-center justify-center shadow-sm">
            <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <div>
            <span class="text-base font-bold text-gray-900 tracking-tight">DigitalBank</span>
          </div>
        </div>

        <!-- Navigation -->
        <nav class="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
          <a *ngFor="let item of navItems"
             [routerLink]="item.route"
             routerLinkActive="bg-blue-50 text-blue-700"
             class="flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium text-gray-600 hover:bg-gray-50 hover:text-gray-900 transition-all duration-150">
            <span [innerHTML]="item.icon" class="w-5 h-5 flex-shrink-0"></span>
            {{ item.label }}
          </a>
        </nav>

        <!-- User section -->
        <div class="p-4 border-t border-gray-100">
          <div class="flex items-center gap-3 p-3 rounded-xl hover:bg-gray-50 cursor-pointer transition" routerLink="/profile">
            <div class="w-9 h-9 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white text-sm font-bold shadow-sm">
              {{ userInitial }}
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-semibold text-gray-900 truncate">{{ userName }}</p>
              <p class="text-xs text-gray-500 truncate">{{ userRole }}</p>
            </div>
          </div>
          <button (click)="logout()"
                  class="mt-2 w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium text-gray-500 hover:bg-red-50 hover:text-red-600 transition-all duration-150">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
            </svg>
            Sign Out
          </button>
        </div>
      </aside>

      <!-- Mobile Header -->
      <div class="lg:hidden fixed top-0 inset-x-0 z-40 bg-white border-b border-gray-100">
        <div class="h-14 flex items-center justify-between px-4">
          <div class="flex items-center gap-2">
            <div class="w-8 h-8 bg-gradient-to-br from-blue-600 to-indigo-700 rounded-lg flex items-center justify-center">
              <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
            </div>
            <span class="font-bold text-gray-900">DigitalBank</span>
          </div>
          <button (click)="mobileMenuOpen = !mobileMenuOpen" class="p-2 rounded-lg hover:bg-gray-100">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path *ngIf="!mobileMenuOpen" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
              <path *ngIf="mobileMenuOpen" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>
        <!-- Mobile menu dropdown -->
        <div *ngIf="mobileMenuOpen" class="border-t border-gray-100 bg-white px-4 py-3 space-y-1">
          <a *ngFor="let item of navItems"
             [routerLink]="item.route"
             (click)="mobileMenuOpen = false"
             routerLinkActive="bg-blue-50 text-blue-700"
             class="flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium text-gray-600 hover:bg-gray-50">
            <span [innerHTML]="item.icon" class="w-5 h-5"></span>
            {{ item.label }}
          </a>
          <button (click)="logout()" class="w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium text-red-500 hover:bg-red-50">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
            </svg>
            Sign Out
          </button>
        </div>
      </div>

      <!-- Main Content -->
      <main class="flex-1 lg:ml-64 pt-14 lg:pt-0 min-h-screen">
        <router-outlet></router-outlet>
      </main>
    </div>
  `
})
export class LayoutComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  userName = '';
  userInitial = '';
  userRole = '';
  mobileMenuOpen = false;

  navItems = [
    { label: 'Dashboard', route: '/dashboard', icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/></svg>' },
    { label: 'Accounts', route: '/accounts', icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"/></svg>' },
    { label: 'Transactions', route: '/transactions', icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4"/></svg>' },
    { label: 'Payments', route: '/payments', icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z"/></svg>' },
    { label: 'Cards', route: '/cards', icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"/></svg>' },
    { label: 'Loans', route: '/loans', icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/></svg>' },
    { label: 'Beneficiaries', route: '/beneficiaries', icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/></svg>' },
  ];

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.userName = user.fullName || user.username;
      this.userInitial = (user.fullName || user.username).charAt(0).toUpperCase();
      this.userRole = user.role ? user.role.replace('ROLE_', '').toLowerCase().replace(/^\w/, (c: string) => c.toUpperCase()) : '';
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
