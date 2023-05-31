import { Component, Input } from '@angular/core';
import { UserFull } from 'src/app/entities/user';

@Component({
  selector: 'app-users-preview-card',
  templateUrl: './users-preview-card.component.html',
  styleUrls: ['./users-preview-card.component.css']
})
export class UsersPreviewCardComponent {
  
  @Input() Title : string = "USERS";
  @Input() Users : UserFull[] = [];

}
