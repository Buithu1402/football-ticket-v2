import {Component, OnDestroy, OnInit} from '@angular/core';
import {BsModalService} from 'ngx-bootstrap/modal';
import {ToastrService} from 'ngx-toastr';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import {ResponseData} from '../../../share/model/response-data.model';
import {MatchDetailDTO, MatchSeat, MatchType} from '../../../share/model/MatchDetailDTO';
import {Match} from '../../../share/model/Match';
import {Team} from '../../../share/model/Team';
import {forkJoin} from 'rxjs';
import {FormsModule} from '@angular/forms';
import {BookingParam} from '../../../share/model/BookingParam';
import {AuthenticationService} from '../../../share/service/authentication.service';
import {LoginModalComponent} from '../modal/login-modal/login-modal.component';
import {NgOptimizedImage} from '@angular/common';
import {NgSelectComponent} from '@ng-select/ng-select';
import {PaymentMethodComponent} from '../payment-method/payment-method.component';

@Component({
  selector: 'app-fixture-detail',
  imports: [
    FormsModule,
    NgOptimizedImage,
    NgSelectComponent
  ],
  templateUrl: './fixture-detail.component.html',
  standalone: true,
  styleUrl: './fixture-detail.component.scss'
})
export class FixtureDetailComponent implements OnInit, OnDestroy {
  matchId = 0;
  matchDetail: MatchDetailDTO = new MatchDetailDTO();
  currentType: MatchType = new MatchType();
  days: number = 0;
  hours: number = 0;
  minutes: number = 0;
  seconds: number = 0;
  matches: Match[] = [];
  teams: Team[] = [];
  paramBooking: BookingParam = new BookingParam();
  currentSelectedSeats: MatchSeat[] = [];

  private countdownInterval: any;

  constructor(protected http: HttpClient,
              protected toast: ToastrService,
              protected bsModal: BsModalService,
              protected route: ActivatedRoute,
              protected authService: AuthenticationService
  ) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(param => {
      this.matchId = param['mid'];
    });

    forkJoin([
      this.http.get<ResponseData<Match[]>>('api/public/match/top?n=6'),
      this.http.get<ResponseData<Team[]>>('api/public/team/top?n=6'),
      this.http.get<ResponseData<MatchDetailDTO>>(`api/public/match/${this.matchId}`)
    ]).subscribe(([res1, res2, res3]: ResponseData<any>[]) => {
      if (res1.success) {
        this.matches = res1.data;
      } else {
        this.toast.error(res1.message);
      }

      if (res2.success) {
        this.teams = res2.data;
      } else {
        this.toast.error(res2.message);
      }

      this.setMatchDetail(res3);
    });
  }

  getMatchDetail() {
    this.http.get<ResponseData<MatchDetailDTO>>(`api/public/match/${this.matchId}`)
      .subscribe((res: any) => {
        this.setMatchDetail(res);
      });
  }

  setMatchDetail(res: any) {
    if (res.success) {
      this.matchDetail = res.data;
      if (this.matchDetail.types.length > 0) {
        this.currentType = this.matchDetail.types[0];
        this.currentSelectedSeats = this.currentType.seats;
        this.paramBooking.typeId = this.currentType.typeId;
      }
      this.startCountdown();
    } else {
      this.toast.error(res.message);
    }
  }

  changeType() {
    this.currentType = this.matchDetail.types.find(t => t.typeId == this.paramBooking.typeId) || new MatchType();
    this.currentSelectedSeats = this.currentType.seats;
    this.paramBooking.typeId = this.currentType.typeId;
  }

  private startCountdown(): void {
    if (!this.matchDetail.matchDate || !this.matchDetail.matchTime) {
      return;
    }

    const matchStartTime = new Date(`${this.matchDetail.matchDate}T${this.matchDetail.matchTime}`);

    this.countdownInterval = setInterval(() => {
      const now = new Date();
      const timeDiff = matchStartTime.getTime() - now.getTime();

      if (timeDiff <= 0) {
        clearInterval(this.countdownInterval);
        this.matchDetail.matchStatus = 'started';
        this.days = this.hours = this.minutes = this.seconds = 0;
      } else {
        this.days = Math.floor(timeDiff / (1000 * 60 * 60 * 24)); // days
        this.hours = Math.floor((timeDiff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)); // hours
        this.minutes = Math.floor((timeDiff % (1000 * 60 * 60)) / (1000 * 60)); // minutes
        this.seconds = Math.floor((timeDiff % (1000 * 60)) / 1000); // seconds
      }
    }, 1_000);
  }

  buyTicket() {
    if (!this.authService.isLoggedIn) {
      this.toast.warning('Please login to buy ticket');
      this.bsModal.show(LoginModalComponent, {
        class: 'modal-lg modal-dialog-centered'
      });
      return
    }

    if (this.paramBooking.typeId == 0) {
      this.toast.warning('Please select ticket type');
      return;
    }

    if (this.paramBooking.seatIds.length == 0) {
      this.toast.warning('Please select seat');
      return;
    }

    this.paramBooking.matchId = this.matchId;
    this.paramBooking.typeId = this.currentType.typeId;


    this.bsModal.show(PaymentMethodComponent, {
      class: 'modal-lg modal-dialog-centered',
    }).content?.eventOut.subscribe((type: string) => {
      if (type === 'vnpay' || type === 'visa') {
        this.paramBooking.paymentMethod = type;
        this.http.post<ResponseData<any>>('api/booking', this.paramBooking)
          .subscribe(res => {
            this.getMatchDetail();
            if (res.success) {
              this.toast.success('Booking successfully');
              window.location.href = res.data;
            } else {
              this.toast.error(res.message);
            }
          });
      }
    });
  }

  ngOnDestroy(): void {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
  }
}
