import { Component, Input } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { UserStatsDTO } from 'src/app/entities/user';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-stats-preview',
  templateUrl: './stats-preview.component.html',
  styleUrls: ['./stats-preview.component.css']
})
export class StatsPreviewComponent {

  @Input() userId : number = 6;
  @Input() isUserSelected : boolean = false;

  constructor(private userService : UserService) { }

  range = new FormGroup({
    start: new FormControl<Date | null>(null),
    end: new FormControl<Date | null>(null),
  });

  stats: UserStatsDTO = {
    amount: 0,
    totalRides: 0,
    totalDistance: 0,
    ridesPerDay: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    distancePerDay: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
  }
  
  amountAvg: string = '0';
  ridesAvg: string = '0';
  distanceAvg: string = '0';

  ridesMax: number = 0;
  distanceMax: number = 0;

  public pickerClosed() {
    this.refresh();
  }

  onFieldFocus() {
    this.refresh();
  }

  private refresh() {
    if(!this.range.valid) return;
    let days = (this.range.value.end?.getTime() == undefined ? 0 : this.range.value.end?.getTime() -
      (this.range.value.start?.getTime() == undefined ? 0 : this.range.value.start?.getTime())) / 86400000;
    this.userService.getStats(this.userId, 
      this.range.value.start?.getTime() == undefined ? 0 : this.range.value.start?.getTime(), 
      this.range.value.end?.getTime() == undefined ? 0 : this.range.value.end?.getTime()).subscribe(stats => {
        console.log(stats);
        this.stats = stats;
        this.amountAvg = (stats.amount/days).toFixed(1);
        this.ridesAvg = (stats.totalRides/days).toFixed(1);
        this.distanceAvg = (stats.totalDistance/days).toFixed(1);
        this.ridesMax = Math.max(...stats.ridesPerDay);
        this.distanceMax = Math.max(...stats.distancePerDay);
    });
  }

}
