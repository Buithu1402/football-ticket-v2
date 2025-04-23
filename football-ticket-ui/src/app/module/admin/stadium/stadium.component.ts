import {Component, OnInit, signal} from '@angular/core';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {PagingData, ResponseData} from '../../../share/model/response-data.model';
import {debounceTime, Subject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {StadiumDTO, StadiumParam} from '../../../share/model/StadiumDTO';
import {ConfirmModalComponent} from '../../common/confirm-modal/confirm-modal.component';
import {BsModalService} from 'ngx-bootstrap/modal';
import {StadiumUpsertComponent} from './stadium-upsert/stadium-upsert.component';
import {StadiumSeatComponent} from './stadium-seat/stadium-seat.component';

@Component({
  selector: 'app-stadium',
  imports: [
    PaginationComponent,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './stadium.component.html',
  standalone: true,
  styleUrl: './stadium.component.scss'
})
export class StadiumComponent implements OnInit {
  data: PagingData<StadiumDTO> = new PagingData<StadiumDTO>();
  searchSubject = new Subject<string>();
  searchKey = '';

  constructor(
    protected http: HttpClient,
    protected toast: ToastrService,
    protected bsModal: BsModalService
  ) {
  }

  ngOnInit(): void {
    this.getData();
    this.searchSubject.pipe(
      debounceTime(500) // Wait for 500ms before calling API
    ).subscribe(() => this.getData());
  }

  pageChanged(event: any): void {
    this.getData(event.page, this.data.size);
  }

  getData(page: number = this.data.page, size: number = this.data.size) {
    this.http.get<ResponseData<PagingData<StadiumDTO>>>(`/api/stadium?page=${page}&size=${size}&key=${this.searchKey}`)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;
        } else {
          this.toast.error(res.message);
        }
      });
  }

  searchData() {
    this.searchSubject.next(this.searchKey);
  }

  update(item: StadiumDTO) {
    this.bsModal.show(StadiumUpsertComponent, {
      class: 'modal-xl modal-dialog-centered',
      initialState: {
        isUpdate: signal(true),
        title: signal('Update Stadium'),
        stadium: signal(new StadiumParam(item.stadiumId, item.stadiumName, item.address, item.image))
      }
    }).content?.eventOut.subscribe((result: boolean) => {
      if (result) {
        this.getData();
      }
    });
  }

  delete(item: StadiumDTO) {
    this.bsModal.show(ConfirmModalComponent, {
      class: 'modal-dialog-centered',
      initialState: {
        title: signal('Xóa sân vận động'),
        message: signal('Bạn có chắc chắn muốn xóa sân vận động này không?'),
      }
    }).content?.eventOut.subscribe((result: boolean) => {
      if (result) {
        this.http.delete<ResponseData<string>>(`/api/stadium/${item.stadiumId}`)
          .subscribe(res => {
            if (res.success) {
              this.toast.success('Xóa sân vận động thành công');
              this.getData();
            } else {
              this.toast.error(res.message);
            }
          });
      }
    });
  }

  seats(id: number) {
    this.bsModal.show(StadiumSeatComponent, {
      class: 'modal-xl modal-dialog-centered',
      initialState: {
        stadiumId: signal(id)
      }
    })
  }
}
