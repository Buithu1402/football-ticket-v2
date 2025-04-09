import {Component} from '@angular/core';
import {CopyRighterComponent} from './copy-righter/copy-righter.component';
import {FooterWidgetComponent} from './footer-widget/footer-widget.component';
import {LIB} from '../../../share/constant';

@Component({
  selector: 'app-footer',
  imports: [
    CopyRighterComponent,
    FooterWidgetComponent
  ],
  templateUrl: './footer.component.html',
  standalone: true,
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent {

}
