import {Component, EventEmitter, OnInit, Output, signal} from '@angular/core';
import {SeatParam, StadiumParam} from '../../../../share/model/StadiumDTO';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {BsModalRef, BsModalService} from 'ngx-bootstrap/modal';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {TransferFileService} from '../../../../share/service/transfer-file.service';
import {ResponseData} from '../../../../share/model/response-data.model';
import {StadiumSeatComponent} from '../stadium-seat/stadium-seat.component';
import {SeatDTO} from '../../../../share/model/SeatDTO';

@Component({
  selector: 'app-stadium-upsert',
  imports: [
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './stadium-upsert.component.html',
  standalone: true,
  styleUrl: './stadium-upsert.component.scss',
  providers: [TransferFileService]
})
export class StadiumUpsertComponent implements OnInit {
  title = signal('Create Stadium');
  stadium = signal(new StadiumParam());
  @Output() eventOut = new EventEmitter<boolean>();
  isUpdate = signal(false);
  types: TypeSeat[] = [];

  constructor(
    protected http: HttpClient,
    protected toast: ToastrService,
    protected bsRef: BsModalRef,
    protected fileService: TransferFileService,
    protected bsModal: BsModalService
  ) {
  }

  ngOnInit(): void {
    if (this.isUpdate()) {
      this.title.set('Update Stadium');
      this.http.get<ResponseData<SeatDTO[]>>(`api/stadium/${this.stadium().stadiumId}/seats`)
        .subscribe(res => {
          if (res.success) {
            this.stadium().seats2 = res.data;
          } else {
            this.toast.error(res.message);
          }
        });
    }
    this.http.get<ResponseData<TypeSeat[]>>('api/stadium/seat/type')
      .subscribe(res => {
        if (res.success) {
          this.types = res.data;
        } else {
          this.toast.error(res.message);
        }
      });
  }

  submit() {
    if (this.stadium().stadiumName === '') {
      this.toast.error('Stadium name is required');
      return;
    }
    if (this.stadium().address === '') {
      this.toast.error('Address is required');
      return;
    }
    if (this.stadium().seats.length === 0) {
      this.toast.error('Seat is required');
      return;
    }
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(this.stadium())], {type: 'application/json'}));
    if (this.fileService.fileData) {
      formData.append('file', this.fileService.fileData);
    }
    this.http.post<ResponseData<string>>('api/stadium', formData)
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Success');
          if (this.isUpdate()) {
            this.eventOut.emit(true);
            this.bsRef.hide();
          } else {
            this.stadium.set(new StadiumParam());
            this.clearUpload();
          }
        } else {
          this.toast.error(res.message);
        }
      });
  }

  clearUpload() {
    this.fileService.reset();
  }

  addRowSeat() {
    this.stadium().seats.push(new SeatParam());
  }
}

export class TypeSeat {
  public constructor(
    public typeId: number = 0,
    public name: string = ''
  ) {
  }
}
