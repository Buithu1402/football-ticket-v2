import {Component, OnInit, signal} from '@angular/core';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {PagingData, ResponseData} from '../../../../share/model/response-data.model';
import {League} from '../../../../share/model/League';
import {HttpClient} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {BsModalService} from 'ngx-bootstrap/modal';
import {LeagueUpsertComponent} from '../league-upsert/league-upsert.component';
import {ConfirmModalComponent} from '../../../common/confirm-modal/confirm-modal.component';
import {debounceTime, Subject} from 'rxjs';

@Component({
  selector: 'app-league-list',
  imports: [
    PaginationComponent,
    FormsModule
  ],
  templateUrl: './league-list.component.html',
  standalone: true,
  styleUrl: './league-list.component.scss'
})
export class LeagueListComponent implements OnInit {
  data: PagingData<League> = new PagingData<League>();
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

  pageChanged(event: any): void {
    this.getData(event.page, this.data.size);
  }

  getData(page: number = this.data.page, size: number = this.data.size) {
    this.http.get<ResponseData<PagingData<League>>>(`/api/league?page=${page}&size=${size}&keyword=${this.searchKey}`)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;
        } else {
          this.toast.error(res.message);
        }
      });
  }

  update(item: League) {
    this.bsModal.show(LeagueUpsertComponent, {
      class: 'modal-xl modal-dialog-centered',
      initialState: {
        league: signal(JSON.parse(JSON.stringify(item))),
        isUpdate: signal(true),
        title: signal('Update League')
      }
    }).content?.eventOut.subscribe((result: boolean) => {
      if (result) {
        this.getData();
      }
    });
  }

  delete(item: League) {
    this.bsModal.show(ConfirmModalComponent, {
      class: 'modal-dialog-centered',
      initialState: {
        title: signal('Delete League'),
        message: signal('Are you sure you want to delete this league?')
      }
    }).content?.eventOut.subscribe((result: boolean) => {
      if (result) {
        this.http.delete<ResponseData<string>>(`/api/league/${item.leagueId}`)
          .subscribe(res => {
            if (res.success) {
              this.toast.success('Delete league successfully');
              this.getData();
            } else {
              this.toast.error(res.message);
            }
          });
      }
    });
  }

  searchData() {
    this.searchSubject.next(this.searchKey);
  }
}
