
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { HttpClient, HttpHeaders, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Response } from '../model/response.model';
import { catchError } from 'rxjs/operators';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
  })
};

@Injectable()
export class ApiService {
  constructor(private http: HttpClient) { }

  post(path: String, body: Object = {}): Observable<Response> {
    console.log("executing post metod : " + JSON.stringify(body));
    return this.http.post<Response>(`${environment.apiUrl}${path}`, JSON.stringify(body), httpOptions);
  }
}