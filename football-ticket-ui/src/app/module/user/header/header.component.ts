import {Component, Input, NO_ERRORS_SCHEMA} from '@angular/core';
import {TopStripComponent} from './top-strip/top-strip.component';
import {MainHeaderComponent} from './main-header/main-header.component';

@Component({
  selector: 'app-header',
  imports: [
    TopStripComponent,
    MainHeaderComponent
  ],
  templateUrl: './header.component.html',
  standalone: true,
  styleUrls: ['./header.component.scss'],
  schemas: [NO_ERRORS_SCHEMA]
})
export class HeaderComponent {
  @Input() isLogin = false;

  constructor() {
  }
}
