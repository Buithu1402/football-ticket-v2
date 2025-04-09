import {Component, input} from '@angular/core';
import {BreadCrumb} from '../../../../share/model/BreadCrumb';

@Component({
  selector: 'app-super-header',
  imports: [],
  templateUrl: './super-header.component.html',
  standalone: true,
  styleUrls: ['./super-header.component.scss']
})
export class SuperHeaderComponent {
  title = input('Super Header');
  breadCrumb = input<BreadCrumb[]>([]);
}
