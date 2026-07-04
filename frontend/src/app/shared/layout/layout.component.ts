import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <div class="min-h-screen bg-gray-50 flex flex-col">
      <nav class="bg-gradient-to-r from-blue-600 to-blue-800 text-white shadow-lg sticky top-0 z-50">
        <div class="max-w-7xl mx-auto px-4 sm:px-6">
          <div class="h-16 flex items-center justify-between">
            <a routerLink="/dashboard" class="text-xl font-bold tracking-tight hover:opacity-90 transition">
              DigitalBank
            </a>

            <div class="hidden md:flex items-center gap-1">
              <a *ngFor="let item of navItems"
                 [routerLink]="item.route"
                 routerLinkActive="bg-white/20"
                 class="px-3 py-2 rounded-lg text-sm font-medium hover:bg-white/10 transition">
                {{ item.label }}
              </a>
            </div>

            <div class="flex items-center gap-3">
              <div class="flex items-center gap-2 cursor-pointer" routerLink="/profile">
                <div class="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center text-sm font-bold">
                  {{ userInitial }}
                </div>
                <span class="hidden sm:block text-sm font-medium">{{ userName }}</span>
              </div>
              <button (click)="logout()"
                      class="px-3 py-1.5 text-sm font-medium bg-white/10 hover:bg-white/20 rounded-lg transition">
                Logout
              </button>
            </div>
          </div>

          <div class="md:hidden flex gap-1 pb-2 overflow-x-auto">
            <a *ngFor="let item of navItems"
               [routerLink]="item.route"
               routerLinkActive="bg-white/20"
               class="px-3 py-1.5 rounded-lg text-xs font-medium hover:bg-white/10 whitespace-nowrap transition">
              {{ item.label }}
            </a>
          </div>
        </div>
      </nav>

      <main class="flex-1">
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

  navItems = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Accounts', route: '/accounts' },
    { label: 'Transactions', route: '/transactions' },
    { label: 'Payments', route: '/payments' },
    { label: 'Cards', route: '/cards' },
    { label: 'Loans', route: '/loans' },
    { label: 'Beneficiaries', route: '/beneficiaries' },
  ];

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.userName = user.fullName || user.username;
      this.userInitial = (user.fullName || user.username).charAt(0).toUpperCase();
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
