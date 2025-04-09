import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {PagingData, ResponseData} from '../../../../share/model/response-data.model';
import {User} from '../../../../share/model/User';
import {ToastrService} from 'ngx-toastr';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-user-list',
  imports: [
    PaginationComponent,
    FormsModule
  ],
  templateUrl: './user-list.component.html',
  standalone: true,
  styleUrl: './user-list.component.scss'
})
export class UserListComponent implements OnInit {
  data: PagingData<User> = new PagingData<User>();

  constructor(private http: HttpClient,
              private toast: ToastrService) {
  }

  ngOnInit(): void {
    this.getData();
  }

  getData(page: number = this.data.page, size: number = this.data.size) {
    this.http.get<ResponseData<PagingData<User>>>(`/api/users?page=${page}&size=${size}`)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;
        } else {
          this.toast.error(res.message);
        }
      });

  }

  manage(user: User, action: string): void {
    this.http.patch<ResponseData<User>>(`/api/users/manage?uid=${user.userId}&action=${action}`, {})
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Success');
          this.getData();
        } else {
          this.toast.error(res.message);
        }
      });
  }

  pageChanged(event: any): void {
    this.getData(event.page, this.data.size);
  }
}
