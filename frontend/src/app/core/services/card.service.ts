import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Card, ApiResponse } from '../models/common.model';

@Injectable({ providedIn: 'root' })
export class CardService {
  private apiUrl = `${environment.apiUrl}/cards`;

  constructor(private http: HttpClient) {}

  getMyCards(): Observable<ApiResponse<Card[]>> {
    return this.http.get<ApiResponse<Card[]>>(this.apiUrl);
  }

  issueCard(request: any): Observable<ApiResponse<Card>> {
    return this.http.post<ApiResponse<Card>>(`${this.apiUrl}/issue`, request);
  }

  blockCard(cardId: number): Observable<ApiResponse<Card>> {
    return this.http.post<ApiResponse<Card>>(`${this.apiUrl}/${cardId}/block`, {});
  }

  unblockCard(cardId: number): Observable<ApiResponse<Card>> {
    return this.http.post<ApiResponse<Card>>(`${this.apiUrl}/${cardId}/unblock`, {});
  }
}
