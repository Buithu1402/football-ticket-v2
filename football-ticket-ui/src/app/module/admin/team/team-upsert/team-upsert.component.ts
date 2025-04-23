import {Component, EventEmitter, OnInit, Output, signal} from '@angular/core';
import {TransferFileService} from '../../../../share/service/transfer-file.service';
import {Team} from '../../../../share/model/Team';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {FormsModule} from '@angular/forms';
import {ResponseData} from '../../../../share/model/response-data.model';

@Component({
  selector: 'app-team-upsert',
  imports: [
    FormsModule,
  ],
  templateUrl: './team-upsert.component.html',
  standalone: true,
  styleUrl: './team-upsert.component.scss',
  providers: [TransferFileService]
})
export class TeamUpsertComponent implements OnInit {
  title = signal('Create Team');
  team = signal(new Team());
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
    if (this.isUpdate()) {
      this.title.set('Cập nhật đội bóng');
    }
  }

  clearUpload() {
    this.fileService.reset();
  }

  add() {
    let valid = true;
    Object.entries(this.team()).forEach(([key, value]) => {
      if (['name', 'stadiumName', 'address', 'establishedYear'].includes(key) && !value) {
        this.toast.error(`${key} không được để trống`);
        valid = false;
      }
    });
    if (valid) {
      const formData = new FormData();
      formData.append('data', new Blob([JSON.stringify(this.team())], {type: 'application/json'}));
      if (this.fileService.fileData) {
        formData.append('logo', this.fileService.fileData);
      }
      this.http.post<ResponseData<string>>('api/team/upsert', formData)
        .subscribe(res => {
          if (res.success) {
            this.toast.success(this.isUpdate()
              ? 'Cập nhật đội bóng thành công'
              : 'Thêm đội bóng thành công');
            this.bsRef.hide();
            this.team.set(new Team());
            this.clearUpload();
            this.eventOut.emit(true);
          } else {
            this.toast.error(res.message);
          }
        });
    }
  }
}
