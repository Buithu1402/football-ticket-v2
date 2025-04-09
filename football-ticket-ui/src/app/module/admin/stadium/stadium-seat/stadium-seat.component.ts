import {Component, OnInit, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {SeatDTO} from '../../../../share/model/SeatDTO';
import {ResponseData} from '../../../../share/model/response-data.model';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BsModalRef, BsModalService} from 'ngx-bootstrap/modal';
import {ConfirmModalComponent} from '../../../common/confirm-modal/confirm-modal.component';

@Component({
  selector: 'app-stadium-seat',
  imports: [
    PaginationComponent,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './stadium-seat.component.html',
  standalone: true,
  styleUrl: './stadium-seat.component.scss'
})
export class StadiumSeatComponent implements OnInit {
  seats: SeatDTO[] = [];
  stadiumId = signal(-1);

  constructor(
    protected http: HttpClient,
    protected toast: ToastrService,
    protected bsRef: BsModalRef,
    protected bsModal: BsModalService
  ) {
  }

  ngOnInit(): void {
    this.loadSeats();
  }

  loadSeats(): void {
    this.http.get<ResponseData<SeatDTO[]>>(`/api/stadium/${this.stadiumId()}/seats`)
      .subscribe({
        next: (res) => {
          if (res.success) {
            this.seats = res.data;
          } else {
            this.toast.error(res.message);
          }
        },
        error: (err) => this.toast.error(err.error.message)
      });
  }

  delete(seatId: number): void {
    this.bsModal.show(ConfirmModalComponent, {
      class: 'modal-dialog-centered',
      initialState: {
        title: signal('Delete seat'),
        message: signal('Are you sure you want to delete this seat?')
      }
    }).content?.eventOut.subscribe(res => {
      if (res) {
        this.http.delete<ResponseData<SeatDTO>>(`/api/stadium/seat/${seatId}`)
          .subscribe({
            next: (res) => {
              if (res.success) {
                this.toast.success(`Delete success seat ${seatId}`);
                this.loadSeats();
              } else {
                this.toast.error(res.message);
              }
            },
            error: (err) => this.toast.error(err.error.message)
          });
      }
    });

  }
}
