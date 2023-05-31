import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Admin } from '../../entities/admin';

@Component({
  selector: 'app-admin-basic-info-card',
  templateUrl: './admin-basic-info-card.component.html',
  styleUrls: ['./admin-basic-info-card.component.css']
})
export class AdminBasicInfoCardComponent {
  
  @Output() editInfo = new EventEmitter<boolean>();
  @Input() basicInfoDTO : Admin = {
    id: 0,
    name: '',
    surname: '',
    email: ''
  }

  toggleEditInfo() {
    this.editInfo.emit(true);
  }
}
