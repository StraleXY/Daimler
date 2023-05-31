package com.tim1.daimlerback.controllers;

import com.tim1.daimlerback.dtos.passenger.PassengerShortDTO;
import com.tim1.daimlerback.dtos.review.CombinedReviewsDTO;
import com.tim1.daimlerback.dtos.review.RatingDTO;
import com.tim1.daimlerback.dtos.review.ReviewDTO;
import com.tim1.daimlerback.dtos.review.ReviewsDTO;
import com.tim1.daimlerback.entities.DriverReview;
import com.tim1.daimlerback.entities.User;
import com.tim1.daimlerback.entities.VehicleReview;
import com.tim1.daimlerback.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(value = "api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping(value = "{rideId}/vehicle", consumes = "application/json")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_PASSENGER')")
    public ResponseEntity<ReviewDTO> reviewRideVehicle(@PathVariable Integer rideId, @RequestBody RatingDTO ratingDto, @AuthenticationPrincipal User user) {
        ReviewDTO ret = new ReviewDTO(reviewService.postVehicleReview(rideId, ratingDto, user.getId()));
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping(value = "vehicle/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewsDTO> getVehicleReviews(@PathVariable Integer id) {
        ReviewsDTO ret = reviewService.getVehicleReviews(id);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @PostMapping(value = "{rideId}/driver", consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_PASSENGER')")
    public ResponseEntity<ReviewDTO> reviewRideDriver(@PathVariable Integer rideId, @RequestBody RatingDTO ratingDto, @AuthenticationPrincipal User user) {
        ReviewDTO ret = new ReviewDTO(reviewService.postDriverReview(rideId, user.getId(), ratingDto));
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping(value = "driver/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewsDTO> getDriverReviews(@PathVariable Integer id) {
        ReviewsDTO ret = reviewService.getDriverReviews(id);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping(value = "{rideId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CombinedReviewsDTO>> getRideReviews(@PathVariable Integer rideId) {
        return new ResponseEntity<>(reviewService.getReviewsByRideId(rideId), HttpStatus.OK);
    }
}
