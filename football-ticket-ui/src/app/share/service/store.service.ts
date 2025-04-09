import {Injectable, signal} from '@angular/core';
import {BreadCrumb} from '../model/BreadCrumb';

@Injectable({
  providedIn: 'root'
})
export class StoreService {
  title = signal('');
  breadCrumb = signal<BreadCrumb[]>([]);

  constructor() {
  }

  setTitle(title: string) {
    this.title.set(title);
  }

  setBreadCrumb(breadCrumb: BreadCrumb[]) {
    this.breadCrumb.set([...breadCrumb]);
  }

  clear() {
    this.title.set('');
    this.breadCrumb.set([]);
  }
}
