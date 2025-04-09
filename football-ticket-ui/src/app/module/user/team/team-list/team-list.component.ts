import {Component, OnInit} from '@angular/core';
import {StoreService} from '../../../../share/service/store.service';
import {Team} from '../../../../share/model/Team';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {FormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {PagingData, ResponseData} from '../../../../share/model/response-data.model';
import {ToastrService} from 'ngx-toastr';
import {Router} from '@angular/router';

@Component({
  selector: 'app-team-list',
  imports: [
    PaginationComponent,
    FormsModule
  ],
  templateUrl: './team-list.component.html',
  standalone: true,
  styleUrls: ['./team-list.component.scss']
})
export class TeamListComponent implements OnInit {
  teams: PagingData<Team> = new PagingData<Team>();

  constructor(
    protected storeService: StoreService
    , protected http: HttpClient
    , protected toastr: ToastrService
    , protected router: Router
  ) {
  }

  ngOnInit(): void {
    this.storeService.setTitle('Team list');
    this.storeService.setBreadCrumb([
      {
        label: 'Home',
        url: '/'
      },
      {
        label: 'Team list',
        url: '/team/list'
      }
    ]);
    this.getData();
  }

  getData(page: number = this.teams.page, size: number = this.teams.size): void {
    this.http.get<ResponseData<PagingData<Team>>>(`/api/public/team?page=${page}&size=${size}`).subscribe(data => {
      if (data.success) {
        this.teams = data.data;
      } else {
        this.toastr.error(data.message);
      }
    });

  }

  onPageChange(event: any): void {
    this.getData(event.page, event.itemsPerPage);
  }

  detail(team: Team): void {
    this.router.navigate(['/team/detail'], {queryParams: {id: team.teamId}}).then();
  }
}
