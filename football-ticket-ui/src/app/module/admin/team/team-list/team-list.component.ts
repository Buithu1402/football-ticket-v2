import {Component, OnInit, signal} from '@angular/core';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {PagingData, ResponseData} from '../../../../share/model/response-data.model';
import {League} from '../../../../share/model/League';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {BsModalService} from 'ngx-bootstrap/modal';
import {Team} from '../../../../share/model/Team';
import {FormsModule} from '@angular/forms';
import {TeamUpsertComponent} from '../team-upsert/team-upsert.component';
import {ConfirmModalComponent} from '../../../common/confirm-modal/confirm-modal.component';
import {debounceTime, Subject} from 'rxjs';

@Component({
  selector: 'app-team-list',
  imports: [
    PaginationComponent,
    FormsModule
  ],
  templateUrl: './team-list.component.html',
  standalone: true,
  styleUrl: './team-list.component.scss'
})
export class TeamListComponent implements OnInit {
  data: PagingData<Team> = new PagingData<Team>();
  searchSubject = new Subject<string>();
  searchKey = '';
  constructor(protected http: HttpClient,
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

  getData(page: number = this.data.page, size: number = this.data.size) {
    this.http.get<ResponseData<PagingData<Team>>>(`/api/team?page=${page}&size=${size}&keyword=${this.searchKey}`)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;
        } else {
          this.toast.error('Không thể tải danh sách đội bóng');
        }
      });
  }

  pageChanged(event: any): void {
    this.getData(event.page, this.data.size);
  }

  update(item: Team) {
    this.bsModal.show(TeamUpsertComponent, {
      class: 'modal-xl modal-dialog-centered',
      initialState: {
        team: signal(JSON.parse(JSON.stringify(item))),
        isUpdate: signal(true),
      }
    }).content?.eventOut.subscribe((result: boolean) => {
      if (result) {
        this.getData();
      }
    });
  }

  delete(item: Team) {
    this.bsModal.show(ConfirmModalComponent, {
      class: 'modal-dialog-centered modal-lg',
      initialState: {
        title: signal('Xóa đội bóng'),
        message: signal(`Bạn có muốn xóa đội ${item.name}?`)
      }
    }).content?.eventOut.subscribe(res => {
      if (res) {
        this.http.delete<ResponseData<any>>(`api/team?teamId=${item.teamId}`)
          .subscribe(res => {
            if (res.success) {
              this.toast.success('Xóa đội bóng thành công');
              this.getData();
            } else {
              this.toast.error('Không thể xóa đội bóng');
            }
          });
      }
    });
  }

  searchData() {
    this.searchSubject.next(this.searchKey);
  }
}
