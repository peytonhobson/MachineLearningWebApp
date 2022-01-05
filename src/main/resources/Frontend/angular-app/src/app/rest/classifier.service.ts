import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { Response } from '../model/response.model';
import { HttpErrorResponse, HttpParams } from '@angular/common/http';

@Injectable()
export class ClassifierService {

   constructor(private apiService: ApiService) { }

   classify(dataset: string, classifier: string): Observable<Result> {
    return this.apiService.post('inputData', { "dataset": dataset, "classifier": classifier });
    }
}