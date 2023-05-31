import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatSelectChange } from '@angular/material/select';
import { LocationDTO, UserRidesDTO, DepartureDestinationDTO, RideDTO } from 'src/app/entities/rides';

interface SortBys {
  value: string;
  viewValue: string;
}

export interface PageReguestArgs {
  id: number;
  page: number;
  itemsPerPage: number;
  sortBy: string; 
}

@Component({
  selector: 'app-route-history-card',
  templateUrl: './route-history-card.component.html',
  styleUrls: ['./route-history-card.component.css']
})
export class RouteHistoryCardComponent implements OnInit {

  ngOnInit(): void {
    this.sendPageRequest(this.page);
  }

  @Input() userId : number = Number.parseInt(localStorage.getItem('userId') ?? '');

  @Input() set setPage(value : {page : number, rides : UserRidesDTO}) {
    if (value.page != 1 && value.rides.totalCount == 0) return;
    this.page = value.page;
    this.rides = value.rides;
    this.lastItemId = this.rides.results[this.rides.totalCount - 1].id
    this.noData = this.rides.totalCount == 0;
  }

  public clearForm() {
    this.rides = {
      totalCount : 0,
      results : []
    }
  }
  
  @Output() onPageRequested = new EventEmitter<PageReguestArgs>();
  @Output() onDetailsRequested = new EventEmitter<RideDTO>();

  noData : boolean = false;
  lastItemId : number = -1;

  page : number = 1;
  itemsPerPage : number = 3;
  sortBy : string = 'id';

  sortOptions: SortBys[] = [
    {value: 'id', viewValue: 'Default'},
    {value: 'totalCost', viewValue: 'Price'},
    {value: 'startTime', viewValue: 'Date'},
    {value: 'estimatedTimeInMinutes', viewValue: 'Duration'}
  ];

  rides : UserRidesDTO = {
    totalCount : 0,
    results : []
  }

  private sendPageRequest(page : number) {
    this.onPageRequested.emit({
      id: this.userId,
      page: page,
      itemsPerPage: this.itemsPerPage,
      sortBy: this.sortBy
    });
  }

  sortSelectionChange(event : MatSelectChange) {
    this.sortBy = event.value;
    this.sendPageRequest(this.page);
  }

  public nextClick() {
    this.sendPageRequest(this.page + 1);
  }

  public previousClick() {
    if(this.page == 1) return;
    this.sendPageRequest(this.page - 1);
  }

  public getLocationList(locations : DepartureDestinationDTO[]) : LocationDTO[] {
    let loc : LocationDTO[] = [];
    for(let i = 0; i < locations.length; i++) {
        loc.push(locations[i].departure);
        if(i == locations.length - 1) loc.push(locations[i].destination);
    }
    return loc;
  }

  public detailsClick(ride : RideDTO) {
    this.onDetailsRequested.emit(ride);
  }
}