import {Component, OnInit} from '@angular/core';
import {StoreService} from '../../../../share/service/store.service';
import {BreadCrumb} from '../../../../share/model/BreadCrumb';
import {ActivatedRoute} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {TeamDetailDTO} from '../../../../share/model/TeamDetailDTO';
import {ResponseData} from '../../../../share/model/response-data.model';
import {Team} from '../../../../share/model/Team';
import {ToastrService} from 'ngx-toastr';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-team-detail',
  imports: [
    DatePipe
  ],
  templateUrl: './team-detail.component.html',
  standalone: true,
  styleUrls: ['./team-detail.component.scss']
})
export class TeamDetailComponent implements OnInit {
  teamDetail: TeamDetailDTO = new TeamDetailDTO();

  constructor(protected storeService: StoreService,
              protected route: ActivatedRoute,
              protected toast: ToastrService,
              protected http: HttpClient) {
    this.storeService.setTitle('CHI TIẾT ĐỘI BÓNG');
    this.storeService.setBreadCrumb([
      new BreadCrumb('Trang chủ', '/home'),
      new BreadCrumb('Chi tiết đội bóng', '/team/detail')
    ]);
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const teamId = params['id'];
      this.http.get<ResponseData<TeamDetailDTO>>(`api/public/team/${teamId}`)
        .subscribe(res => {
          if (res.success) {
            this.teamDetail = res.data;
          } else {
            this.toast.error('Không thể tải thông tin đội bóng');
          }
        });
    });
  }
}
