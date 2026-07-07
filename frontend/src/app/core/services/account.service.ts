import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Account, ApiResponse } from '../models/common.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private apiUrl = `${environment.apiUrl}/accounts`;

  constructor(private http: HttpClient) {}

  getMyAccounts(): Observable<ApiResponse<Account[]>> {
    return this.http.get<ApiResponse<Account[]>>(this.apiUrl);
  }

}
