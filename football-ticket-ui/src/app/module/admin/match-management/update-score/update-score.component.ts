import {Component, EventEmitter, OnInit, Output, signal} from '@angular/core';
import {BsDatepickerDirective, BsDatepickerInputDirective} from 'ngx-bootstrap/datepicker';
import {NgSelectComponent} from '@ng-select/ng-select';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgClass} from '@angular/common';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {ResponseData} from '../../../../share/model/response-data.model';
import {BsModalRef} from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-update-score',
  imports: [
    BsDatepickerDirective,
    BsDatepickerInputDirective,
    NgSelectComponent,
    ReactiveFormsModule,
    FormsModule,
    NgClass
  ],
  templateUrl: './update-score.component.html',
  standalone: true,
  styleUrl: './update-score.component.scss'
})
export class UpdateScoreComponent implements OnInit {
  matchId = signal(0);
  param: ParamUpdateScore = new ParamUpdateScore();
  @Output() eventOut = new EventEmitter();

  constructor(protected http: HttpClient, protected toast: ToastrService, protected bsRef: BsModalRef) {
  }

  ngOnInit(): void {
    this.http.get<ResponseData<ParamUpdateScore>>(`api/match/${this.matchId()}/score`)
      .subscribe(res => {
        if (res.success) {
          this.param = res.data;
        } else {
          this.toast.error(res.message);
        }
      });
  }

  update() {
    this.param.matchId = this.matchId();
    this.http.post<ResponseData<any>>(`/api/match/score`, this.param)
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Update score success');
          this.eventOut.emit();
        } else {
          this.toast.error(res.message);
        }
        this.bsRef.hide();
      });
  }
}


export class ParamUpdateScore {
  constructor(
    public matchId: number = 0,
    public homeScore: number = 0,
    public awayScore: number = 0,
    public status: string = ''
  ) {
  }
}
