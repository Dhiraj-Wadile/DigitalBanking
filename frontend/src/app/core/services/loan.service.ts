import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Loan, ApiResponse } from '../models/common.model';

@Injectable({ providedIn: 'root' })
export class LoanService {
  private apiUrl = `${environment.apiUrl}/loans`;

  constructor(private http: HttpClient) {}

  getMyLoans(page: number = 0, size: number = 10): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(this.apiUrl, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

  applyForLoan(request: any): Observable<ApiResponse<Loan>> {
    return this.http.post<ApiResponse<Loan>>(`${this.apiUrl}/apply`, request);
  }

  getLoanByNumber(loanNumber: string): Observable<ApiResponse<Loan>> {
    return this.http.get<ApiResponse<Loan>>(`${this.apiUrl}/${loanNumber}`);
  }
}
