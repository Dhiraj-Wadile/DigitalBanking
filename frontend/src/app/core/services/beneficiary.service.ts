import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Beneficiary, ApiResponse } from '../models/common.model';

@Injectable({ providedIn: 'root' })
export class BeneficiaryService {
  private apiUrl = `${environment.apiUrl}/beneficiaries`;

  constructor(private http: HttpClient) {}

  getBeneficiaries(): Observable<ApiResponse<Beneficiary[]>> {
    return this.http.get<ApiResponse<Beneficiary[]>>(this.apiUrl);
  }

  addBeneficiary(request: any): Observable<ApiResponse<Beneficiary>> {
    return this.http.post<ApiResponse<Beneficiary>>(this.apiUrl, request);
  }

  deleteBeneficiary(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }
}
