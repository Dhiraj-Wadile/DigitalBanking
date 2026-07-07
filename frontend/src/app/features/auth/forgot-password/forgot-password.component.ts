import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-slate-50 px-4">
      <div class="w-full max-w-md">
        <div class="text-center mb-8">
          <div class="w-14 h-14 bg-gradient-to-br from-blue-600 to-indigo-700 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg">
            <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z"/>
            </svg>
          </div>
          <h1 class="text-2xl font-bold text-gray-900">Reset Password</h1>
          <p class="text-gray-500 mt-2">Enter your email and we'll send you a reset link</p>
        </div>

        <div class="card p-8">
          <form (ngSubmit)="onSubmit()" class="space-y-5">
            <div>
              <label class="label">Email Address</label>
              <input type="email" [(ngModel)]="email" name="email"
                     class="input-field"
                     placeholder="Enter your email" required>
            </div>

            <div *ngIf="success" class="flex items-center gap-2 bg-emerald-50 text-emerald-600 p-3 rounded-xl text-sm">
              <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              {{ success }}
            </div>

            <div *ngIf="error" class="flex items-center gap-2 bg-red-50 text-red-600 p-3 rounded-xl text-sm">
              <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              {{ error }}
            </div>

            <button type="submit" [disabled]="loading" class="btn-primary w-full">
              {{ loading ? 'Sending...' : 'Send Reset Link' }}
            </button>
          </form>

          <div class="mt-6 text-center">
            <a routerLink="/login" class="text-sm font-medium text-blue-600 hover:text-blue-700 inline-flex items-center gap-1">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
              </svg>
              Back to Login
            </a>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ForgotPasswordComponent {
  private authService = inject(AuthService);
  email = '';
  loading = false;
  error = '';
  success = '';

  onSubmit() {
    this.loading = true;
    this.error = '';
    this.success = '';
    this.authService.forgotPassword(this.email).subscribe({
      next: () => { this.success = 'Password reset link sent to your email'; this.loading = false; },
      error: (err) => { this.error = err.error?.message || 'Failed to send reset link'; this.loading = false; }
    });
  }
}
