import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="mb-8">
        <h1 class="page-title">Profile</h1>
        <p class="page-subtitle">Your account information</p>
      </div>

      <div class="card p-8">
        <div class="flex flex-col sm:flex-row items-center gap-6 mb-8 pb-8 border-b border-gray-100">
          <div class="w-20 h-20 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-2xl flex items-center justify-center text-white text-3xl font-bold shadow-lg">
            {{ userInitial }}
          </div>
          <div class="text-center sm:text-left">
            <h2 class="text-xl font-bold text-gray-900">{{ user?.fullName }}</h2>
            <p class="text-gray-500 mt-0.5">{{ user?.email }}</p>
            <span class="badge-blue mt-2 inline-block">{{ user?.role }}</span>
          </div>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">
          <div>
            <label class="label">Username</label>
            <div class="input-field bg-gray-50 cursor-not-allowed">{{ user?.username }}</div>
          </div>
          <div>
            <label class="label">Email</label>
            <div class="input-field bg-gray-50 cursor-not-allowed">{{ user?.email }}</div>
          </div>
          <div>
            <label class="label">Phone</label>
            <div class="input-field bg-gray-50 cursor-not-allowed">{{ user?.phone || 'Not provided' }}</div>
          </div>
          <div>
            <label class="label">Role</label>
            <div class="input-field bg-gray-50 cursor-not-allowed">{{ user?.role }}</div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  user: any = null;
  userInitial = '';

  ngOnInit() {
    this.user = this.authService.getCurrentUser();
    if (this.user) {
      this.userInitial = (this.user.fullName || this.user.username).charAt(0).toUpperCase();
    }
  }
}
