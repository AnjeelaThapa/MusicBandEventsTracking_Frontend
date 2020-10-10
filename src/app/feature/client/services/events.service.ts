import { Injectable } from '@angular/core';
import {BaseService} from '../../../core/BaseService';
import {Events} from '../../model/events';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiUtils} from '../../../core/api/ApiUtils';

@Injectable({
  providedIn: 'root'
})
export class EventsService extends BaseService<Events> {
  static API = 'events';

  constructor(readonly http: HttpClient) {
    super(http);
  }

  protected getApi(): string {
    return EventsService.API;
  }

  public filterEvents(searchObj): Observable<any> {
    const api = `${EventsService.API}/filter`;
    const req = ApiUtils.getRequest(api);
    return this.http.post(req.url, searchObj, {headers: req.header});
  }
}
