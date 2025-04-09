import {Component, effect, OnInit, signal} from '@angular/core';
import {UserService} from '../../../share/service/user.service';
import {AuthenticationService} from '../../../share/service/authentication.service';
import {interval} from 'rxjs';
import {NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-navbar',
  imports: [
    NgOptimizedImage
  ],
  templateUrl: './navbar.component.html',
  standalone: true,
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit {
  date = signal(new Date());

  constructor(
    protected userService: UserService,
    private authService: AuthenticationService
  ) {
    effect(() => {
      interval(1000).subscribe(_ => {
        this.date.set(new Date());
      });
    });
  }

  ngOnInit(): void {
  }

  logout() {
    this.authService.logout();
  }
}
