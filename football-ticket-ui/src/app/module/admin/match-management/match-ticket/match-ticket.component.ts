import {Component, OnInit, signal} from '@angular/core';
import {BsDatepickerDirective, BsDatepickerInputDirective} from 'ngx-bootstrap/datepicker';
import {NgSelectComponent} from '@ng-select/ng-select';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {TicketDTO} from '../../../../share/model/TicketDTO';
import {ResponseData} from '../../../../share/model/response-data.model';
import {ToastrService} from 'ngx-toastr';
import {TicketPayload} from '../../../../share/model/TicketPayload';
import {NgClass} from '@angular/common';
import {MASK_CONFIG} from '../../../../share/mask-input';
import {createMask, InputMaskModule} from '@ngneat/input-mask';
import {SeatDTO} from '../../../../share/model/SeatDTO';
import {forkJoin} from 'rxjs';

@Component({
  selector: 'app-match-ticket',
  imports: [
    BsDatepickerDirective,
    BsDatepickerInputDirective,
    NgSelectComponent,
    ReactiveFormsModule,
    FormsModule,
    NgClass,
    InputMaskModule
  ],
  templateUrl: './match-ticket.component.html',
  standalone: true,
  styleUrl: './match-ticket.component.scss'
})
export class MatchTicketComponent implements OnInit {
  title = signal('Add ticket for match');
  tickets: TicketDTO[] = [];
  matchId = signal(0);
  currencyInputMask = createMask(MASK_CONFIG.currency);
  params: TicketPayload[] = [];
  seats: SeatDTO[] = [];


  constructor(protected http: HttpClient,
              protected toast: ToastrService,) {
  }

  ngOnInit(): void {
    this.getData();
  }

  getData() {
    forkJoin([
      this.http.get<ResponseData<TicketDTO[]>>(`api/match/${this.matchId()}/tickets`),
      this.http.get<ResponseData<SeatDTO[]>>(`api/match/${this.matchId()}/seats`)
    ]).subscribe(([res1, res2]) => {
      if (res1.success) {
        this.tickets = res1.data;
        this.params = this.tickets.map(t => new TicketPayload(
          t.matchId ?? this.matchId(),
          t.typeId,
          t.name,
          t.price,
          t.price,
          t.description,
          !!t.price,
          t.seats?.map(s => s.seatNumber).join(', '),
          t.seats?.map(s => s.seatId).filter(s => !!s) ?? []
        ));
      } else {
        this.toast.error(res1.message);
      }

      if (res2.success) {
        this.seats = res2.data;
        this.calculateSeatAvailabe();
      } else {
        this.toast.error(res2.message);
      }
    });
  }

  calculateSeatAvailabe() {
    this.params.forEach(p => {
      const otherSelectedSeats = this.params.filter(p1 => p1 !== p).map(p1 => p1.seatIds).flat();
      p.availableSeats = this.seats.filter(s => !otherSelectedSeats.includes(s.seatId));
    });
  }

  add() {
    let valid = this.params.every(p => p.validPrice);

    if (valid) {
      // Add ticket for match
      this.http.post<ResponseData<number[]>>('api/match/tickets', this.params)
        .subscribe({
          next: res => {
            if (res.success) {
              this.toast.success('Add ticket success');
              this.getData();
            } else {
              this.params.forEach(p => {
                if (res.data.includes(p.typeId)) {
                  this.toast.error(`Ticket ${p.typeName} is failed`);
                }
              });
            }
          },
          error: () => {
            this.toast.error('Add ticket failed');
          }
        });
    }
  }

  validatePrice(p: TicketPayload) {
    p.price = Number(`${p.priceMask}`.replace(/\D/g, ''));
    p.validPrice = !p.price || p.price > 0;
  }
}
