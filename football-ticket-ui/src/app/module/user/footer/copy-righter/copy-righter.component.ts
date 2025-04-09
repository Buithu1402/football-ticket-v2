import {Component} from '@angular/core';

@Component({
  selector: 'app-copy-righter',
  imports: [],
  templateUrl: './copy-righter.component.html',
  standalone: true,
  styleUrl: './copy-righter.component.scss'
})
export class CopyRighterComponent {
  currentYear = new Date().getFullYear();
}
