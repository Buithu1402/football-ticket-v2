import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {NgOptimizedImage} from '@angular/common';
import {SlickCarouselComponent, SlickCarouselModule} from 'ngx-slick-carousel';

@Component({
  selector: 'app-footer-widget',
  imports: [
    NgOptimizedImage,
    SlickCarouselModule
  ],
  templateUrl: './footer-widget.component.html',
  standalone: true,
  styleUrl: './footer-widget.component.scss'
})
export class FooterWidgetComponent implements AfterViewInit {
  @ViewChild('thumbSlider') thumbSlider!: SlickCarouselComponent;
  @ViewChild('listSlider') listSlider!: SlickCarouselComponent;
  thumbSlideConfig = {
    slidesToShow: 1,
    slidesToScroll: 1,
    arrows: false,
    fade: true,
    swipeToSlide: true,
    asNavFor: '.widget-images-list'  // Will be linked in template
  };

  listSlideConfig = {
    slidesToShow: 4,
    slidesToScroll: 1,
    asNavFor: '.widget-images-thumb',
    dots: false,
    vertical: false,
    prevArrow: `<span class='slick-arrow-left'><i class='fa fa-angle-left'></i></span>`,
    nextArrow: `<span class='slick-arrow-right'><i class='fa fa-angle-right'></i></span>`,
    centerMode: false,
    focusOnSelect: true,
    responsive: [
      {
        breakpoint: 1024,
        settings: {
          slidesToShow: 4,
          slidesToScroll: 1,
          infinite: true,
          vertical: false
        }
      },
      {
        breakpoint: 800,
        settings: {
          slidesToShow: 5,
          slidesToScroll: 1,
          vertical: false
        }
      },
      {
        breakpoint: 400,
        settings: {
          slidesToShow: 3,
          slidesToScroll: 1,
          vertical: false
        }
      }
    ]
  };

  thumbImages = [
    'assets/images/league/epl_360x150.png',
    'assets/images/league/seria_360x150.png',
    'assets/images/league/laliga_360x150.png',
    'assets/images/league/c1_360x150.png',
    'assets/images/league/duc_360x150.png'
  ];

  listImages = [
    'assets/images/league/epl_83x83.png',
    'assets/images/league/seria_83x83.png',
    'assets/images/league/laliga_83x83.png',
    'assets/images/league/c1_83x83.png',
    'assets/images/league/duc_83x83.png'
  ];

  ngAfterViewInit() {
    // Ensure sliders are initialized before syncing
    setTimeout(() => {
      if (this.thumbSlider && this.listSlider) {
        console.log('Sliders initialized');
      }
    }, 0);
  }

  slideChanged(event: any) {
    // Sync thumb slider with list slider
    if (this.thumbSlider) {
      this.thumbSlider.slickGoTo(event.currentSlide);
    }
    console.log('Slide changed:', event);
  }

  click(i: number) {
    console.log(i)
    this.listSlider.slickGoTo(i + 1);
  }

  prevSlide() {
    this.listSlider.slickPrev();
  }

  nextSlide() {
    this.listSlider.slickNext();
  }
}
