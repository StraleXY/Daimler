import { trigger, style, animate, transition } from '@angular/animations';
import { Component, EventEmitter, ViewChild } from '@angular/core';
import { PanicRideDTO } from 'src/app/entities/user';
import {Howl, Howler} from 'howler';
import { WebsocketMessage, WebsocketService } from 'src/app/services/websocket.service';
import { UserPanicNotificationComponent } from '../user-panic-notification/user-panic-notification.component';

@Component({
  selector: 'app-admin-home',
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css'],
  animations: [
    trigger("inOutPaneAnimation", [
      transition(":enter", [
        style({ opacity: 0, transform: "translateY(35%)" }),
        animate(
          "150ms ease-in",
          style({ opacity: 1, transform: "translateY(0)" })
        )
      ]),
      transition(":leave", [
        style({ opacity: 1, transform: "translateX(0)" }),
        animate(
          "250ms ease-out",
          style({ opacity: 0, transform: "translateX(100%)" })
        )
      ])
    ])
  ]
})
export class AdminHomeComponent {

  panicVisibility = false;
  animationLength = 350;
  websocketService: WebsocketService;
  @ViewChild("panicCard") panicCard! : UserPanicNotificationComponent;
  private eventCallback: EventEmitter<any> = new EventEmitter<any>();
  
  constructor() {
    
    this.websocketService = new WebsocketService();
    this.websocketService.eventCallback = this.eventCallback;
    this.websocketService._connect();
    this.eventCallback.subscribe((res: any) => {
      this.onMessageReceived(res);
    });
  }

  onMessageReceived(message: any) {
    let msg: WebsocketMessage = JSON.parse(message);
    if(msg.topic == 'panic') {
      let panicDTO: PanicRideDTO = JSON.parse(msg.body);
      var sound = new Howl({
        src: ['../../../../assets/panicNotification.wav']
      });
      sound.play();
      setTimeout(() => {
        this.panicVisibility = true;
        this.panicDTO = panicDTO;
      }, this.animationLength);
    }
  }

  panicDTO: PanicRideDTO = {
    id: 0,
    user: {
      id: 0,
      name: '',
      surname: '',
      profilePicture: '',
      telephoneNumber: '',
      email: '',
      address: ''
    },
    ride: {
      id: 0,
      startTime: '',
      endTime: '',
      totalCost: 0,
      driver: {
        id: 0,
        email: '',
        name: '',
        surname: '',
        profilePicture: ''
      },
      passengers: [],
      estimatedTimeInMinutes: 0,
      vehicleType: '',
      petTransport: false,
      babyTransport: false,
      rejection: {
        reason : '',
        timeOfRejection : ''
      },
      locations: [],
      status: '',
      distance: 0,
      scheduledTimestamp: 0
    },
    time: '',
    reason: '',
    vehicleId: 0  
  };

}