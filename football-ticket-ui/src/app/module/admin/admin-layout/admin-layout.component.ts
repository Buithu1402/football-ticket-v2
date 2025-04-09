import {AfterViewInit, Component, OnInit, ViewEncapsulation} from '@angular/core';
import {SidebarComponent} from '../sidebar/sidebar.component';
import {NavbarComponent} from '../navbar/navbar.component';
import {FooterComponent} from '../footer/footer.component';
import {ActivatedRoute, Router, RouterOutlet} from '@angular/router';
import {LibServiceService} from '../../../share/service/lib-service.service';

@Component({
  selector: 'app-admin-layout',
  imports: [
    SidebarComponent,
    NavbarComponent,
    FooterComponent,
    RouterOutlet
  ],
  templateUrl: './admin-layout.component.html',
  standalone: true,
  styleUrls: [
    './admin-layout.component.scss'
  ],
  providers: [LibServiceService],
  encapsulation: ViewEncapsulation.None
})
export class AdminLayoutComponent implements OnInit, AfterViewInit {
  constructor(private libService: LibServiceService, private router: Router, private route: ActivatedRoute) {
  }

  ngAfterViewInit(): void {
    this.libService.admin();
  }

  ngOnInit(): void {
  }
}
