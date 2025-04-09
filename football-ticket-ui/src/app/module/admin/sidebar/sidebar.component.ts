import {Component, OnInit} from '@angular/core';
import {ChildMenu, MenuAdmin} from '../../../share/model/Menu';
import {MultiMenuComponent} from './multi-menu/multi-menu.component';
import {SigleMenuComponent} from './sigle-menu/sigle-menu.component';
import {NgClass, NgOptimizedImage} from '@angular/common';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';

@Component({
  selector: 'app-sidebar',
  imports: [
    MultiMenuComponent,
    SigleMenuComponent,
    NgOptimizedImage,
    NgClass,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './sidebar.component.html',
  standalone: true,
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  menuAdmin: MenuAdmin[] = [
    new MenuAdmin('Dashboard', 'mdi-home-outline', '/admin/home'),
    new MenuAdmin('User Management', 'mdi-account-group-outline', '/admin/user'),
    new MenuAdmin('League Management', 'mdi-book-open-page-variant-outline', '/admin/league', [
      new ChildMenu('List', '/admin/league/list'),
      new ChildMenu('Create', '/admin/league/upsert'),
    ]),
    new MenuAdmin('Team Management', 'mdi-school-outline', '/admin/team', [
      new ChildMenu('List', '/admin/team/list'),
      new ChildMenu('Create', '/admin/team/upsert'),
    ]),
    new MenuAdmin('Match Management', 'mdi-school-outline', '/admin/match', [
      new ChildMenu('List', '/admin/match/list'),
      new ChildMenu('Create', '/admin/match/upsert')
    ]),
    new MenuAdmin('Order Management', 'mdi-school-outline', '/admin/order', [
      new ChildMenu('List', '/admin/order/list'),
    ]),
    new MenuAdmin('Stadium Management', 'mdi-school-outline', '/admin/stadium', [
      new ChildMenu('List', '/admin/stadium/list'),
      new ChildMenu('Create', '/admin/stadium/upsert'),
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
