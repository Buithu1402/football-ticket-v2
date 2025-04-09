import {Component, OnInit} from '@angular/core';
import {SlickCarouselModule} from 'ngx-slick-carousel';
import {HttpClient} from '@angular/common/http';
import {Match} from '../../../../share/model/Match';
import {ResponseData} from '../../../../share/model/response-data.model';
import {ToastrService} from 'ngx-toastr';
import {DatePipe} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'app-fixture-slider',
  imports: [
    SlickCarouselModule,
    DatePipe
  ],
  templateUrl: './fixture-slider.component.html',
  standalone: true,
  styleUrls: ['./fixture-slider.component.scss']
})
export class FixtureSliderComponent implements OnInit {
  matches: Match[] = [];
  slideConfig = {
    slidesToShow: 3,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 2000,
    // infinite: true,
    dots: false,
    responsive: [
      {
        breakpoint: 1024,
        settings: {
          slidesToShow: 3,
          slidesToScroll: 1,
          infinite: true,
        }
      },
      {
        breakpoint: 800,
        settings: {
          slidesToShow: 2,
          slidesToScroll: 1
        }
      },
      {
        breakpoint: 400,
        settings: {
          slidesToShow: 1,
          slidesToScroll: 1
        }
      }
    ]
  };

  constructor(
    protected http: HttpClient,
    protected toast: ToastrService,
    protected router: Router,
  ) {
  }

  ngOnInit(): void {
    this.http.get<ResponseData<Match[]>>('api/public/match/top')
      .subscribe(res => {
        if (res.success) {
          this.matches = res.data;
        } else {
          this.toast.error(res.message);
        }
      });
  }

  viewDetail(matchId: number): void {
    this.router.navigate(['/fixture-detail'], {queryParams: {mid: matchId}});
  }
}
