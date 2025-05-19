import {Component, OnInit} from '@angular/core';
import {PagingData, ResponseData} from '../../../../share/model/response-data.model';
import {StoreService} from '../../../../share/service/store.service';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {Router} from '@angular/router';
import {League} from '../../../../share/model/League';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {FormsModule} from '@angular/forms';
import {CurrencyPipe} from '@angular/common';

@Component({
  selector: 'app-leagues',
  imports: [
    PaginationComponent,
    FormsModule,
    CurrencyPipe
  ],
  templateUrl: './leagues.component.html',
  standalone: true,
  styleUrl: './leagues.component.scss'
})
export class LeaguesComponent implements OnInit {
  data: PagingData<League> = new PagingData<League>();

  constructor(
    protected storeService: StoreService
    , protected http: HttpClient
    , protected toastr: ToastrService
    , protected router: Router
  ) {
  }

  ngOnInit(): void {
    this.storeService.setTitle('Danh sách giải đấu');
    this.storeService.setBreadCrumb([
      {
        label: 'Trang chủ',
        url: '/'
      },
      {
        label: 'Giải đấu',
        url: '/leagues'
      }
    ]);
    this.getData();
  }

  getData(page: number = this.data.page, size: number = this.data.size): void {
    this.http.get<ResponseData<PagingData<League>>>(`/api/public/league?page=${page}&size=${size}`).subscribe(data => {
      if (data.success) {
        this.data = data.data;
      } else {
        this.toastr.error(data.message);
      }
    });
  }

  detail(item: League): void {
    this.router.navigate(['/fixture'], {
      queryParams: {
        lid: item.leagueId
      }
    }).then();
  }

  onPageChange(event: any): void {
    this.getData(event.page, event.itemsPerPage);
  }
}
