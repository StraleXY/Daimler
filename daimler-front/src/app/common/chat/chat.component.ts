import { Component, ElementRef, EventEmitter, Input, ViewChild } from '@angular/core';
import { InboxDTO, MessageDTO, MessagesDTO, SimpleUserDTO } from 'src/app/entities/user';
import { WebsocketService } from 'src/app/services/websocket.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent {

  userId: number = 0;
  messages : MessagesDTO = {
    totalCount: 0,
    results: []
  }
  chatTitle : string = "";
  ongoingMessage : string = "";
  inbox : any;

  constructor(private userService: UserService) {
    this.userId = Number.parseInt(localStorage.getItem('userId')!);
    userService.ShowChatEvent.subscribe(inbox => {
      userService.getMessages(inbox.lastMessage.senderId, inbox.lastMessage.receiverId, inbox.lastMessage.type, inbox.lastMessage.rideId).subscribe(messages => {
        console.log(messages);
        this.messages = messages;
        this.chatTitle = "[" + inbox.with.name + " " + inbox.with.surname + "]";
        this.inbox = inbox;
      });
    });
  
  }

  sendMessageClick(message : string) {
    console.log(message);
    if (this.inbox.with.id == -1) return;
    let newMsg = {
      receiverId: this.inbox.with.id,
      message: message,
      type: this.inbox.lastMessage.type,
      rideId: this.inbox.lastMessage.rideId,
      timestamp: Date.now()
    }
    this.userService.sendMessage(this.userId, newMsg).subscribe(msg => {
      this.messages.results.push(msg);
      this.messages.totalCount++;
      this.ongoingMessage = "";
      this.userService.msgSent(msg);
    });
  }

}
