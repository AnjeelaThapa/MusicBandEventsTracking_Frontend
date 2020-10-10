import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CheckActiveUserService {

  private userType = new BehaviorSubject('none');
  activeUserObservable = this.userType.asObservable();

  private token = new BehaviorSubject(undefined);
  tokenObservable = this.token.asObservable();

  private userId = new BehaviorSubject(undefined);
  userIdObservable = this.userId.asObservable();

  constructor() {
  }

  changeCurrentUserType(userType: string) {
    this.userType.next(userType);
  }

  changeCurrentUserToken(token: string) {
    this.token.next(token);
  }

  changeCurrentUserId(userId: string) {
    this.userId.next(userId);
  }

  checkActiveUser() {
    if (localStorage.getItem('user_type')) {
      this.changeCurrentUserType(localStorage.getItem('user_type'));
    }
    if (localStorage.getItem('user_id')) {
      this.changeCurrentUserId(localStorage.getItem('user_id'));
    }
    if (localStorage.getItem('token')) {
      this.changeCurrentUserToken(localStorage.getItem('token'));
    }
  }
}
