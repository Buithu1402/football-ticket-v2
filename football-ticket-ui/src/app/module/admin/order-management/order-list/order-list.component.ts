import {Component, OnInit} from '@angular/core';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BookingHistoryDTO} from '../../../../share/model/BookingHistoryDTO';
import {PagingData, ResponseData} from '../../../../share/model/response-data.model';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {debounceTime, Subject} from 'rxjs';

@Component({
  selector: 'app-order-list',
  imports: [
    PaginationComponent,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './order-list.component.html',
  standalone: true,
  styleUrl: './order-list.component.scss'
})
export class OrderListComponent implements OnInit {
  data: PagingData<BookingHistoryDTO> = new PagingData<BookingHistoryDTO>();
  keyword = '';
  searchSubject = new Subject<string>();

  constructor(
    protected http: HttpClient,
    protected toast: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.getHistory();
    this.searchSubject.pipe(
      debounceTime(500) // Wait for 500ms before calling API
    ).subscribe(() => this.getHistory());
  }

  getHistory(): void {
    this.http.get<ResponseData<PagingData<BookingHistoryDTO>>>(`/api/booking?page=${this.data.page}&size=${this.data.size}&key=${this.keyword}`)
      .subscribe(data => {
        if (data.success) {
          this.data = data.data;
        } else {
          this.toast.error(data.message);
        }
      });
  }

  pageChanged(event: any): void {
    this.data.page = event.page;
    this.getHistory();
  }

  searchData() {
    this.searchSubject.next(this.keyword);
  }
}
