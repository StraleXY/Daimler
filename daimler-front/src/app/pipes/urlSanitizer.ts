import { Pipe, PipeTransform } from "@angular/core";
import { SafeUrl } from "@angular/platform-browser";

@Pipe({name: 'url-sanitizer'})
export class urlSanitizer implements PipeTransform {

    transform(value: string) : SafeUrl {
        return './assets/img_avatar.png';
    }

}