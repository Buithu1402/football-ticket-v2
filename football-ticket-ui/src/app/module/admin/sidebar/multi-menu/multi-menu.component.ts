import {Component, input} from '@angular/core';
import {NgClass} from '@angular/common';
import {MenuAdmin} from '../../../../share/model/Menu';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {CONSTANT} from '../../../../share/constant';

@Component({
  selector: 'app-multi-menu',
  imports: [
    NgClass,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './multi-menu.component.html',
  standalone: true,
  styleUrls: ['./multi-menu.component.scss']
})
export class MultiMenuComponent {
  menu = input<MenuAdmin>(new MenuAdmin());

  expandMenu(menu: MenuAdmin) {

  }
}
