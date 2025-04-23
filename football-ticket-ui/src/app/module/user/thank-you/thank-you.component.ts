import {Component, OnInit} from '@angular/core';
import {StoreService} from '../../../share/service/store.service';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-thank-you',
  imports: [],
  templateUrl: './thank-you.component.html',
  standalone: true,
  styleUrl: './thank-you.component.scss'
})
export class ThankYouComponent implements OnInit {
  status = '';

  constructor(
    protected storeService: StoreService,
    protected http: HttpClient,
    protected router: Router,
    protected activatedRoute: ActivatedRoute,
    protected toast: ToastrService
  ) {
    this.storeService.setTitle('Cảm ơn');
    this.storeService.setBreadCrumb([
      {
        label: 'Trang chủ',
        url: '/home'
      },
      {
        label: 'Cảm ơn',
        url: '/thank-you'
      }
    ]);
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(param => {
      const isVnPay = this.router.url.includes('vn-pay');
      let txnRef: string;
      let isSuccess: boolean;
      if (isVnPay) {
        txnRef = param['vnp_TxnRef'];
        const response = param['vnp_ResponseCode'];
        if (response === '00') {
          this.status = 'success';
          isSuccess = true;
        } else {
          this.status = 'fail';
          isSuccess = false;
        }
      } else {
        txnRef = param['txnRef'];
        const response = param['s'];

        if (response === 'success') {
          this.status = 'success';
          isSuccess = true;
        } else {
          this.status = 'fail';
          isSuccess = false;
        }
      }

      this.http.post('api/booking/confirm', {
        txnRef,
        success: isSuccess
      }).subscribe((res: any) => {
        if (!res.success) {
          this.toast.error('Có lỗi xảy ra trong quá trình xác nhận đơn hàng');
          this.status = 'fail';
        }
      })
    });
  }

  goHome(): void {
    this.router.navigate(['/home']).then();
  }
}
