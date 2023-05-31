import { Pipe, PipeTransform, SecurityContext } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Pipe({
  name: 'urlSentizer'
})
export class UrlSentizerPipe implements PipeTransform {

  constructor(private santizier : DomSanitizer) {}
  
  transform(value: string): SafeUrl {
    if (value == "") return "./assets/img_avatar.png";
    else {
      let url = this.santizier.sanitize(SecurityContext.HTML, this.santizier.bypassSecurityTrustHtml('data:image/jpg;base64,' + value));
      if (url == null) return "./assets/img_avatar.png";
      else return url;
    }
  }

}
