package com.tim1.daimlerback.services;

import com.tim1.daimlerback.dtos.review.CombinedReviewsDTO;
import com.tim1.daimlerback.dtos.review.RatingDTO;
import com.tim1.daimlerback.dtos.review.ReviewDTO;
import com.tim1.daimlerback.dtos.review.ReviewsDTO;
import com.tim1.daimlerback.entities.*;
import com.tim1.daimlerback.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private IVehicleReviewRepository vehicleReviewRepository;
    @Autowired
    private IDriverReviewRepository driverReviewRepository;
    @Autowired
    private IVehicleRepository vehicleRepository;
    @Autowired
    private IPassengerRepository passengerRepository;
    @Autowired
    private IDriverRepository driverRepository;
    @Autowired
    private IRideRepository rideRepository;


    public List<VehicleReview> getRideVehicleReviews(Integer vehicleId) {
        List<VehicleReview> vehicleReviews = vehicleReviewRepository.findAllByVehicleId(vehicleId);
        if (vehicleReviews.isEmpty()) {
            String value = "Reviews not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        return vehicleReviews;
    }

    public List<DriverReview> getRideDriverReviews(Integer driverId) {
        List<DriverReview> driverReviews = driverReviewRepository.findAllByDriverId(driverId);
        if (driverReviews.isEmpty()) {
            String value = "Reviews not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        return driverReviews;
    }

    public ReviewsDTO getDriverReviews(Integer driverId) {
        System.out.println(driverId);
        ReviewsDTO reviews = new ReviewsDTO();
        List<DriverReview> driverReviews = getRideDriverReviews(driverId);
        reviews.setResults(driverReviews.stream().map(ReviewDTO::new).collect(Collectors.toList()));
        return reviews;
    }

    public ReviewsDTO getVehicleReviews(Integer vehicleId) {
        ReviewsDTO reviews = new ReviewsDTO();
        List<VehicleReview> vehicleReviews = getRideVehicleReviews(vehicleId);
        reviews.setResults(vehicleReviews.stream().map(ReviewDTO::new).collect(Collectors.toList()));
        return reviews;
    }

    public VehicleReview postVehicleReview(Integer rideId, RatingDTO rating, Integer userId) {
        VehicleReview review = new VehicleReview();

        Optional<Ride> rr = rideRepository.findById(rideId);
        if (rr.isEmpty()) {
            String value = "Ride does not exist!";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        Ride r = rr.get();
        review.setRide(r);
        Driver d = r.getDriver();

        Optional<Vehicle> vehicle = vehicleRepository.findById(d.getVehicle().getId());
        if (vehicle.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found!");
        review.setVehicle(vehicle.get());

        Optional<Passenger> passenger = passengerRepository.findById(userId);
        if (passenger.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger not found!");
        review.setPassenger(passenger.get());

        review.setComment(rating.getComment());
        review.setRating(rating.getRating());

        return save(review);
    }

    public Review postDriverReview(Integer rideId, Integer passengerId, RatingDTO rating) {
        DriverReview review = new DriverReview();

        Optional<Ride> ride = rideRepository.findById(rideId);
        if (ride.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found!");
        review.setRide(ride.get());

        Driver driver = ride.get().getDriver();
        review.setDriver(driver);

        Passenger passenger = passengerRepository.findById(passengerId).get();

        review.setComment(rating.getComment());
        review.setRating(rating.getRating());
        review.setPassenger(passenger);

        return save(review);
    }

    public ArrayList<CombinedReviewsDTO> getReviewsByRideId(Integer rideId) {
        List<DriverReview> driverReviews = driverReviewRepository.findAllByRideId(rideId);
        driverReviews.sort(Comparator.comparing(r -> r.getPassenger().getId()));
        List<VehicleReview> vehicleReviews = vehicleReviewRepository.findAllByRideId(rideId);
        vehicleReviews.sort(Comparator.comparing(r -> r.getPassenger().getId()));
        ArrayList<CombinedReviewsDTO> retList = new ArrayList<CombinedReviewsDTO>();

        int i = 0, j = 0;
        while (i < driverReviews.size() || j < vehicleReviews.size()) {
            if (i < driverReviews.size() && j < vehicleReviews.size() && driverReviews.get(i).getPassenger().getId().equals(vehicleReviews.get(j).getPassenger().getId())) {
                retList.add(new CombinedReviewsDTO(driverReviews.get(i), vehicleReviews.get(j)));
                i++;
                j++;
            } else if (i >= driverReviews.size() || (j < vehicleReviews.size() && driverReviews.get(i).getPassenger().getId().compareTo(vehicleReviews.get(j).getPassenger().getId())>0)) {
                retList.add(new CombinedReviewsDTO(null, vehicleReviews.get(j)));
                j++;
            } else {
                retList.add(new CombinedReviewsDTO(driverReviews.get(i), null));
                i++;
            }
        }

        return retList;
    }

    public VehicleReview save(VehicleReview vehicle) {
        return vehicleReviewRepository.save(vehicle);
    }
    public DriverReview save(DriverReview driver) {
        return driverReviewRepository.save(driver);
    }

}