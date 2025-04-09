import {Injectable} from '@angular/core';
import {DatePipe} from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class DateService {

  constructor() {
  }

  getFormatDate(value: Date | null, formatString: string = 'yyyy-MM-dd hh:mm:ss'): string | null {
    return !value
      ? null
      : new DatePipe('en_US').transform(value, formatString) || null;
  }
}
