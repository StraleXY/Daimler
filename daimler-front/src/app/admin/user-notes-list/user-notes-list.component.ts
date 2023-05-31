import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { MatInput } from '@angular/material/input';
import { Note, NotesDTO } from 'src/app/entities/user';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-user-notes-list',
  templateUrl: './user-notes-list.component.html',
  styleUrls: ['./user-notes-list.component.css']
})
export class UserNotesListComponent implements OnInit {

  noData : boolean = true;
  page : number = 1;
  itemsPerPage : number = 4;
  userId : number = -1;

  constructor(private userService: UserService) {}

  noteMessageText: string = '';

  ngOnInit(): void {
    this.userService.ShowNotesEvent.subscribe(id => {
      this.page = 1;
      this.userId = id;
      this.noteMessageText = "";
      this.Notes = {
        totalCount: 0,
        results: []
      }
      this.noData = true;
      this.sendPageRequest(this.page);
    })
  }

  Notes: NotesDTO = {
    totalCount: 0,
    results: []
  }

  public nextClick() {
    this.sendPageRequest(this.page + 1);
  }

  public previousClick() {
    if(this.page == 1) return;
    this.sendPageRequest(this.page - 1);
  }

  private sendPageRequest(page: number) {
    this.userService.getNotes(this.userId, page - 1, this.itemsPerPage).subscribe(res => {
      if (page != 1 && res.totalCount == 0) return;
      if (res.totalCount != 0) this.noData = false;
      this.Notes = res;
      this.page = page;
      console.log(res);
    });
  }

  public addNoteClick(message: string) {
    this.userService.insertNote(this.userId, message).subscribe(res => {
      this.noteMessageText = "";
      this.sendPageRequest(this.page);
    })
  }
}
