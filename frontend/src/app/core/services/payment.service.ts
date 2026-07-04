import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Payment, Beneficiary, PagedResponse, ApiResponse } from '../models/common.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  processPayment(request: any): Observable<ApiResponse<Payment>> {
    return this.http.post<ApiResponse<Payment>>(`${this.apiUrl}/payments`, request);
  }

  getMyPayments(page: number = 0, size: number = 10): Observable<ApiResponse<PagedResponse<Payment>>> {
    return this.http.get<ApiResponse<PagedResponse<Payment>>>(`${this.apiUrl}/payments`, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

  getBeneficiaries(): Observable<ApiResponse<Beneficiary[]>> {
    return this.http.get<ApiResponse<Beneficiary[]>>(`${this.apiUrl}/beneficiaries`);
  }

  addBeneficiary(request: any): Observable<ApiResponse<Beneficiary>> {
    return this.http.post<ApiResponse<Beneficiary>>(`${this.apiUrl}/beneficiaries`, request);
  }

  deleteBeneficiary(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/beneficiaries/${id}`);
  }
}
