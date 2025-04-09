import {Component, OnInit, signal} from '@angular/core';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {PagingData, ResponseData} from '../../../../share/model/response-data.model';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {BsModalService} from 'ngx-bootstrap/modal';
import {Match} from '../../../../share/model/Match';
import {MatchListPayload} from '../../../../share/model/MatchListPayload';
import {DateService} from '../../../../share/service/date.service';
import {DatePipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MatchTicketComponent} from '../match-ticket/match-ticket.component';
import {UpdateScoreComponent} from '../update-score/update-score.component';
import {debounceTime, Subject} from 'rxjs';

@Component({
  selector: 'app-match-list',
  imports: [
    PaginationComponent,
    DatePipe,
    FormsModule
  ],
  templateUrl: './match-list.component.html',
  standalone: true,
  styleUrl: './match-list.component.scss'
})
export class MatchListComponent implements OnInit {
  data: PagingData<Match> = new PagingData<Match>();
  param: MatchListPayload = new MatchListPayload();
  searchSubject = new Subject<string>();

  constructor(protected http: HttpClient,
              protected toast: ToastrService,
              protected bsModal: BsModalService,
              protected dateService: DateService
  ) {
  }

  ngOnInit(): void {
    this.getData();
    this.searchSubject.pipe(
      debounceTime(500) // Wait for 500ms before calling API
    ).subscribe(() => this.getData());
  }

  getData(date: Date | null = null) {
    this.param.date = this.dateService.getFormatDate(date, 'yyyy-MM-dd');
    this.http.post<ResponseData<PagingData<Match>>>(`/api/match/all`, this.param)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;
        } else {
          this.toast.error(res.message);
        }
      });
  }

  pageChanged(event: any): void {
    this.param.page = event.page;
    this.getData();
  }

  ticket(match: Match) {
    this.bsModal.show(MatchTicketComponent, {
      class: 'modal-lg modal-dialog-centered modal-dialog-scrollable draggable',
      initialState: {
        matchId: signal(match.matchId)
      }
    });
  }

  update(match: Match) {
    this.bsModal.show(UpdateScoreComponent, {
      class: 'modal-lg modal-dialog-centered modal-dialog-scrollable draggable',
      initialState: {
        matchId: signal(match.matchId)
      }
    })?.content?.eventOut.subscribe(_ => {
      this.getData();
    });
  }

  searchData() {
    this.searchSubject.next(this.param.key);
  }


}
