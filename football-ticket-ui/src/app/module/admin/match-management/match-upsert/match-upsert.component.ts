import {Component, OnInit, signal} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {HttpClient} from '@angular/common/http';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {Match} from '../../../../share/model/Match';
import {NgSelectComponent} from '@ng-select/ng-select';
import {Team} from '../../../../share/model/Team';
import {BsDatepickerModule} from 'ngx-bootstrap/datepicker';
import {League} from '../../../../share/model/League';
import {PagingData, ResponseData} from '../../../../share/model/response-data.model';
import {DateService} from '../../../../share/service/date.service';
import {forkJoin} from 'rxjs';
import {StadiumDTO} from '../../../../share/model/StadiumDTO';

@Component({
  selector: 'app-match-upsert',
  imports: [
    ReactiveFormsModule,
    FormsModule,
    NgSelectComponent,
    BsDatepickerModule
  ],
  templateUrl: './match-upsert.component.html',
  standalone: true,
  styleUrl: './match-upsert.component.scss',
})
export class MatchUpsertComponent implements OnInit {
  title = signal('Tạo mới trận đấu');
  match = signal(new Match());
  teams = signal<Team[]>([]);
  stadiums = signal<StadiumDTO[]>([]);
  matchDate = signal(new Date());
  leagues = signal<League[]>([]);
  today = new Date();
  config = {
    withTimepicker: true,
    rangeInputFormat: 'DD/MM/YYYY HH:mm',
    dateInputFormat: 'DD/MM/YYYY HH:mm'
  };

  constructor(
    protected toast: ToastrService,
    protected http: HttpClient,
    protected bsRef: BsModalRef,
    protected dateService: DateService
  ) {
  }

  ngOnInit(): void {
    this.getData();
  }

  getData() {
    forkJoin([
      this.http.get<ResponseData<PagingData<Team>>>('api/team?size=100000'),
      this.http.get<ResponseData<PagingData<League>>>('api/league?size=100000'),
      this.http.get<ResponseData<PagingData<StadiumDTO>>>('api/stadium?size=100000')
    ]).subscribe(([teamRes, leagueRes, stadiums]) => {
      if (teamRes.success) {
        this.teams.set(teamRes.data.contents);
      } else {
        this.toast.error(teamRes.message);
      }
      if (leagueRes.success) {
        this.leagues.set(leagueRes.data.contents);
      } else {
        this.toast.error(leagueRes.message);
      }

      if (stadiums.success) {
        this.stadiums.set(stadiums.data.contents);
      } else {
        this.toast.error(stadiums.message);
      }
    });
  }

  add() {
    if (this.match().homeTeamId === this.match().awayTeamId) {
      this.toast.error('Đội nhà và đội khách không được trùng nhau');
      return;
    }
    this.match().matchDate = this.dateService.getFormatDate(this.matchDate(), 'yyyy-MM-dd') || '';
    this.match().matchTime = this.dateService.getFormatDate(this.matchDate(), 'HH:mm:ss') || '';
    // set default second is 0
    this.match().matchTime = this.match().matchTime.replace(/:\d{2}$/, ':00');

    this.http.post<ResponseData<string>>('api/match/upsert', this.match())
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Thành công');
          this.match.set(new Match());
          this.bsRef.hide();
        } else {
          this.toast.error(res.message);
        }
      });
  }
}
