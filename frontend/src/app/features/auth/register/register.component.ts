import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
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
            Start your financial journey today.
          </h1>
          <p class="text-lg text-blue-100/80 leading-relaxed">
            Create your account in minutes and get access to a full suite of banking services.
          </p>
        </div>
      </div>

      <!-- Right Panel - Form -->
      <div class="flex-1 flex items-center justify-center px-6 py-12 bg-white">
        <div class="w-full max-w-md">
          <div class="lg:hidden flex items-center gap-2 mb-8">
            <div class="w-10 h-10 bg-gradient-to-br from-blue-600 to-indigo-700 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
            </div>
            <span class="text-xl font-bold text-gray-900">DigitalBank</span>
          </div>

          <div class="mb-8">
            <h2 class="text-2xl font-bold text-gray-900">Create your account</h2>
            <p class="text-gray-500 mt-1">Fill in the details to get started</p>
          </div>

          <form (ngSubmit)="onRegister()" class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="label">First Name</label>
                <input type="text" [(ngModel)]="data.firstName" name="firstName" class="input-field" placeholder="John" required>
              </div>
              <div>
                <label class="label">Last Name</label>
                <input type="text" [(ngModel)]="data.lastName" name="lastName" class="input-field" placeholder="Doe" required>
              </div>
            </div>
            <div>
              <label class="label">Email</label>
              <input type="email" [(ngModel)]="data.email" name="email" class="input-field" placeholder="john@example.com" required>
            </div>
            <div>
              <label class="label">Username</label>
              <input type="text" [(ngModel)]="data.username" name="username" class="input-field" placeholder="johndoe" required>
            </div>
            <div>
              <label class="label">Phone</label>
              <input type="tel" [(ngModel)]="data.phone" name="phone" class="input-field" placeholder="+91 98765 43210">
            </div>
            <div>
              <label class="label">Password</label>
              <input type="password" [(ngModel)]="data.password" name="password" class="input-field" placeholder="Create a strong password" required>
            </div>
            <div *ngIf="error" class="flex items-center gap-2 bg-red-50 text-red-600 p-3 rounded-xl text-sm">
              <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              {{ error }}
            </div>
            <button type="submit" [disabled]="loading" class="btn-primary w-full flex items-center justify-center gap-2">
              {{ loading ? 'Creating account...' : 'Create Account' }}
            </button>
          </form>

          <div class="mt-6 text-center">
            <p class="text-sm text-gray-500">
              Already have an account?
              <a routerLink="/login" class="font-semibold text-blue-600 hover:text-blue-700">Sign in</a>
            </p>
          </div>
        </div>
      </div>
    </div>
  `
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  data = { firstName: '', lastName: '', email: '', username: '', phone: '', password: '' };
  loading = false;
  error = '';

  onRegister() {
    this.loading = true;
    this.error = '';
    this.authService.register(this.data).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Registration failed.';
      }
    });
  }
}
