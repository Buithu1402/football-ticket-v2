import {Component, EventEmitter, OnInit, Output, signal} from '@angular/core';
import {TransferFileService} from '../../../../share/service/transfer-file.service';
import {League} from '../../../../share/model/League';
import {FormsModule} from '@angular/forms';
import {ResponseData} from '../../../../share/model/response-data.model';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {BsModalRef} from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-league-upsert',
  imports: [
    FormsModule
  ],
  templateUrl: './league-upsert.component.html',
  standalone: true,
  styleUrl: './league-upsert.component.scss',
  providers: [TransferFileService]
})
export class LeagueUpsertComponent implements OnInit {
  title = signal('Tạo mới giải đấu');
  league = signal(new League());
  @Output() eventOut = new EventEmitter<boolean>();
  isUpdate = signal(false);

  constructor(
    protected fileService: TransferFileService,
    protected http: HttpClient,
    protected toast: ToastrService,
    protected bsRef: BsModalRef
  ) {
  }

  ngOnInit(): void {
  }

  clearUpload() {
    this.fileService.reset();
  }

  addLeague() {
    if (this.league().name === '') {
      this.toast.error('Tên giải đấu không được để trống');
      return;
    }
    const formData = new FormData();
    formData.append('name', this.league().name);
    if (this.fileService.fileData) {
      formData.append('logo', this.fileService.fileData);
    }
    if (this.league().leagueId > 0) {
      formData.append('leagueId', this.league().leagueId.toString());
    }
    this.http.post<ResponseData<string>>('api/league', formData)
      .subscribe(res => {
        if (res.success) {
          this.toast.success(this.isUpdate() ? 'Thành công' : 'Tạo mới thành công');
          if(this.isUpdate()) {
            this.bsRef.hide();
            this.eventOut.emit(true);
          } else {
            this.league = signal(new League());
            this.clearUpload();
          }
        } else {
          this.toast.error(res.message);
        }
      });
  }
}
