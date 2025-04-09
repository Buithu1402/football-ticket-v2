import {Component, OnInit} from '@angular/core';
import {StoreService} from '../../../share/service/store.service';
import {ToastrService} from 'ngx-toastr';
import {FormsModule} from '@angular/forms';
import {InputParam} from '../../../share/model/InputParam';
import {NgClass} from '@angular/common';
import {HttpClient} from '@angular/common/http';
import {ResponseData} from '../../../share/model/response-data.model';
import {AuthenticationService} from '../../../share/service/authentication.service';

@Component({
  selector: 'app-login-signup',
  imports: [
    FormsModule,
    NgClass
  ],
  templateUrl: './login-signup.component.html',
  standalone: true,
  styleUrls: ['./login-signup.component.scss']
})
export class LoginSignupComponent implements OnInit {
  inputRegisterParams: InputParam[] = [
    new InputParam('email', '', '', 'Your Email'),
    new InputParam('password', '', '', 'Password', 'password'),
    new InputParam('confirmPassword', '', '', 'Confirm Password', 'password'),
    new InputParam('firstName', '', '', 'First Name'),
    new InputParam('lastName', '', '', 'Last Name'),
  ];

  inputLoginParams: InputParam[] = [
    new InputParam('email', '', '', 'Your Email'),
    new InputParam('password', '', '', 'Password', 'password'),
  ];

  constructor(
    protected storeService: StoreService
    , protected toast: ToastrService
    , private http: HttpClient
    , private authService: AuthenticationService
  ) {

  }

  ngOnInit(): void {
    this.storeService.setTitle('Login/Signup');
    if (this.authService.isLoggedIn) {
      this.authService.redirectHome();
    }
  }

  showToast(mess: string, type: string) {
    switch (type) {
      case 'success':
        this.toast.success(mess);
        break;
      case 'error':
        this.toast.error(mess);
        break;
      case 'warning':
        this.toast.warning(mess);
        break;
      default:
        this.toast.info(mess);
        break;
    }
  }

  signUp() {
    let valid = true;
    this.inputRegisterParams.forEach(param => {
      if (!param.value.trim()) {
        param.message = `${param.title} is required`;
        valid = false;
      } else {
        param.message = '';
      }
    });
    if (valid) {
      const param = Object.fromEntries(this.inputRegisterParams.map(param => [param.name, param.value.trim()]));
      this.http.post<ResponseData<string>>('api/auth/register', param)
        .subscribe(res => {
          if (res.success) {
            this.showToast('Register success', 'success');
            this.inputRegisterParams.forEach(param => param.value = '');
          } else {
            this.showToast(res.message, 'error');
          }
        });
    }
  }

  login() {
    let valid = true;
    this.inputLoginParams.forEach(param => {
      if (!param.value) {
        param.message = `${param.title} is required`;
        valid = false;
      } else {
        param.message = '';
      }
    });
    if (valid) {
      const param = Object.fromEntries(this.inputLoginParams.map(param => [param.name, param.value]));
      this.authService.login(param)
        .subscribe(res => {
          if (res.success) {
            this.showToast('Login success', 'success');
            this.authService.redirectHome();
          } else {
            this.showToast(res.message, 'error');
          }
        });
    }
  }
}
