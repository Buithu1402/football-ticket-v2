import { Component } from '@angular/core';
import {NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-banner',
  imports: [
    NgOptimizedImage
  ],
  templateUrl: './banner.component.html',
  standalone: true,
  styleUrl: './banner.component.scss'
})
export class BannerComponent {

}
