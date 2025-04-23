import {Component} from '@angular/core';
import {SlickCarouselModule} from 'ngx-slick-carousel';
import {NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-our-partner',
  imports: [
    SlickCarouselModule,
    NgOptimizedImage
  ],
  templateUrl: './our-partner.component.html',
  standalone: true,
  styleUrls: ['./our-partner.component.scss']
})
export class OurPartnerComponent {
  sliderConfig = {
    slidesToShow: 4,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 2000,
    infinite: true,
    dots: false,
    prevArrow: "<span class='slick-arrow-left'><i class='icon-arrows-2'></i></span>",
    nextArrow: "<span class='slick-arrow-right'><i class='icon-arrows-2'></i></span>",
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
  protected readonly Array = Array;
  images = [
    "assets/images/extra-images/partner-logo-1.jpg",
    "assets/images/extra-images/partner-logo-2.jpg",
    "assets/images/extra-images/partner-logo-3.jpg",
    "assets/images/extra-images/partner-logo-1.jpg",
    "assets/images/extra-images/partner-logo-3.jpg",
  ]
}
