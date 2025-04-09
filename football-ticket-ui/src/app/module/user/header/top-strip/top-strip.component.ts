import {Component, Input} from '@angular/core';
import {DialogService, DynamicDialogModule} from 'primeng/dynamicdialog';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {AuthenticationService} from '../../../../share/service/authentication.service';

@Component({
  selector: 'app-top-strip',
  imports: [DynamicDialogModule, RouterLinkActive, RouterLink],
  templateUrl: './top-strip.component.html',
  standalone: true,
  styleUrl: './top-strip.component.scss',
  providers: [DialogService],
})
export class TopStripComponent {
  @Input() isLogin = false;

  constructor(protected dialogService: DialogService
    , protected authService: AuthenticationService) {
  }
}
