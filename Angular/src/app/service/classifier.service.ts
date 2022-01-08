import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { Response } from '../model/response.model';

/**
 * Service for response model
 */
@Injectable()
export class ClassifierService {

   constructor(private apiService: ApiService) { }

   /**
    * Calls post request using selected dataset and classifier
    * @param dataset 
    * @param classifier 
    * @returns 
    */
   classify(dataset: string, classifier: string): Observable<Response> {
    return this.apiService.post('inputData', { "dataset": dataset, "classifier": classifier, "output" : "" });
    }
}