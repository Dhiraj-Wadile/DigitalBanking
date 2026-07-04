import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Transaction, TransferRequest, PagedResponse, ApiResponse } from '../models/common.model';

@Injectable({ providedIn: 'root' })
export class TransactionService {
  private apiUrl = `${environment.apiUrl}/transactions`;

  constructor(private http: HttpClient) {}

  transfer(request: TransferRequest): Observable<ApiResponse<Transaction>> {
    return this.http.post<ApiResponse<Transaction>>(`${this.apiUrl}/transfer`, request);
  }

  deposit(accountNumber: string, amount: number, description?: string): Observable<ApiResponse<Transaction>> {
    const params = new HttpParams()
      .set('accountNumber', accountNumber)
      .set('amount', amount.toString())
      .set('description', description || '');
    return this.http.post<ApiResponse<Transaction>>(`${this.apiUrl}/deposit`, null, { params });
  }

  withdraw(accountNumber: string, amount: number, description?: string): Observable<ApiResponse<Transaction>> {
    const params = new HttpParams()
      .set('accountNumber', accountNumber)
      .set('amount', amount.toString())
      .set('description', description || '');
    return this.http.post<ApiResponse<Transaction>>(`${this.apiUrl}/withdraw`, null, { params });
  }

  getAccountTransactions(accountNumber: string, page: number = 0, size: number = 10): Observable<ApiResponse<PagedResponse<Transaction>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<PagedResponse<Transaction>>>(`${this.apiUrl}/account/${accountNumber}`, { params });
  }

}
