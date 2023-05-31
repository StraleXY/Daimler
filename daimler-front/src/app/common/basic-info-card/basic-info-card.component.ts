import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { UserFull } from 'src/app/entities/user';

@Component({
  selector: 'app-basic-info-card',
  templateUrl: './basic-info-card.component.html',
  styleUrls: ['./basic-info-card.component.css']
})
export class BasicInfoCardComponent {

  constructor(private santizier : DomSanitizer) {}
  
  @Output() editInfo = new EventEmitter<boolean>();
  @Input() basicInfoDTO : UserFull = {
    name: '',
    surname: '',
    email: '',
    telephoneNumber: '',
    address: '',
    id: 0,
    profilePicture: ''
  };

  selectedProfilePic: any = null;
  ngOnChanges(changes: SimpleChanges) {
    let base64 = changes['basicInfoDTO'].currentValue['profilePicture'];
    if(base64 == "") this.selectedProfilePic = "./assets/img_avatar.png"
    else this.selectedProfilePic = this.santizier.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + base64);
  }
  
  toggleEditInfo() {
    this.editInfo.emit(true);
  }
}
