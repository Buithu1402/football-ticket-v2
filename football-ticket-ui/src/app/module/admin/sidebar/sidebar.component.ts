import {Component, OnInit} from '@angular/core';
import {ChildMenu, MenuAdmin} from '../../../share/model/Menu';
import {MultiMenuComponent} from './multi-menu/multi-menu.component';
import {SigleMenuComponent} from './sigle-menu/sigle-menu.component';
import {NgClass, NgOptimizedImage} from '@angular/common';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';

@Component({
  selector: 'app-sidebar',
  imports: [
    NgOptimizedImage,
    NgClass,
  ],
  templateUrl: './sidebar.component.html',
  standalone: true,
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  menuAdmin: MenuAdmin[] = [
    new MenuAdmin('Trang chủ', 'mdi-home-outline', '/admin/home'),
    new MenuAdmin('Quản lý người dùng', 'mdi-account-group-outline', '/admin/user'),
    new MenuAdmin('Quản lý giải đấu', 'mdi-book-open-page-variant-outline', '/admin/league', [
      new ChildMenu('Danh sách', '/admin/league/list'),
      new ChildMenu('Thêm', '/admin/league/upsert'),
    ]),
    new MenuAdmin('Quản lý đội', 'mdi-school-outline', '/admin/team', [
      new ChildMenu('Danh sách', '/admin/team/list'),
      new ChildMenu('Thêm', '/admin/team/upsert'),
    ]),
    new MenuAdmin('Quản lý trận đấu', 'mdi-school-outline', '/admin/match', [
      new ChildMenu('Danh sách', '/admin/match/list'),
      new ChildMenu('Thêm', '/admin/match/upsert')
    ]),
    new MenuAdmin('Quản lý hóa đơn', 'mdi-school-outline', '/admin/order', [
      new ChildMenu('Danh sách', '/admin/order/list'),
    ]),
    new MenuAdmin('Quản lý sân vận động', 'mdi-school-outline', '/admin/stadium', [
      new ChildMenu('Danh sách', '/admin/stadium/list'),
      new ChildMenu('Thêm', '/admin/stadium/upsert'),
    ])
  ];

  constructor(private router: Router) {
  }

  ngOnInit(): void {
    this.activeMenu();
    this.router.events.subscribe(_ => {
      this.activeMenu();
    });
  }

  activeMenu() {
    this.menuAdmin.forEach(menu => {
      if (this.router.url.includes(menu.path)) {
        menu.active = true;
        menu.expand = true;
      } else {
        menu.active = false;
        menu.expand = false
      }
    });
  }

  expandMenu(menu: MenuAdmin) {
    menu.expand = !menu.expand;
  }

  navigateTo(path: string) {
    this.router.navigateByUrl(path).then();
  }
}
