import {Component, input} from '@angular/core';
import {Match} from '../../../../share/model/Match';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {PagingData} from '../../../../share/model/response-data.model';

@Component({
  selector: 'app-grid-fixture',
  imports: [
    PaginationComponent,
    FormsModule
  ],
  templateUrl: './grid-fixture.component.html',
  standalone: true,
  styleUrl: './grid-fixture.component.scss'
})
export class GridFixtureComponent {
  matches = input<PagingData<Match>>(new PagingData<Match>());

  constructor(protected router: Router) {
  }

  onPageChange(event: any): void {
    this.matches().page = event.page;
  }

  navigateToDetail(item: Match) {
    this.router.navigate(['/fixture-detail'], {
      queryParams: {
        mid: item.matchId
      }
    }).then();
  }
}
