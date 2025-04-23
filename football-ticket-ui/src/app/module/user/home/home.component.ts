import {Component} from '@angular/core';
import {FixtureSliderComponent} from './fixture-slider/fixture-slider.component';
import {MatchFixtureComponent} from './match-fixture/match-fixture.component';
import {OurPartnerComponent} from './our-partner/our-partner.component';
import {PostComponent} from './post/post.component';

@Component({
  selector: 'app-home',
  imports: [
    FixtureSliderComponent,
    MatchFixtureComponent,
    OurPartnerComponent,
    PostComponent,
  ],
  templateUrl: './home.component.html',
  standalone: true,
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {

}
