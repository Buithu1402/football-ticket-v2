import {Component, EventEmitter, Output} from '@angular/core';
import {NgClass} from '@angular/common';
import {BsModalRef} from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-payment-method',
  imports: [
    NgClass
  ],
  templateUrl: './payment-method.component.html',
  standalone: true,
  styleUrl: './payment-method.component.scss'
})
export class PaymentMethodComponent {
  @Output() eventOut = new EventEmitter<string>();
  currentType = 'vnpay';

  constructor(protected bsRef: BsModalRef) {
  }

  click() {
    this.eventOut.emit(this.currentType);
    this.bsRef.hide();
  }
}
