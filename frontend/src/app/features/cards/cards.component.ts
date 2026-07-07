import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardService } from '../../core/services/card.service';
import { Card } from '../../core/models/common.model';

@Component({
  selector: 'app-cards',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="mb-8">
        <h1 class="page-title">My Cards</h1>
        <p class="page-subtitle">Manage your debit and credit cards</p>
      </div>

      <div *ngIf="error" class="flex items-center gap-2 bg-red-50 text-red-600 p-4 rounded-xl mb-6">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        {{ error }}
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div *ngFor="let card of cards"
             class="rounded-2xl p-6 text-white shadow-lg relative overflow-hidden group"
             [ngClass]="card.cardType === 'CREDIT' ? 'bg-gradient-to-br from-violet-600 via-purple-700 to-indigo-900' : card.cardType === 'DEBIT' ? 'bg-gradient-to-br from-blue-600 via-blue-700 to-slate-900' : 'bg-gradient-to-br from-emerald-600 via-teal-700 to-slate-900'">
          <!-- Background decoration -->
          <div class="absolute top-0 right-0 w-32 h-32 bg-white/5 rounded-full -mr-16 -mt-16"></div>
          <div class="absolute bottom-0 left-0 w-24 h-24 bg-white/5 rounded-full -ml-12 -mb-12"></div>

          <div class="relative z-10">
            <div class="flex justify-between items-start mb-8">
              <div>
                <p class="text-sm opacity-70 font-medium">{{ card.cardType }} Card</p>
                <p class="text-lg font-bold mt-0.5">{{ card.cardNetwork }}</p>
              </div>
              <div class="w-10 h-10 bg-white/10 backdrop-blur rounded-xl flex items-center justify-center">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"/>
                </svg>
              </div>
            </div>

            <p class="text-xl tracking-[0.2em] font-mono mb-6">{{ card.cardNumber }}</p>

            <div class="flex justify-between items-end">
              <div>
                <p class="text-xs opacity-60 mb-1">CARD HOLDER</p>
                <p class="font-semibold text-sm">{{ card.cardHolderName }}</p>
              </div>
              <div class="text-right">
                <p class="text-xs opacity-60 mb-1">EXPIRES</p>
                <p class="font-semibold text-sm">{{ card.expiryMonth }}/{{ card.expiryYear }}</p>
              </div>
            </div>

            <div class="mt-5 pt-4 border-t border-white/10 flex justify-between items-center">
              <span class="text-xs px-2.5 py-1 rounded-full"
                    [ngClass]="card.status === 'ACTIVE' ? 'bg-emerald-500/20 text-emerald-200' : 'bg-red-500/20 text-red-200'">
                {{ card.status }}
              </span>
              <div class="flex gap-2">
                <button *ngIf="card.status === 'ACTIVE'" (click)="blockCard(card.id)"
                        class="text-xs bg-white/10 backdrop-blur px-3 py-1.5 rounded-full hover:bg-white/20 transition font-medium">
                  Block
                </button>
                <button *ngIf="card.status === 'BLOCKED'" (click)="unblockCard(card.id)"
                        class="text-xs bg-white/10 backdrop-blur px-3 py-1.5 rounded-full hover:bg-white/20 transition font-medium">
                  Unblock
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div *ngIf="cards.length === 0 && !error" class="text-center py-16">
        <div class="w-16 h-16 bg-gray-100 rounded-2xl flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"/>
          </svg>
        </div>
        <p class="text-gray-500 font-medium">No cards found</p>
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
    if (confirm('Are you sure you want to block this card?')) {
      this.cardService.blockCard(id).subscribe({ next: () => this.ngOnInit(), error: (e) => this.error = e.error?.message || 'Failed.' });
    }
  }

  unblockCard(id: number) {
    this.cardService.unblockCard(id).subscribe({ next: () => this.ngOnInit(), error: (e) => this.error = e.error?.message || 'Failed.' });
  }
}
