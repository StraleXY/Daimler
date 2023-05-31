import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-driver-reject',
  templateUrl: './driver-reject.component.html',
  styleUrls: ['./driver-reject.component.css']
})
export class DriverRejectComponent {
  rejectionReasonText: string = '';

  @Output() cancel: EventEmitter<boolean> = new EventEmitter<boolean>;
  @Output() reject: EventEmitter<boolean> = new EventEmitter<boolean>;
  @Output() rejectionText: EventEmitter<string> = new EventEmitter<string>;

  cancelClick() {
      this.cancel.emit(true);
  }

  rejectClick() {
      this.reject.emit(true);
      this.rejectionText.emit(this.rejectionReasonText);
  }
}
