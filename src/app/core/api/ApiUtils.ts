import {HttpHeaders} from '@angular/common/http';
import {ApiConfig} from './ApiConfig';

export class ApiUtils {
  public static getRequest(api: string) {
    const fullApi = `${ApiConfig.URL}/${api}`;
    const token = localStorage.getItem('token');

    return {
      url: fullApi,
      header: new HttpHeaders({
        Authorization : 'Bearer ' + token,
        'Content-Type': 'application/json'
      })
    };
  }
}
