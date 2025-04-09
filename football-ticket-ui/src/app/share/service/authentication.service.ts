import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, throwError} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {catchError, tap} from 'rxjs/operators';
import {LoginDTO, LoginResponseDTO} from '../model/auth.model';
import {UserService} from './user.service';
import {API_URL, CONSTANT} from '../constant';
import {ResponseData} from '../model/response-data.model';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private currentUser = new BehaviorSubject<LoginResponseDTO>(new LoginResponseDTO());
  private refreshing = false;

  constructor(private http: HttpClient,
              private userService: UserService,
              private router: Router
  ) {
    if (localStorage.getItem(CONSTANT.authToken)) {
      const currentUser = JSON.parse(localStorage.getItem(CONSTANT.authToken) ?? '');
      this.currentUser.next(currentUser);
    }
  }

  get userInfo(): LoginResponseDTO {
    return this.currentUser.value;
  }

  public get isLoggedIn(): boolean {
    return this.currentUser.value.valid;
  }

  public get isAdmin(): boolean {
    return this.currentUser.value.isAdmin;
  }

  public logout() {
    this.removeTokenStorage();
    this.userService.setProfile();
    this.redirectLogin();
  }

  getCurrentUser(): Observable<LoginResponseDTO> {
    return this.currentUser.asObservable();
  }

  getToken(): string {
    return `Bearer ${this.currentUser.value.accessToken}`;
  }

  isRefreshing(): boolean {
    return this.refreshing;
  }

  doRefreshToken() {
    this.refreshing = true;
    const refreshToken = this.currentUser.value.refreshToken;
    return this.http.post<ResponseData<string[]>>(API_URL.auth.refreshToken, {rt: refreshToken}).pipe(
      tap((res) => {
        this.refreshing = false;
        if (res.success && res?.data) {
          const user = new LoginResponseDTO(res.data[0], refreshToken, false, true);
          this.setTokenStorage(user);
        } else {
          this.removeTokenStorage();
        }
      }),
      catchError((err) => {
        this.refreshing = false;
        this.logout();
        return throwError(() => new Error(err));
      }),
    );
  }

  removeTokenStorage(): void {
    this.currentUser.next(new LoginResponseDTO());
    localStorage.removeItem(CONSTANT.authToken);
  }

  setTokenStorage(user: LoginResponseDTO): void {
    if (user) {
      localStorage.setItem(CONSTANT.authToken, JSON.stringify(user));
      this.currentUser.next(user);
    } else {
      localStorage.removeItem(CONSTANT.authToken);
    }
  }

  loginV3(email: string, password: string, captchaCode = '') {
    return this.login({email, password, captchaCode});
  }

  login(param: any) {
    return this.http.post<ResponseData<LoginResponseDTO>>(API_URL.auth.login, param)
      .pipe(
        tap((res) => {
          if (res.success) {
            const tokenObject = res.data;
            this.setTokenStorage(tokenObject);
          } else {
            this.removeTokenStorage();
          }
        }),
      );
  }

  loginV2(payload: LoginDTO) {
    return this.login(payload);
  }

  redirectLogin() {
    this.router.navigate([CONSTANT.login])
      .then(_ => window.location.reload());
  }

  redirectHome() {
    const href = this.currentUser.value.isAdmin ? `${CONSTANT.adminPath}` : `/`;
    this.router.navigate([href]).then(_ => window.location.reload());
  }

}
