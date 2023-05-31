import { EventEmitter, Injectable } from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
    webSocketEndPoint: string = 'http://localhost:8080/ws';
    stompClient: any;
    stompEndpoint: String = "ws"
    eventCallback: EventEmitter<any> = new EventEmitter<any>();

    constructor() { }

    _connect() {
        console.log("Initialize WebSocket Connection");
        let ws = new SockJS(this.webSocketEndPoint);
        this.stompClient = Stomp.over(ws);
        this.stompClient.connect({}, () => {
            this.connectToTopic();
        }, this.errorCallBack);
    };

    connectToMessagingSocket(userId : number) {
        console.log("Initialize WebSocket Connection");
        let ws = new SockJS(this.webSocketEndPoint);
        this.stompClient = Stomp.over(ws);
        this.stompClient.connect({}, () => {
            this.connectToSpecificTopic('queue/message/' + userId);
        }, this.errorCallBack);
    };

    _disconnect() {
        if (this.stompClient !== null) {
            this.stompClient.disconnect();
        }
        console.log("Disconnected");
    }

    // on error, schedule a reconnection attempt
    errorCallBack(error: any) {
        console.log("errorCallBack -> " + error)
        this._connect();
    }

    connectToTopic() {
        if (localStorage.getItem('userRole') == "ROLE_PASSENGER") {
            let passengerId = localStorage.getItem('userId');
            this.connectToSpecificTopic('queue/passenger/' + passengerId);
        }
        else if (localStorage.getItem('userRole') == "ROLE_DRIVER") {
            let driverId = localStorage.getItem('userId');
            this.connectToSpecificTopic('queue/driver/' + driverId);
        }
        else if (localStorage.getItem('userRole') == "ROLE_ADMIN") {
            let adminId = localStorage.getItem('userId');
            this.connectToSpecificTopic('queue/admin/' + adminId);
        }

    }

    connectToSpecificTopic(path: string) {
        this.stompClient.subscribe(path, (message: any) => {
            console.log(message);
            this.eventCallback.emit(message.body);
        });
    }

//
//  /**
//   * Send message to sever via web socket
//   * @param {*} message
//   */
//     _send(message: any) {
//         //console.log("calling logout api via web socket");
//         //this.stompClient.send("/app/hello", {}, JSON.stringify(message));
//     }
//
//     onMessageReceived(message: any) {
//         console.log("Message Recieved from Server :: " + message);
//     }
}

export interface WebsocketMessage {
    topic: string;
    body: string;
}