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
    new InputParam('email', '', '', 'Nhập Email', 'email', 'Nhập địa chỉ email của bạn'),
    new InputParam('password', '', '', 'Nhập Password', 'password', 'Mật khẩu của bạn'),
    new InputParam('confirmPassword', '', '', 'Nhập lại Password', 'password', 'Xác nhận mật khẩu'),
    new InputParam('firstName', '', '', 'Họ', 'text', 'Nhập họ của bạn'),
    new InputParam('lastName', '', '', 'Tên', 'text', 'Nhập tên của bạn'),
  ];

  inputLoginParams: InputParam[] = [
    new InputParam('email', '', '', 'Email', 'email', 'Nhập email đăng nhập của bạn'),
    new InputParam('password', '', '', 'Mật khẩu', 'password', 'Nhập mật khẩu của bạn'),
  ];
  constructor(
    protected storeService: StoreService
    , protected toast: ToastrService
    , private http: HttpClient
    , private authService: AuthenticationService
  ) {

  }

  ngOnInit(): void {
    this.storeService.setTitle('ĐĂNG NHẬP - ĐĂNG KÝ');
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
        param.message = `${param.title} bắt buộc`;
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
            this.showToast('Đăng ký thành công', 'success');
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
        param.message = `${param.title} bắt buộc`;
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
            this.showToast('Đăng nhập thành công', 'success');
            this.authService.redirectHome();
          } else {
            this.showToast(res.message, 'error');
          }
        });
    }
  }
}
