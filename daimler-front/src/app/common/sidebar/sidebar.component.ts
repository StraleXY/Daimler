import { Component, Input, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from 'src/app/services/login.service';
var L = require('leaflet')

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {

  constructor(private router : Router, private loginService : LoginService) {}

  home_opacity: string = "0.45"
  inbox_opacity: string = "0.45"
  history_opacity: string = "0.4"
  account_opacity: string = "0.45"
  block_opacity: string = "0.45"

  _selected : Navigation = Navigation.home;

  @Input() set selected(value: Navigation) {
    this._selected = value;
    switch(this._selected){
      case Navigation.home:
        this.home_opacity = "1";
        break;
      case Navigation.inbox:
        this.inbox_opacity = "1";
        break;
      case Navigation.history:
        this.history_opacity = "1";
        break;
      case Navigation.account:
        this.account_opacity = "1";
        break;
      case Navigation.block:
        this.block_opacity = "1";
        break;
    }
  }

  @Input() apiPath : String = "";
  @Input() isAdmin : boolean = false;

  navigationClick(destination : string) {
    console.log(destination);
    if (destination == 'exit') this.loginService.logout()
    else this.router.navigateByUrl('/' + this.apiPath + '/' + destination);
  }
}

export enum Navigation {
  home = 1,
  inbox,
  history,
  account,
  block
}
