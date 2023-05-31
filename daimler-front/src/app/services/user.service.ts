import { HttpClient } from '@angular/common/http';
import { EventEmitter, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CreateMessageDTO, InboxDTO, MessageDTO, MessagesDTO, Note, NotesDTO, UsersDTO, UserStatsDTO } from '../entities/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  getUsers(page: number, size: number) : Observable<UsersDTO> {
    return this.http.get<UsersDTO>(environment.apiHost + "user?page=" + page + "&size=" + size);
  }

  //Blocking
  blockUser(id: number) : Observable<void> {
    return this.http.put<void>(environment.apiHost + "user/" + id + "/block", "");
  }
  unblockUser(id: number) : Observable<void> {
    return this.http.put<void>(environment.apiHost + "user/" + id + "/unblock", "");
  }

  // Notes
  ShowNotesEvent : EventEmitter<number> = new EventEmitter();
  
  showNotes(id: number) {
    this.ShowNotesEvent.emit(id);
  }
  getNotes(id: number, page: number, size: number) : Observable<NotesDTO> {
    return this.http.get<NotesDTO>(environment.apiHost + "user/" + id + "/note?page=" + page + "&size=" + size);
  }
  insertNote(id: number, message: string) : Observable<Note> {
    return this.http.post<Note>(environment.apiHost + "user/" + id + "/note", {message: message});
  }

  // Stats
  getStats(id: number, from: number, to: number) : Observable<UserStatsDTO> {
    return this.http.get<UserStatsDTO>(environment.apiHost + "user/stats/" + id + "/" + from + "/" + to);
  }

  // Messages
  ShowChatEvent : EventEmitter<InboxDTO> = new EventEmitter();
  MsgSentEvent : EventEmitter<MessageDTO> = new EventEmitter();

  getInbox(id: number) : Observable<InboxDTO[]> {
    return this.http.get<InboxDTO[]>(environment.apiHost + "user/inbox/" + id);
  }
  getMessages(from: number, to: number, type: string, rideId: number) : Observable<MessagesDTO> {
    return this.http.get<MessagesDTO>(environment.apiHost + "user/message/" + from + "/" + to + "/" + type + "/" + rideId);
  }
  sendMessage(from: number, message : CreateMessageDTO) : Observable<MessageDTO> {
    return this.http.post<MessageDTO>(environment.apiHost + "user/" + from + "/message", message);
  }
  showChat(inbox: InboxDTO) {
    this.ShowChatEvent.emit(inbox);
  }
  msgSent(msg: MessageDTO) {
    this.MsgSentEvent.emit(msg);
  }
}