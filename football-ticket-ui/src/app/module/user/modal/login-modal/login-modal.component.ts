import {Component} from '@angular/core';
import {CONSTANT, LIB} from '../../../../share/constant';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {ToastrService} from 'ngx-toastr';
import {HttpClient} from '@angular/common/http';
import {AuthenticationService} from '../../../../share/service/authentication.service';
import {InputParam} from '../../../../share/model/InputParam';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-login-modal',
  imports: [
    RouterLink,
    RouterLinkActive,
    FormsModule
  ],
  templateUrl: './login-modal.component.html',
  standalone: true,
  styleUrls: ['./login-modal.component.scss',]
})
export class LoginModalComponent {
  param: ParamLoginModal = new ParamLoginModal();

  constructor(
    protected bsRef: BsModalRef
    , protected router: Router
    , protected toast: ToastrService
    , private http: HttpClient
    , private authService: AuthenticationService
  ) {
  }

  redirectToLogin() {
    this.router.navigate([CONSTANT.loginPath]).then(_ => this.bsRef.hide());
  }

  login() {
    let valid = true;
    if (!this.param.email || !this.param.password) {
      this.toast.warning('Please fill in all fields', 'warning');
      valid = false;
    }
    if (valid) {
      this.authService.login(this.param)
        .subscribe(res => {
          if (res.success) {
            this.toast.success('Login success');
            this.bsRef.hide();
          } else {
            this.toast.error(res.message, 'Error');
          }
        });
    }
  }
}


export class ParamLoginModal {
  constructor(
    public email: string = '',
    public password: string = ''
  ) {
  }
}
