import {AfterViewInit, Component, OnInit, signal} from '@angular/core';
import {HeaderComponent} from '../header/header.component';
import {BannerComponent} from '../banner/banner.component';
import {FooterComponent} from '../footer/footer.component';
import {ActivatedRoute, Router, RouterOutlet} from '@angular/router';
import {StoreService} from '../../../share/service/store.service';
import {SuperHeaderComponent} from '../header/super-header/super-header.component';
import {SearchModalComponent} from '../modal/search-modal/search-modal.component';
import {AuthenticationService} from '../../../share/service/authentication.service';
import {NgClass} from '@angular/common';
import {LibServiceService} from '../../../share/service/lib-service.service';

@Component({
  selector: 'app-user-layout',
  imports: [
    HeaderComponent,
    BannerComponent,
    FooterComponent,
    RouterOutlet,
    SuperHeaderComponent,
    SearchModalComponent,
    NgClass
  ],
  templateUrl: './user-layout.component.html',
  standalone: true,
  styleUrls: ['./user-layout.component.scss'],
  providers: [LibServiceService]
})
export class UserLayoutComponent implements OnInit, AfterViewInit {
  isHome = signal(false);
  isFixtureDetail = signal(false);
  urlTemp = signal('user');

  constructor(
    protected router: Router,
    protected storeService: StoreService,
    protected authService: AuthenticationService,
    private libService: LibServiceService,
    private activeRoute: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    if (this.authService.isAdmin) {
      this.router.navigate(['/admin']).then();
      return;
    }
    this.isHome.set(this.router.url.includes('home'));
    this.isFixtureDetail.set(this.router.url.includes('fixture-detail'));

    this.router.events.subscribe((_) => {
        this.isHome.set(this.router.url.includes('home'));
        this.isFixtureDetail.set(this.router.url.includes('fixture-detail'));
        const isAdmin = this.router.url.includes('admin') ? 'admin' : 'user';
        if (isAdmin !== this.urlTemp()) {
          window.location.reload();
        }
      }
    )
    ;
  }

  ngAfterViewInit(): void {
    this.libService.user();
  }
}
