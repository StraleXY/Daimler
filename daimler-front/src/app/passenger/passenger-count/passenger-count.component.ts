import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-passenger-count',
  templateUrl: './passenger-count.component.html',
  styleUrls: ['./passenger-count.component.css']
})
export class PassengerCountComponent {
  
  @Output() onSoloClicked: EventEmitter<void> = new EventEmitter();
  @Output() onGroupClicked: EventEmitter<void> = new EventEmitter();

  soloClick() {
    this.onSoloClicked.emit();
  }

  groupClick() {
    this.onGroupClicked.emit();
  }
}
