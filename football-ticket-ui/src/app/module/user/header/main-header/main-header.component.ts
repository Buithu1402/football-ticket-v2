import {Component} from '@angular/core';
import {NgClass, NgOptimizedImage} from '@angular/common';
import {Router} from '@angular/router';
import {Menu} from '../../../../share/model/Menu';
import {HttpClient} from '@angular/common/http';
import {ResponseData} from '../../../../share/model/response-data.model';
import {League} from '../../../../share/model/League';

@Component({
  selector: 'app-main-header',
  imports: [
    NgOptimizedImage,
    NgClass
  ],
  templateUrl: './main-header.component.html',
  standalone: true,
  styleUrls: ['./main-header.component.scss']
})
export class MainHeaderComponent {
  menus: Menu[] = [
    new Menu('TRANG CHỦ', '/home', '', [], '', false, true),
    new Menu('GIẢI ĐẤU', '/leagues', 'Các giải đấu phổ biến', [], 'assets/images/ticket_488x275.png'),
    new Menu('ĐỘI BÓNG', '/team/list'),
    new Menu('MUA VÉ', '/login-register', '', [], '', true)
  ];

  // get, post, put, patch, delete
  constructor(
    protected router: Router,
    protected http: HttpClient,
  ) {
    this.menus.forEach(menu => {
      menu.isActive = this.router.url.includes(menu.link);
    });
    this.http.get<ResponseData<League[]>>('api/public/league/top')
      .subscribe(res => {
        if (res.success) {
          const m = res.data.map(league => new Menu(league.name, `/fixture?lid=${league.leagueId}`));
          this.menus[1].children = [
            new Menu('TẤT CẢ CÁC GIẢI ĐẤU', '/leagues'),
            ...m
          ];
        }
      });
  }

  navigate(link: string, idx: number): void {
    this.menus.forEach(menu => menu.isActive = false);
    this.menus[idx].isActive = true;
    this.router.navigateByUrl(link).then();
  }
}
