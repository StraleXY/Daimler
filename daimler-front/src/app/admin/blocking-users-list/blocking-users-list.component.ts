import { Component } from '@angular/core';
import { UsersDTO, UserWithRole } from 'src/app/entities/user';
import { User } from 'src/app/register/register.component';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-blocking-users-list',
  templateUrl: './blocking-users-list.component.html',
  styleUrls: ['./blocking-users-list.component.css']
})
export class BlockingUsersListComponent {

  page : number = 1;
  itemsPerPage : number = 4;

  constructor(private userService : UserService) {
    this.sendPageRequest(this.page);
  }

  Users : UsersDTO = {
    totalCount: 0,
    results: []
  };

  public nextClick() {
    this.sendPageRequest(this.page + 1);
  }

  public previousClick() {
    if(this.page == 1) return;
    this.sendPageRequest(this.page - 1);
  }

  private sendPageRequest(page : number) {
    this.userService.getUsers(page - 1, this.itemsPerPage).subscribe(res => {
      if (page != 2 && res.totalCount == 0) return;
      this.Users = res;
      this.page = page;
      console.log(res);
    });
  }

  public blockClick(user : UserWithRole) {
    this.userService.blockUser(user.id).subscribe({
      next: (_) => user.blocked = true,
      error: (e) => {}
    });
  }

  public unblockClick(user : UserWithRole) {
    this.userService.unblockUser(user.id).subscribe({
      next: (_) => user.blocked = false,
      error: (e) => {}
    });
  }

  public showNotesClick(user : UserWithRole) {
    this.userService.showNotes(user.id);
  }
}
