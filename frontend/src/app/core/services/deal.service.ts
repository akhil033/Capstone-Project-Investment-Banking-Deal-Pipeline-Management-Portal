import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Deal,
  CreateDealRequest,
  UpdateDealRequest,
  UpdateDealStageRequest,
  UpdateDealValueRequest,
  AddNoteRequest
} from '../models/deal.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DealService {
  private apiUrl = `${environment.apiUrl}/deals`;
  
  constructor(private http: HttpClient) {}
  
  createDeal(request: CreateDealRequest): Observable<Deal> {
    return this.http.post<Deal>(this.apiUrl, request);
  }
  
  getAllDeals(): Observable<Deal[]> {
    return this.http.get<Deal[]>(this.apiUrl);
  }
  
  getDealById(id: string): Observable<Deal> {
    return this.http.get<Deal>(`${this.apiUrl}/${id}`);
  }
  
  updateDeal(id: string, request: UpdateDealRequest): Observable<Deal> {
    return this.http.put<Deal>(`${this.apiUrl}/${id}`, request);
  }
  
  updateDealStage(id: string, request: UpdateDealStageRequest): Observable<Deal> {
    return this.http.patch<Deal>(`${this.apiUrl}/${id}/stage`, request);
  }
  
  updateDealValue(id: string, request: UpdateDealValueRequest): Observable<Deal> {
    return this.http.patch<Deal>(`${this.apiUrl}/${id}/value`, request);
  }
  
  addNote(id: string, request: AddNoteRequest): Observable<Deal> {
    return this.http.post<Deal>(`${this.apiUrl}/${id}/notes`, request);
  }
  
  deleteDeal(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
