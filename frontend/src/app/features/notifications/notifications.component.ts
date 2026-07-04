import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="max-w-3xl mx-auto px-4 py-8">
        <h1 class="text-2xl font-bold text-gray-800 mb-6">Notifications</h1>
        <div class="glass-card p-12 text-center">
          <p class="text-4xl mb-4">&#128276;</p>
          <p class="text-gray-500">No notifications yet</p>
        </div>
      </div>
  `
})
export class NotificationsComponent {}
