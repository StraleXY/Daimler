import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-pick-profile-pic',
  templateUrl: './pick-profile-pic.component.html',
  styleUrls: ['./pick-profile-pic.component.css']
})
export class PickProfilePicComponent {

  constructor(private santizier : DomSanitizer) {}

  @Input() hasTitle = false;

  private _profilePic: string = "";
  @Input() set profilePic(value: string) {
    this._profilePic = value;
    if(value == "") this.selectedProfilePicUrl = "./assets/img_avatar.png";
    else this.selectedProfilePicUrl = this.santizier.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + this._profilePic);
  }
  @Output() pickedPicture = new EventEmitter<string>();
  
  selectedProfilePic: any = null;
  selectedProfilePicUrl : SafeUrl = "./assets/img_avatar.png";

  onFileSelected(event: any): void {
      this.selectedProfilePic = event.target.files[0] ?? null;

      var reader = new FileReader();
      reader.onload = this._handleReaderLoaded.bind(this);
      reader.readAsBinaryString(this.selectedProfilePic);

      this.selectedProfilePicUrl = this.santizier.bypassSecurityTrustUrl(window.URL.createObjectURL(this.selectedProfilePic));
  }

  _handleReaderLoaded(readerEvt : any) {
    var binaryString = readerEvt.target.result;
    this.pickedPicture.emit(btoa(binaryString));
  }
}
