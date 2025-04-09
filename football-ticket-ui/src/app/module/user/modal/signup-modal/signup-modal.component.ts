import {Component} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {LIB} from '../../../../share/constant';

@Component({
  selector: 'app-signup-modal',
  imports: [
  ],
  templateUrl: './signup-modal.component.html',
  standalone: true,
  styleUrls: ['./signup-modal.component.scss']
})
export class SignupModalComponent {
  constructor(protected toast: ToastrService) {
  }

  notify(mess: string, type: string) {
    switch (type) {
      case 'success':
        this.toast.success(mess);
        break;
      case 'error':
        this.toast.error(mess);
        break;
      default:
        this.toast.info(mess);
        break;
    }
  }
}
