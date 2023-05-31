import { Component, EventEmitter, Input } from '@angular/core';
import { InboxDTO } from 'src/app/entities/user';
import { WebsocketService } from 'src/app/services/websocket.service';
import { UserService } from 'src/app/services/user.service';
import { Howl } from 'howler';

@Component({
  selector: 'app-inbox',
  templateUrl: './inbox.component.html',
  styleUrls: ['./inbox.component.css']
})
export class InboxComponent {

  userId: number = 0;
  noData : boolean = false;
  inboxes : InboxDTO[] = [];
  showingChatWith: number = -1;
  @Input() showForRide: number = -1;
  
  private websocketService : WebsocketService;
  private eventCallback: EventEmitter<any> = new EventEmitter<any>();

  constructor(private userService: UserService) {
    this.userId = Number.parseInt(localStorage.getItem('userId')!);
    userService.getInbox(this.userId).subscribe(inboxes => {
      this.inboxes = inboxes;
      this.noData = inboxes.length == 0;
      this.showChatClick(this.inboxes.filter(i => i.lastMessage.rideId == this.showForRide)[0]);
    });

    userService.MsgSentEvent.subscribe(msg => {
      let inbox = this.inboxes.filter(x => x.with.id == (msg.senderId == this.userId ? msg.receiverId : msg.senderId))[0];
      inbox.lastMessage = msg;
    });

    this.websocketService = new WebsocketService();
    this.websocketService.eventCallback = this.eventCallback;
    this.websocketService.connectToMessagingSocket(this.userId);
    this.eventCallback.subscribe((res: any) => {
      this.onMessageReceived(res);
    });
  }

  showChatClick(inbox : InboxDTO) {
    console.log(inbox);
    this.showingChatWith = inbox.with.id;
    this.userService.showChat(inbox);
  }

  onMessageReceived(message : string) {
    let msg = JSON.parse(message);
    let inbox = this.inboxes.filter(x => x.with.id == (msg.senderId == this.userId ? msg.receiverId : msg.senderId))[0];
    inbox.lastMessage = msg;
    if (inbox.with.id == this.showingChatWith) this.userService.showChat(inbox);
  }

}