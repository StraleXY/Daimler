package com.tim1.daimlerback.services;


import com.tim1.daimlerback.dtos.common.AssumptionDTO;
import com.tim1.daimlerback.dtos.common.DepartureDestinationDTO;
import com.tim1.daimlerback.dtos.common.EstimationDTO;
import com.tim1.daimlerback.dtos.common.LocationDTO;
import com.tim1.daimlerback.entities.VehicleType;
import com.tim1.daimlerback.repositories.IVehicleTypeRepository;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EstimationService {
    @Autowired
    private IVehicleTypeRepository vehicleTypeRepository;

    public EstimationDTO getEstimate(AssumptionDTO dto) {
        Optional<VehicleType> vehicleType = vehicleTypeRepository.findByName(dto.getVehicleType());
        if (vehicleType.isEmpty()) {
            String value = "message: Vehicle type does not exist";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        VehicleType type = vehicleType.get();
        ArrayList<DepartureDestinationDTO> locations = (ArrayList<DepartureDestinationDTO>) dto.getLocations();
        if (locations.isEmpty()) {
            String value = "message: Location list cannot be empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        DepartureDestinationDTO depDest = locations.get(0);
        EstimationDTO ret = osrmRequest(depDest);
        ret.setEstimatedCost(ret.getEstimatedCost() + type.getPrice());
        return ret;
    }

    public EstimationDTO osrmRequest(DepartureDestinationDTO depDest) {
        String uri = "http://router.project-osrm.org/route/v1/driving/" + depDest.getDeparture().getLongitude() + "," + depDest.getDeparture().getLatitude()
                + ";" + depDest.getDestination().getLongitude() + "," + depDest.getDestination().getLatitude();
        String distanceString = "", durationString = "";
        Double distance = 0.0, time = 0.0;
        EstimationDTO ret = new EstimationDTO();
        try {
            HttpGet request = new HttpGet(uri);
            CloseableHttpClient client = HttpClients.createDefault();
            String response = client.execute(request, new BasicResponseHandler());

            Pattern distancePattern = Pattern.compile("(?<=\"distance\":)[0-9\\.]+");
            Matcher distanceMatcher = distancePattern.matcher(response);
            if (distanceMatcher.find()) distanceString = distanceMatcher.group(0);

            Pattern durationPattern = Pattern.compile("(?<=\"duration\":)[0-9\\.]+");
            Matcher durationMatcher = durationPattern.matcher(response);
            if (durationMatcher.find()) durationString = durationMatcher.group(0);

            // Time is in seconds
            time = Double.parseDouble(durationString) / 60;
            ret.setEstimatedTimeInMinutes(time.intValue());
            // Distance is in meters
            distance = Double.parseDouble(distanceString);
            ret.setDistance(distance);
            distance = 120 * distance / 1000;
            ret.setEstimatedCost(distance.intValue());
        } catch (Exception ex) {
            ex.printStackTrace();
            String value = "message: OSRM request failed";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        return ret;
    }
}
