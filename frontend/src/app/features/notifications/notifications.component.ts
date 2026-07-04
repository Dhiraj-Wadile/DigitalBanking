import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-notifications',
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
        <h1 class="text-2xl font-bold text-gray-800 mb-6">Notifications</h1>
        <div class="glass-card p-12 text-center">
          <p class="text-4xl mb-4">&#128276;</p>
          <p class="text-gray-500">No notifications yet</p>
        </div>
      </div>
    </div>
  `
})
export class NotificationsComponent {}
