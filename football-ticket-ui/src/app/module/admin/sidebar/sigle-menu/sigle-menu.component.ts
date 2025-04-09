import {Component, input} from '@angular/core';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {NgClass} from '@angular/common';
import {MenuAdmin} from '../../../../share/model/Menu';
import {CONSTANT} from '../../../../share/constant';

@Component({
  selector: 'app-sigle-menu',
  imports: [
    RouterLink,
    RouterLinkActive,
    NgClass
  ],
  templateUrl: './sigle-menu.component.html',
  standalone: true,
  styleUrls: ['./sigle-menu.component.scss']
})
export class SigleMenuComponent {
  menu = input<MenuAdmin>(new MenuAdmin());
}
