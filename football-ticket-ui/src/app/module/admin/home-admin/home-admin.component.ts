import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {LeagueStatistic} from '../../../share/model/Statistic';
import {forkJoin} from 'rxjs';
import {PagingData, ResponseData} from '../../../share/model/response-data.model';
import {ToastrService} from 'ngx-toastr';
import {BsDatepickerModule} from 'ngx-bootstrap/datepicker';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BaseChartDirective} from 'ng2-charts';
import {ChartConfiguration, ChartData} from 'chart.js';
import {CHART_OPTIONS} from '../../../share/constant';
import {DateService} from '../../../share/service/date.service';
import {StatisticTicketDTO} from '../../../share/model/StatisticTicketDTO';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {Chart} from '../../../share/model/Chart';

@Component({
  selector: 'app-home-admin',
  imports: [
    BsDatepickerModule,
    ReactiveFormsModule,
    FormsModule,
    BaseChartDirective,
    PaginationComponent
  ],
  templateUrl: './home-admin.component.html',
  standalone: true,
  styleUrl: './home-admin.component.scss'
})
export class HomeAdminComponent implements OnInit {
  public barChartOptionsRevenueDayOrMonth: ChartConfiguration<'bar'>['options'] = CHART_OPTIONS.bar;
  public barChartDataRevenueDayOrMonth: ChartData<'bar'> = CHART_OPTIONS.init;

  public barChartOptionsRevenueLeague: ChartConfiguration<'bar'>['options'] = CHART_OPTIONS.bar;
  public barChartDataRevenueLeague: ChartData<'bar'> = CHART_OPTIONS.init;
  league: LeagueStatistic[] = [];
  config = {
    rangeInputFormat: 'YYYY-MM-DD',
    dateInputFormat: 'YYYY-MM-DD'
  };
  dataTicket: PagingData<StatisticTicketDTO> = new PagingData<StatisticTicketDTO>();


  fromDate: Date = new Date();
  toDate: Date = new Date();
  type: 'D' | 'M' = 'D';

  constructor(
    protected http: HttpClient,
    protected toast: ToastrService,
    protected dateService: DateService
  ) {
  }

  ngOnInit(): void {
    const fd = this.dateService.getFormatDate(this.fromDate, 'yyyy-MM-dd');
    const td = this.dateService.getFormatDate(this.toDate, 'yyyy-MM-dd');
    forkJoin([
      this.http.get<ResponseData<LeagueStatistic[]>>('api/statistic/league'),
      this.http.get<ResponseData<Chart>>(`api/statistic/revenue?from=${fd}&to=${td}&type=${this.type}`),
      this.http.get<ResponseData<PagingData<StatisticTicketDTO>>>(`api/statistic/ticket?page=${this.dataTicket.page}&size=${this.dataTicket.size}`),
      this.http.get<ResponseData<Chart>>(`api/statistic/revenue-league?from=${fd}&to=${td}`)
    ]).subscribe(([res1, res2, res3, res4]) => {
      if (res1.success) {
        this.league = res1.data;
      } else {
        this.toast.error(res1.message);
      }
      if (res2.success) {
        const datasets = [...res2.data.datasets];
        this.barChartDataRevenueDayOrMonth = {
          labels: res2.data.labels.length ? [...res2.data.labels] : [],
          datasets: datasets
        }
      } else {
        this.toast.error(res2.message);
      }

      if (res3.success) {
        this.dataTicket = res3.data;
      } else {
        this.toast.error(res3.message);
      }

      if (res4.success) {
        const datasets = [...res4.data.datasets];
        this.barChartDataRevenueLeague = {
          labels: res4.data.labels.length ? [...res4.data.labels] : [],
          datasets: datasets
        }
      } else {
        this.toast.error(res4.message);
      }
    });
  }

  submit() {
    const fd = this.dateService.getFormatDate(this.fromDate, 'yyyy-MM-dd');
    const td = this.dateService.getFormatDate(this.toDate, 'yyyy-MM-dd');
    forkJoin([
      this.http.get<ResponseData<Chart>>(`api/statistic/revenue?from=${fd}&to=${td}&type=${this.type}`),
      this.http.get<ResponseData<Chart>>(`api/statistic/revenue-league?from=${fd}&to=${td}`)
    ]).subscribe(([res1, res2]) => {
      if (res1.success) {
        const datasets = [...res1.data.datasets];
        this.barChartDataRevenueDayOrMonth = {
          labels: [...res1.data.labels],
          datasets: datasets
        }
      } else {
        this.toast.error(res1.message);
      }

      if (res2.success) {
        const datasets = [...res2.data.datasets];
        this.barChartDataRevenueLeague = {
          labels: res2.data.labels.length ? [...res2.data.labels] : [],
          datasets: datasets
        }
      } else {
        this.toast.error(res2.message);
      }
    });
  }

  pageChanged(e: any) {
    this.dataTicket.page = e.page;
    this.http.get<ResponseData<PagingData<StatisticTicketDTO>>>(`api/statistic/ticket?page=${this.dataTicket.page}&size=${this.dataTicket.size}`)
      .subscribe(res3 => {
        if (res3.success) {
          this.dataTicket = res3.data;
        } else {
          this.toast.error(res3.message);
        }
      });
  }

}
