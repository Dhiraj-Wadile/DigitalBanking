import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen bg-gray-50">
      <nav class="bg-white shadow-sm border-b">
        <div class="max-w-7xl mx-auto px-4 h-16 flex items-center">
          <a routerLink="/dashboard" class="text-blue-600 hover:underline font-medium">&larr; Back to Dashboard</a>
        </div>
      </nav>
      <div class="max-w-3xl mx-auto px-4 py-8">
        <h1 class="text-2xl font-bold text-gray-800 mb-6">Profile</h1>
        <div class="glass-card p-8">
          <div class="flex items-center gap-6 mb-8">
            <div class="w-20 h-20 bg-blue-600 rounded-full flex items-center justify-center">
              <span class="text-white text-3xl font-bold">{{ userInitial }}</span>
            </div>
            <div>
              <h2 class="text-xl font-bold text-gray-800">{{ user?.fullName }}</h2>
              <p class="text-gray-500">{{ user?.email }}</p>
              <p class="text-sm text-gray-400 mt-1">{{ user?.role }}</p>
            </div>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Username</label>
              <input type="text" [value]="user?.username" disabled class="w-full px-4 py-3 rounded-lg border border-gray-300 bg-gray-50">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input type="email" [value]="user?.email" disabled class="w-full px-4 py-3 rounded-lg border border-gray-300 bg-gray-50">
            </div>
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
