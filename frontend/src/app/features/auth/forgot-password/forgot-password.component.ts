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
    <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-900 to-blue-600 px-4">
      <div class="w-full max-w-md">
        <div class="glass-card p-8">
          <div class="text-center mb-8">
            <h1 class="text-2xl font-bold text-gray-800">Reset Password</h1>
            <p class="text-gray-500 mt-2">Enter your email to reset password</p>
          </div>
          <form (ngSubmit)="onSubmit()" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
              <input type="email" [(ngModel)]="email" name="email"
                     class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                     placeholder="Enter your email" required>
            </div>
            <div *ngIf="success" class="bg-green-50 text-green-600 p-3 rounded-lg text-sm">{{ success }}</div>
            <div *ngIf="error" class="bg-red-50 text-red-600 p-3 rounded-lg text-sm">{{ error }}</div>
            <button type="submit" [disabled]="loading"
                    class="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition disabled:opacity-50">
              {{ loading ? 'Sending...' : 'Send Reset Link' }}
            </button>
          </form>
          <div class="mt-6 text-center">
            <a routerLink="/login" class="text-blue-600 text-sm font-semibold">Back to Login</a>
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
