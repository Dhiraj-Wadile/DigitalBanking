import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CardService } from '../../core/services/card.service';
import { Card } from '../../core/models/common.model';

@Component({
  selector: 'app-cards',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen bg-gray-50">
      <nav class="bg-white shadow-sm border-b">
        <div class="max-w-7xl mx-auto px-4 h-16 flex items-center">
          <a routerLink="/dashboard" class="text-blue-600 hover:underline font-medium">&larr; Back to Dashboard</a>
        </div>
      </nav>
      <div class="max-w-7xl mx-auto px-4 py-8">
        <h1 class="text-2xl font-bold text-gray-800 mb-6">My Cards</h1>
        <div *ngIf="error" class="bg-red-50 text-red-600 p-4 rounded-lg mb-6">{{ error }}</div>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div *ngFor="let card of cards" class="rounded-2xl p-6 text-white shadow-lg" [ngClass]="card.cardType === 'CREDIT' ? 'bg-gradient-to-br from-purple-600 to-purple-900' : card.cardType === 'DEBIT' ? 'bg-gradient-to-br from-blue-600 to-blue-900' : 'bg-gradient-to-br from-green-600 to-green-900'">
            <div class="flex justify-between items-start mb-6">
              <div>
                <p class="text-sm opacity-80">{{ card.cardType }}</p>
                <p class="text-lg font-bold">{{ card.cardNetwork }}</p>
              </div>
              <span class="text-2xl">{{ card.cardType === 'CREDIT' ? '&#127974;' : '&#127975;' }}</span>
            </div>
            <p class="text-xl tracking-widest font-mono mb-4">{{ card.cardNumber }}</p>
            <div class="flex justify-between">
              <div>
                <p class="text-xs opacity-80">CARD HOLDER</p>
                <p class="font-semibold">{{ card.cardHolderName }}</p>
              </div>
              <div class="text-right">
                <p class="text-xs opacity-80">EXPIRES</p>
                <p class="font-semibold">{{ card.expiryMonth }}/{{ card.expiryYear }}</p>
              </div>
            </div>
            <div class="mt-4 pt-3 border-t border-white/20 flex justify-between items-center">
              <span class="text-xs px-2 py-1 rounded-full" [class]="card.status === 'ACTIVE' ? 'bg-green-500/20' : 'bg-red-500/20'">{{ card.status }}</span>
              <div class="flex gap-2">
                <button *ngIf="card.status === 'ACTIVE'" (click)="blockCard(card.id)" class="text-xs bg-white/20 px-3 py-1 rounded-full hover:bg-white/30">Block</button>
                <button *ngIf="card.status === 'BLOCKED'" (click)="unblockCard(card.id)" class="text-xs bg-white/20 px-3 py-1 rounded-full hover:bg-white/30">Unblock</button>
              </div>
            </div>
          </div>
        </div>
        <div *ngIf="cards.length === 0 && !error" class="text-center py-12 text-gray-500">No cards found</div>
      </div>
    </div>
  `
})
export class CardsComponent implements OnInit {
  private cardService = inject(CardService);
  cards: Card[] = [];
  error = '';

  ngOnInit() {
    this.cardService.getMyCards().subscribe({ next: (r) => this.cards = r.data || [], error: () => {} });
  }

  blockCard(id: number) {
    if (confirm('Block this card?')) {
      this.cardService.blockCard(id).subscribe({ next: () => this.ngOnInit(), error: (e) => this.error = e.error?.message || 'Failed.' });
    }
  }

  unblockCard(id: number) {
    this.cardService.unblockCard(id).subscribe({ next: () => this.ngOnInit(), error: (e) => this.error = e.error?.message || 'Failed.' });
  }
}
