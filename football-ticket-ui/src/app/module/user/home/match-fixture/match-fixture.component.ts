import {Component, OnInit} from '@angular/core';
import {LIB} from '../../../../share/constant';
import {HttpClient} from '@angular/common/http';
import {Match} from '../../../../share/model/Match';
import {forkJoin} from 'rxjs';
import {ResponseData} from '../../../../share/model/response-data.model';
import {Team} from '../../../../share/model/Team';
import {ToastrService} from 'ngx-toastr';
import {DatePipe} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'app-match-fixture',
  imports: [
    DatePipe
  ],
  templateUrl: './match-fixture.component.html',
  standalone: true,
  styleUrls: ['./match-fixture.component.scss']
})
export class MatchFixtureComponent implements OnInit {
  matches: Match[] = [];
  teams: Team[] = [];

  constructor(protected http: HttpClient, protected toast: ToastrService, protected router: Router) {
  }

  ngOnInit(): void {
    forkJoin([
      this.http.get<ResponseData<Match[]>>('api/public/match/top?n=6'),
      this.http.get<ResponseData<Team[]>>('api/public/team/top?n=6')
    ]).subscribe(([res1, res2]: ResponseData<any>[]) => {
      if (res1.success) {
        this.matches = res1.data;
      } else {
        this.toast.error(res1.message);
      }

      if (res2.success) {
        this.teams = res2.data;
      } else {
        this.toast.error(res2.message);
      }
    });
  }

  viewMatchDetail(matchId: number): void {
    this.router.navigate(['/fixture-detail'], {queryParams: {mid: matchId}});
  }
}
