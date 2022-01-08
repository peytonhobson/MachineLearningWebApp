import { Injectable } from '@angular/core';
import { Observable} from 'rxjs';
import { HttpClient, HttpHeaders} from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Response } from '../model/response.model';


const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
  })
};

/**
 * Class used for api service from client to server
 */
@Injectable()
export class ApiService {
  constructor(private http: HttpClient) { }

  /**
   * Method for making post request to back end 
   * and returning observable response.
   * @param path 
   * @param body 
   * @returns 
   */
  post(path: String, body: Object = {}): Observable<Response> {
    return this.http.post<Response>(`${environment.apiUrl}${path}`, JSON.stringify(body), httpOptions);
  }
}