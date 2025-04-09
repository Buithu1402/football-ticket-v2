import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {StoreService} from '../../../share/service/store.service';
import {BookingHistoryDTO} from '../../../share/model/BookingHistoryDTO';
import {PagingData, ResponseData} from '../../../share/model/response-data.model';
import {CurrencyPipe} from '@angular/common';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-history',
  imports: [
    CurrencyPipe,
    PaginationComponent,
    FormsModule
  ],
  templateUrl: './history.component.html',
  standalone: true,
  styleUrl: './history.component.scss'
})
export class HistoryComponent implements OnInit {
  histories: PagingData<BookingHistoryDTO> = new PagingData<BookingHistoryDTO>();

  constructor(
    protected http: HttpClient,
    protected toast: ToastrService,
    protected storeService: StoreService
  ) {
    this.storeService.setTitle('History');
    this.storeService.setBreadCrumb([
      {
        label: 'Home',
        url: '/'
      },
      {
        label: 'History',
        url: '/history'
      }
    ]);
  }

  ngOnInit(): void {
    this.getHistory();
  }

  getHistory(): void {
    this.http.get<ResponseData<PagingData<BookingHistoryDTO>>>(`/api/booking/history?page=${this.histories.page}&size=${this.histories.size}`)
      .subscribe(data => {
        if (data.success) {
          this.histories = data.data;
        } else {
          this.toast.error(data.message);
        }
      });
  }

  pageChanged(event: any): void {
    this.histories.page = event.page;
    this.getHistory();
  }
}
