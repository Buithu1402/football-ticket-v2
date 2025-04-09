import {Component, OnInit} from '@angular/core';
import {StoreService} from '../../../share/service/store.service';
import {PaginatorModule} from 'primeng/paginator';
import {FormsModule} from '@angular/forms';
import {Match} from '../../../share/model/Match';
import {ListFixtureComponent} from './list-fixture/list-fixture.component';
import {GridFixtureComponent} from './grid-fixture/grid-fixture.component';
import {NgClass} from '@angular/common';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {PagingData, ResponseData} from '../../../share/model/response-data.model';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-fixture',
  imports: [PaginatorModule, FormsModule, ListFixtureComponent, GridFixtureComponent, NgClass],
  templateUrl: './fixture.component.html',
  standalone: true,
  styleUrl: './fixture.component.scss'
})
export class FixtureComponent implements OnInit {
  mode: 'grid' | 'list' = 'list';
  matches: PagingData<Match> = new PagingData<Match>();
  lid: number = 0;

  constructor(protected storeService: StoreService,
              protected http: HttpClient,
              protected toast: ToastrService,
              protected route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.storeService.setTitle('Match Fixture');
    this.storeService.setBreadCrumb([
      {
        label: 'Home',
        url: '/'
      },
      {
        label: 'Match Fixture',
        url: '/fixture'
      }
    ]);
    this.route.queryParams.subscribe(params => {
      this.lid = params['lid'];
      if (!!this.lid) {
        this.getData();
      }
    });

  }

  getData(page: number = this.matches.page, size: number = this.matches.size): void {
    this.http.get<ResponseData<PagingData<Match>>>(`/api/public/match?page=${page}&size=${size}&leagueId=${this.lid}`)
      .subscribe(data => {
        if (data.success) {
          this.matches = data.data;
        } else {
          this.toast.error(data.message);
        }
      });
  }
}
