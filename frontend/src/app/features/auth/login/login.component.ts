import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="min-h-screen flex">
      <!-- Left Panel - Branding -->
      <div class="hidden lg:flex lg:flex-1 bg-gradient-to-br from-slate-900 via-blue-900 to-blue-700 relative overflow-hidden">
        <div class="absolute inset-0 opacity-10">
          <div class="absolute top-20 left-20 w-72 h-72 bg-blue-400 rounded-full blur-3xl"></div>
          <div class="absolute bottom-20 right-20 w-96 h-96 bg-indigo-400 rounded-full blur-3xl"></div>
        </div>
        <div class="relative z-10 flex flex-col justify-center px-16 max-w-xl">
          <div class="flex items-center gap-3 mb-8">
            <div class="w-12 h-12 bg-white/10 backdrop-blur rounded-2xl flex items-center justify-center">
              <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
            </div>
            <span class="text-2xl font-bold text-white">DigitalBank</span>
          </div>
          <h1 class="text-4xl font-bold text-white leading-tight mb-4">
            Your finances,<br>beautifully managed.
          </h1>
          <p class="text-lg text-blue-100/80 leading-relaxed">
            Experience modern banking with real-time insights, instant transfers, and complete control over your money.
          </p>
          <div class="mt-12 grid grid-cols-3 gap-6">
            <div class="text-center">
              <p class="text-3xl font-bold text-white">256-bit</p>
              <p class="text-sm text-blue-200/60 mt-1">Encryption</p>
            </div>
            <div class="text-center">
              <p class="text-3xl font-bold text-white">24/7</p>
              <p class="text-sm text-blue-200/60 mt-1">Support</p>
            </div>
            <div class="text-center">
              <p class="text-3xl font-bold text-white">99.9%</p>
              <p class="text-sm text-blue-200/60 mt-1">Uptime</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Panel - Form -->
      <div class="flex-1 flex items-center justify-center px-6 py-12 bg-white">
        <div class="w-full max-w-md">
          <!-- Mobile Logo -->
          <div class="lg:hidden flex items-center gap-2 mb-8">
            <div class="w-10 h-10 bg-gradient-to-br from-blue-600 to-indigo-700 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
            </div>
            <span class="text-xl font-bold text-gray-900">DigitalBank</span>
          </div>

          <div class="mb-8">
            <h2 class="text-2xl font-bold text-gray-900">Welcome back</h2>
            <p class="text-gray-500 mt-1">Sign in to continue to your account</p>
          </div>

          <form (ngSubmit)="onLogin()" class="space-y-5">
            <div>
              <label class="label">Username</label>
              <input type="text" [(ngModel)]="username" name="username"
                     class="input-field"
                     placeholder="Enter your username" required>
            </div>

            <div>
              <label class="label">Password</label>
              <input type="password" [(ngModel)]="password" name="password"
                     class="input-field"
                     placeholder="Enter your password" required>
            </div>

            <div class="flex items-center justify-between">
              <label class="flex items-center gap-2 cursor-pointer">
                <input type="checkbox" class="w-4 h-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500">
                <span class="text-sm text-gray-600">Remember me</span>
              </label>
              <a routerLink="/forgot-password" class="text-sm font-medium text-blue-600 hover:text-blue-700">Forgot password?</a>
            </div>

            <div *ngIf="error" class="flex items-center gap-2 bg-red-50 text-red-600 p-3 rounded-xl text-sm">
              <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              {{ error }}
            </div>

            <button type="submit" [disabled]="loading" class="btn-primary w-full flex items-center justify-center gap-2">
              <svg *ngIf="loading" class="animate-spin w-5 h-5" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ loading ? 'Signing in...' : 'Sign In' }}
            </button>
          </form>

          <div class="mt-8 text-center">
            <p class="text-sm text-gray-500">
              Don't have an account?
              <a routerLink="/register" class="font-semibold text-blue-600 hover:text-blue-700">Create account</a>
            </p>
          </div>

          <div class="mt-8 pt-6 border-t border-gray-100">
            <p class="text-xs text-gray-400 text-center">Demo: admin/admin123, customer/customer123</p>
          </div>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  username = '';
  password = '';
  loading = false;
  error = '';

  onLogin() {
    this.loading = true;
    this.error = '';
    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Login failed. Please try again.';
      }
    });
  }
}
