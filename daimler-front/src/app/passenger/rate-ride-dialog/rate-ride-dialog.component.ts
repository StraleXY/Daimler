import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ReviewDialogData } from 'src/app/common/detailed-ride-preview/detailed-ride-preview.component';
import { CombinedReviewsDTO } from 'src/app/entities/reviews';
import { DriverService } from 'src/app/services/driver.service';
import { ReviewService } from 'src/app/services/review.service';

@Component({
  selector: 'app-rate-ride-dialog',
  templateUrl: './rate-ride-dialog.component.html',
  styleUrls: ['./rate-ride-dialog.component.css']
})
export class RateRideDialogComponent {

  constructor(private dialogRef: MatDialogRef<RateRideDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: ReviewDialogData, private driverService : DriverService, private reviewService : ReviewService) {}

  form: FormGroup = new FormGroup({
    driverRating: new FormControl('', [Validators.required, Validators.pattern("[1-5]")]),
    driverComment: new FormControl('', [Validators.required]),
    vehicleRating: new FormControl('', [Validators.required, Validators.pattern("[1-5]")]),
    vehicleComment: new FormControl('', [Validators.required]),
  });
  
  onNoClick() {
    this.dialogRef.close();
  }

  onSaveClick() {
    let result = this.form.value;
    
    // Post Vehicle Review
    this.reviewService.postVehicleRating(this.data.rideId, {
      rating: result.vehicleRating,
      comment: result.vehicleComment,
      passengerId: this.data.passengerId
    }).subscribe(vehicleRes => {
      
      // Post Driver Review
      this.reviewService.postDriverRating(this.data.rideId, {
        rating: result.driverRating,
        comment: result.driverComment,
        passengerId: this.data.passengerId
      }).subscribe(driverRes => {
        
        let review: CombinedReviewsDTO = {
          vehicleReview: vehicleRes,
          driverReview: driverRes
        }

        this.dialogRef.close(review);
      })
    })
  }
}
