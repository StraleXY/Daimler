package com.tim1.daimlerback.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tim1.daimlerback.dtos.common.DepartureDestinationDTO;
import com.tim1.daimlerback.dtos.common.LocationDTO;
import com.tim1.daimlerback.dtos.common.ReasonDTO;
import com.tim1.daimlerback.dtos.panic.PanicRideDTO;
import com.tim1.daimlerback.dtos.passenger.FavoriteRouteDTO;
import com.tim1.daimlerback.dtos.passenger.PassengerShortDTO;
import com.tim1.daimlerback.dtos.ride.CreateRideDTO;
import com.tim1.daimlerback.dtos.ride.CreatedRideDTO;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RideControllerTest {

    @Autowired
    MockMvc mockMvc;

    // Working with json in request body and response body
    private static ObjectWriter objectWriter;
    private static ObjectMapper objectMapper;

    private static final String passengerEmail = "p@gmail.com";
    private static final String passengerPassword = "passengerpassword";
    private static String passengerAccessToken;
    private static int passengerId;

    private static final String secondaryPassengerEmail = "passenger55@gmail.com";
    private static final String secondaryPassengerPassword = "passengerpassword";
    private static String secondaryPassengerAccessToken;
    private static int secondaryPassengerId;

    private static final String driverEmail = "driver@gmail.com";
    private static final String driverPassword = "driverpassword";
    private static String driverAccessToken;
    private static int driverId;

    private static int invalidRideId = 10321;
    private static int validRideId = 4;

    private static final long secondTimestamp = 1000;
    private static final long minuteTimestamp = 60 * secondTimestamp;


    void loginPassenger() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/user/login")
                        .content("{\"email\": \"" + passengerEmail + "\", \"password\": \"" + passengerPassword + "\"}")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        passengerAccessToken = jsonObject.getString("accessToken");
        passengerId = jsonObject.getInt("userId");
    }

     void loginSecondaryPassenger() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/user/login")
                        .content("{\"email\": \"" + secondaryPassengerEmail + "\", \"password\": \"" + secondaryPassengerPassword + "\"}")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        secondaryPassengerAccessToken = jsonObject.getString("accessToken");
        secondaryPassengerId = jsonObject.getInt("userId");
    }

    void loginDriver() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/user/login")
                        .content("{\"email\": \"" + driverEmail + "\", \"password\": \"" + driverPassword + "\"}")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        driverAccessToken = jsonObject.getString("accessToken");
        driverId = jsonObject.getInt("userId");
    }

    private CreateRideDTO getValidCreateRideDTO() {
        CreateRideDTO createRideDTO = new CreateRideDTO();
        createRideDTO.setScheduledTimestamp(System.currentTimeMillis());
        createRideDTO.setVehicleType("limousine");
        List<PassengerShortDTO> passengers = new ArrayList<PassengerShortDTO>();
        PassengerShortDTO passenger = new PassengerShortDTO();
        passenger.setId(passengerId);
        passenger.setEmail(passengerEmail);
        passengers.add(passenger);
        createRideDTO.setPassengers(passengers);
        createRideDTO.setBabyTransport(false);
        createRideDTO.setPetTransport(false);
        List<DepartureDestinationDTO> locations = new ArrayList<DepartureDestinationDTO>();
        DepartureDestinationDTO location = new DepartureDestinationDTO();
        LocationDTO departure = new LocationDTO();
        departure.setAddress("Departure address");
        departure.setLongitude(19.8);
        departure.setLatitude(45.21);
        departure.setId(21);
        LocationDTO destination = new LocationDTO();
        destination.setAddress("Destination address");
        destination.setLongitude(19.75);
        destination.setLatitude(45.25);
        destination.setId(22);
        location.setDeparture(departure);
        location.setDestination(destination);
        locations.add(location);
        createRideDTO.setLocations(locations);
        return createRideDTO;
    }

    @BeforeEach
    void setUp() throws Exception {
        loginPassenger();
        loginSecondaryPassenger();
        loginDriver();
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        objectMapper = new ObjectMapper();
    }

    // --------------------------------------------- createRide ------------------------------------------------- //

    @Test
    @Order(4)
    void createScheduledRideTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        createRideDTO.setScheduledTimestamp(System.currentTimeMillis() + 100 * minuteTimestamp);
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        MvcResult mvcResult = mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        CreatedRideDTO createdRideDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreatedRideDTO.class);
        Assertions.assertEquals("PENDING", createdRideDTO.getStatus());
    }

    @Test
    @Order(4)
    void createRideTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        createRideDTO.getPassengers().get(0).setId(8);
        createRideDTO.getPassengers().get(0).setEmail("p");
        createRideDTO.setVehicleType("coupe");
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        MvcResult mvcResult = mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        CreatedRideDTO createdRideDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreatedRideDTO.class);
        Assertions.assertEquals("ACCEPTED", createdRideDTO.getStatus());
        Assertions.assertNotNull(createdRideDTO.getDriver());
        System.out.println(createdRideDTO.getDriver().getEmail());
        System.out.println(createdRideDTO.getDriver().getId());
    }

    @Test
    void createRideUnauthorizedTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        mockMvc.perform(post("/api/ride")
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createRideForbiddenTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + driverAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    void createRideBadVehicleTypeTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        createRideDTO.setVehicleType("Non existing vehicle type");
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void createRideNullVehicleTypeTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        createRideDTO.setVehicleType(null);
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRideNullPassengersTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        createRideDTO.setPassengers(null);
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRideEmptyPassengersTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        createRideDTO.setPassengers(new ArrayList<PassengerShortDTO>());
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRideNullPassengerTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        List<PassengerShortDTO> passengers = new ArrayList<PassengerShortDTO>();
        passengers.add(null);
        createRideDTO.setPassengers(passengers);
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRideNullRequestBodyTest() throws Exception {
        String requestBody = objectWriter.writeValueAsString(null);
        mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRideNullPetTransportTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        createRideDTO.setPetTransport(null);
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRideNullBabyTransportTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        createRideDTO.setBabyTransport(null);
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRideNullLocationsTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        createRideDTO.setLocations(null);
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRideNullScheduledTimestampTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        createRideDTO.setScheduledTimestamp(null);
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRideBadPassengerTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        List<PassengerShortDTO> passengers = new ArrayList<PassengerShortDTO>();
        PassengerShortDTO passenger = new PassengerShortDTO();
        passenger.setId(10012);
        passenger.setEmail("nonExisting@gmail.com");
        passengers.add(passenger);
        createRideDTO.setPassengers(passengers);
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void createRideWhenPendingRideExistsTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        List<PassengerShortDTO> passengers = new ArrayList<PassengerShortDTO>();
        PassengerShortDTO passenger = new PassengerShortDTO();
        passenger.setId(20);
        passenger.setEmail("passenger20@gmail.com");
        passengers.add(passenger);
        createRideDTO.setPassengers(passengers);
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void createRideAllDriversBusyTest() throws Exception {
        CreateRideDTO createRideDTO = getValidCreateRideDTO();
        String requestBody = objectWriter.writeValueAsString(createRideDTO);
        mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // --------------------------------------------- getDriverActiveRide ------------------------------------------------- //

    @Test
    void getDriverActiveRideUnauthorizedTest() throws Exception {
        mockMvc.perform(get("/api/ride/driver/{driverId}/active", driverId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getDriverActiveRideForbiddenTest() throws Exception {
        mockMvc.perform(get("/api/ride/driver/{driverId}/active", driverId)
                        .header("Authorization", "Bearer " + passengerAccessToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(1)
    void getDriverActiveRideNotFoundTest() throws Exception {
        mockMvc.perform(get("/api/ride/driver/{driverId}/active", driverId)
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    void getDriverActiveRideTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/ride/driver/{driverId}/active", driverId)
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        CreatedRideDTO createdRideDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreatedRideDTO.class);
        Assertions.assertEquals("ACCEPTED", createdRideDTO.getStatus());
    }

    // --------------------------------------------- getPassengerActiveRide ------------------------------------------------- //

    @Test
    void getPassengerActiveRideUnauthorizedTest() throws Exception {
        mockMvc.perform(get("/api/ride/passenger/{passengerId}/active", passengerId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPassengerActiveRideForbiddenTest() throws Exception {
        mockMvc.perform(get("/api/ride/passenger/{passengerId}/active", passengerId)
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(1)
    void getPassengerActiveRideNotFoundTest() throws Exception {
        mockMvc.perform(get("/api/ride/passenger/{passengerId}/active", passengerId)
                        .header("Authorization", "Bearer " + passengerAccessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    void getPassengerActiveRideTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/ride/passenger/{passengerId}/active", passengerId)
                        .header("Authorization", "Bearer " + passengerAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        CreatedRideDTO createdRideDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreatedRideDTO.class);
        Assertions.assertEquals("PENDING", createdRideDTO.getStatus());
    }

    // --------------------------------------------- getRideDetails ------------------------------------------------- //

    @Test
    void getRideDetailsUnauthorizedTest() throws Exception {
        mockMvc.perform(get("/api/ride/{id}", validRideId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getRideDetailsInvalidIdTest() throws Exception {
        mockMvc.perform(get("/api/ride/{id}", invalidRideId)
                        .header("Authorization", "Bearer " + passengerAccessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getRideDetailsTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/ride/{id}", validRideId)
                        .header("Authorization", "Bearer " + passengerAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        CreatedRideDTO createdRideDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreatedRideDTO.class);
        Assertions.assertEquals(validRideId, createdRideDTO.getId());
        Assertions.assertEquals("FINISHED", createdRideDTO.getStatus());
        Assertions.assertFalse(createdRideDTO.getBabyTransport());
        Assertions.assertFalse(createdRideDTO.getPetTransport());
        Assertions.assertEquals(20, createdRideDTO.getEstimatedTimeInMinutes());
    }

    // ----------------------------------------------- acceptRide --------------------------------------------------- //

    private CreatedRideDTO createMockedRide() throws Exception {
        CreateRideDTO dto = getValidCreateRideDTO();
        dto.setVehicleType("coupe");
        String requestBody = objectWriter.writeValueAsString(dto);
        MvcResult mvcResult = mockMvc.perform(post("/api/ride")
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreatedRideDTO.class);
    }

    private CreatedRideDTO changeMockedRideStatus(Integer rideId, String rideStatus) throws Exception {
        MvcResult mvcResult = mockMvc.perform(put("/api/ride/{id}/" + rideStatus, rideId)
                        .header("Authorization", "Bearer " + driverAccessToken)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreatedRideDTO.class);
    }

    @Test
    void acceptRideUnauthorizedTest() throws Exception {
        mockMvc.perform(put("/api/ride/{id}/accept", validRideId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acceptRideForbiddenTest() throws Exception {
        mockMvc.perform(put("/api/ride/{id}/accept", validRideId)
                .header("Authorization", "Bearer " + passengerAccessToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void acceptRideInvalidIdTest() throws Exception {
        mockMvc.perform(put("/api/ride/{id}/accept", 601)
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(0)
    void acceptRideNotPendingTest() throws Exception {
        CreatedRideDTO dto = createMockedRide();
        dto = changeMockedRideStatus(dto.getId(), "accept");
        dto = changeMockedRideStatus(dto.getId(), "start");
        mockMvc.perform(put("/api/ride/{id}/accept", dto.getId())
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());
        dto = changeMockedRideStatus(dto.getId(), "end");
    }

    @Test
    @Order(0)
    void acceptRideTest() throws Exception {
        CreatedRideDTO dto = createMockedRide();
        MvcResult mvcResult = mockMvc.perform(put("/api/ride/{id}/accept", dto.getId())
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        CreatedRideDTO createdRideDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreatedRideDTO.class);
        Assertions.assertEquals(dto.getId(), createdRideDTO.getId());
        Assertions.assertEquals("ACCEPTED", createdRideDTO.getStatus());
        dto = changeMockedRideStatus(dto.getId(), "start");
        dto = changeMockedRideStatus(dto.getId(), "end");
    }

    // ---------------------------------------------- withdrawRide -------------------------------------------------- //

    @Test
    void withdrawRideUnauthorizedTest() throws Exception {
        mockMvc.perform(put("/api/ride/{id}/withdraw", validRideId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void withdrawRideForbiddenTest() throws Exception {
        mockMvc.perform(put("/api/ride/{id}/withdraw", validRideId)
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void withdrawRideInvalidIdTest() throws Exception {
        mockMvc.perform(put("/api/ride/{id}/withdraw", 601)
                        .header("Authorization", "Bearer " + passengerAccessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(0)
    void withdrawRideNotPendingTest() throws Exception {
        CreatedRideDTO dto = createMockedRide();
        dto = changeMockedRideStatus(dto.getId(), "accept");
        dto = changeMockedRideStatus(dto.getId(), "start");
        mockMvc.perform(put("/api/ride/{id}/withdraw", dto.getId())
                        .header("Authorization", "Bearer " + passengerAccessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());
        dto = changeMockedRideStatus(dto.getId(), "end");
    }

    @Test
    @Order(0)
    void withdrawRideTest() throws Exception {
        CreatedRideDTO dto = createMockedRide();
        MvcResult mvcResult = mockMvc.perform(put("/api/ride/{id}/withdraw", dto.getId())
                        .header("Authorization", "Bearer " + passengerAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        CreatedRideDTO createdRideDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreatedRideDTO.class);
        Assertions.assertEquals(dto.getId(), createdRideDTO.getId());
        Assertions.assertEquals("CANCELED", createdRideDTO.getStatus());
        Assertions.assertEquals(null, createdRideDTO.getEndTime());
    }

    // ------------------------------------------------ panicRide --------------------------------------------------- //

    @Test
    void panicRideUnauthorizedTest() throws Exception {
        String requestBody = objectWriter.writeValueAsString(new ReasonDTO());
        mockMvc.perform(put("/api/ride/{id}/panic", validRideId)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void panicRideForbiddenTest() throws Exception {
        String requestBody = objectWriter.writeValueAsString(new ReasonDTO());
        mockMvc.perform(put("/api/ride/{id}/panic", validRideId)
                        .header("Authorization", "Bearer " + "test")
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void panicRideInvalidIdTest() throws Exception {
        String requestBody = objectWriter.writeValueAsString(new ReasonDTO());
        mockMvc.perform(put("/api/ride/{id}/panic", 601)
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(0)
    void panicFinishedRideTest() throws Exception {
        String requestBody = objectWriter.writeValueAsString(new ReasonDTO());
        CreatedRideDTO dto = createMockedRide();
        dto = changeMockedRideStatus(dto.getId(), "accept");
        dto = changeMockedRideStatus(dto.getId(), "start");
        dto = changeMockedRideStatus(dto.getId(), "end");
        mockMvc.perform(put("/api/ride/{id}/panic", dto.getId())
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(0)
    void panicRidePassengerTest() throws Exception {
        String reasonString = "testReason";
        ReasonDTO reason = new ReasonDTO();
        reason.setReason(reasonString);
        String requestBody = objectWriter.writeValueAsString(reason);
        CreatedRideDTO dto = createMockedRide();
        dto = changeMockedRideStatus(dto.getId(), "accept");
        dto = changeMockedRideStatus(dto.getId(), "start");
        MvcResult mvcResult = mockMvc.perform(put("/api/ride/{id}/panic", dto.getId())
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        PanicRideDTO panicRideDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PanicRideDTO.class);
        Assertions.assertEquals(panicRideDTO.getRide().getId(), dto.getId());
        Assertions.assertEquals(panicRideDTO.getReason(), reasonString);
        Assertions.assertEquals(panicRideDTO.getRide().getStatus(), "PANIC");
        Assertions.assertEquals(panicRideDTO.getUser().getEmail(), passengerEmail);
        Assertions.assertNotNull(panicRideDTO.getTime());
    }

    @Test
    @Order(0)
    void panicRideDriverTest() throws Exception {
        String reasonString = "testReason";
        ReasonDTO reason = new ReasonDTO();
        reason.setReason(reasonString);
        String requestBody = objectWriter.writeValueAsString(reason);
        CreatedRideDTO dto = createMockedRide();
        dto = changeMockedRideStatus(dto.getId(), "accept");
        dto = changeMockedRideStatus(dto.getId(), "start");
        MvcResult mvcResult = mockMvc.perform(put("/api/ride/{id}/panic", dto.getId())
                        .header("Authorization", "Bearer " + driverAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        PanicRideDTO panicRideDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PanicRideDTO.class);
        Assertions.assertEquals(panicRideDTO.getRide().getId(), dto.getId());
        Assertions.assertEquals(panicRideDTO.getReason(), reasonString);
        Assertions.assertEquals(panicRideDTO.getRide().getStatus(), "PANIC");
        Assertions.assertEquals(panicRideDTO.getUser().getEmail(), driverEmail);
        Assertions.assertNotNull(panicRideDTO.getTime());
    }

    // ------------------------------------------------ endRide ----------------------------------------------------- //

    @Test
    void endRideUnauthorizedTest() throws Exception {
        mockMvc.perform(put("/api/ride/{id}/end", validRideId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void endRideForbiddenTest() throws Exception {
        mockMvc.perform(put("/api/ride/{id}/end", validRideId)
                        .header("Authorization", "Bearer " + passengerAccessToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void endRideInvalidIdTest() throws Exception {
        mockMvc.perform(put("/api/ride/{id}/end", 601)
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(0)
    void endRideNotActiveTest() throws Exception {
        CreatedRideDTO dto = createMockedRide();
        mockMvc.perform(put("/api/ride/{id}/end", dto.getId())
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());
        changeMockedRideStatus(dto.getId(), "accept");
        changeMockedRideStatus(dto.getId(), "start");
        changeMockedRideStatus(dto.getId(), "end");
    }

    @Test
    @Order(0)
    void endRideTest() throws Exception {
        CreatedRideDTO dto = createMockedRide();
        changeMockedRideStatus(dto.getId(), "accept");
        changeMockedRideStatus(dto.getId(), "start");
        MvcResult mvcResult = mockMvc.perform(put("/api/ride/{id}/end", dto.getId())
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        CreatedRideDTO createdRideDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreatedRideDTO.class);
        Assertions.assertEquals(dto.getId(), createdRideDTO.getId());
        Assertions.assertEquals("FINISHED", createdRideDTO.getStatus());
        Assertions.assertNotNull(createdRideDTO.getEndTime());
    }

    // ----------------------------------------------- startRide ---------------------------------------------------- //

    @Test
    void startRideUnauthorizedTest() throws Exception {
        mockMvc.perform(put("/api/ride/{id}/start", validRideId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void startRideForbiddenTest() throws Exception {
        mockMvc.perform(put("/api/ride/{id}/start", validRideId)
                        .header("Authorization", "Bearer " + passengerAccessToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void startRideInvalidIdTest() throws Exception {
        mockMvc.perform(put("/api/ride/{id}/start", 601)
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(0)
    void startRideNotActiveTest() throws Exception {
        CreatedRideDTO dto = createMockedRide();
        changeMockedRideStatus(dto.getId(), "accept");
        changeMockedRideStatus(dto.getId(), "start");
        changeMockedRideStatus(dto.getId(), "end");
        mockMvc.perform(put("/api/ride/{id}/start", dto.getId())
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(0)
    void startRideTest() throws Exception {
        CreatedRideDTO dto = createMockedRide();
        changeMockedRideStatus(dto.getId(), "accept");
        MvcResult mvcResult = mockMvc.perform(put("/api/ride/{id}/start", dto.getId())
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        CreatedRideDTO createdRideDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreatedRideDTO.class);
        Assertions.assertEquals(dto.getId(), createdRideDTO.getId());
        Assertions.assertEquals("ACTIVE", createdRideDTO.getStatus());
        Assertions.assertNotNull(createdRideDTO.getStartTime());
        changeMockedRideStatus(dto.getId(), "end");
    }

    // ----------------------------------------------- cancelRide --------------------------------------------------- //
    @Test
    void cancelRideUnauthorizedTest() throws Exception {
        ReasonDTO reason = new ReasonDTO();
        reason.setReason("Test");
        String requestBody = objectWriter.writeValueAsString(reason);
        mockMvc.perform(put("/api/ride/{id}/cancel", validRideId)
                .contentType("application/json")
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    void cancelRideForbiddenTest() throws Exception {
        ReasonDTO reason = new ReasonDTO();
        reason.setReason("Test");
        String requestBody = objectWriter.writeValueAsString(reason);
        mockMvc.perform(put("/api/ride/{id}/cancel", validRideId)
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
    @Test
    void cancelRideNotFoundTest() throws Exception {
        ReasonDTO reason = new ReasonDTO();
        reason.setReason("Test");
        String requestBody = objectWriter.writeValueAsString(reason);
        mockMvc.perform(put("/api/ride/{id}/cancel", 5553)
                        .header("Authorization", "Bearer " + driverAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    @Order(0)
    void cancelRideStartedTest() throws Exception {
        CreatedRideDTO dto = createMockedRide();
        dto = changeMockedRideStatus(dto.getId(), "accept");
        dto = changeMockedRideStatus(dto.getId(), "start");
        ReasonDTO reason = new ReasonDTO();
        reason.setReason("Test");
        String requestBody = objectWriter.writeValueAsString(reason);
        mockMvc.perform(put("/api/ride/{id}/cancel", dto.getId())
                        .header("Authorization", "Bearer " + driverAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
        dto = changeMockedRideStatus(dto.getId(), "end");
    }
    @Test
    @Order(0)
    void cancelRideTest() throws Exception {
        CreatedRideDTO dto = createMockedRide();
        dto = changeMockedRideStatus(dto.getId(), "accept");
        ReasonDTO reason = new ReasonDTO();
        reason.setReason("Test");
        String requestBody = objectWriter.writeValueAsString(reason);
        MvcResult mvcResult = mockMvc.perform(put("/api/ride/{id}/cancel", dto.getId())
                        .header("Authorization", "Bearer " + driverAccessToken)
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        CreatedRideDTO createdRideDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreatedRideDTO.class);
        Assertions.assertEquals(dto.getId(), createdRideDTO.getId());
        Assertions.assertEquals("REJECTED", createdRideDTO.getStatus());
        assertNull(createdRideDTO.getEndTime());
    }
    // ------------------------------------------ addRouteToFavourite ----------------------------------------------- //
    @Test
    void addRouteToFavouriteUnauthorizedTest() throws Exception {
        mockMvc.perform(post("/api/ride/{passenger_id}/favorite", passengerId)
                        .param("departureId", "1")
                        .param("destinationId", "2"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    void addRouteToFavouriteForbiddenTest() throws Exception {
        mockMvc.perform(post("/api/ride/{passenger_id}/favorite", passengerId)
                        .header("Authorization", "Bearer " + driverAccessToken)
                        .param("departureId", "1")
                        .param("destinationId", "2"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
    @Test
    void addRouteToFavouriteLocationsNotFoundTest() throws Exception {
        mockMvc.perform(post("/api/ride/{passenger_id}/favorite", passengerId)
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .param("departureId", "100")
                        .param("destinationId", "200"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    @Order(0)
    void addRouteToFavouriteTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/ride/{passenger_id}/favorite", passengerId)
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .param("departureId", "1")
                        .param("destinationId", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        FavoriteRouteDTO favoriteRouteDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FavoriteRouteDTO.class);
        Assertions.assertEquals(1, favoriteRouteDTO.getDeparture().getId());
        Assertions.assertEquals(2, favoriteRouteDTO.getDestination().getId());
    }
    @Test
    void addRouteToFavouriteDuplicatedRouteTest() throws Exception {
        mockMvc.perform(post("/api/ride/{passenger_id}/favorite", passengerId)
                        .header("Authorization", "Bearer " + passengerAccessToken)
                        .param("departureId", "1")
                        .param("destinationId", "2"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------- deleteFavourite ------------------------------------------------- //
    @Test
    void deleteFavouriteUnauthorizedTest() throws Exception {
        mockMvc.perform(delete("/api/ride/{passenger_id}/favorite/{id}", passengerId, 1))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    void deleteFavouriteForbiddenTest() throws Exception {
        mockMvc.perform(delete("/api/ride/{passenger_id}/favorite/{id}", passengerId, 1)
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
    @Test
    void deleteFavouriteInvalidUserTest() throws Exception {
        mockMvc.perform(delete("/api/ride/{passenger_id}/favorite/{id}", secondaryPassengerId, 2)
                        .header("Authorization", "Bearer " + secondaryPassengerAccessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    void deleteFavouriteNotFoundTest() throws Exception {
        mockMvc.perform(delete("/api/ride/{passenger_id}/favorite/{id}", passengerId, 555)
                        .header("Authorization", "Bearer " + passengerAccessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    void deleteFavouriteTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete("/api/ride/{passenger_id}/favorite/{id}", passengerId, 1)
                        .header("Authorization", "Bearer " + passengerAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        FavoriteRouteDTO favoriteRouteDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FavoriteRouteDTO.class);
        Assertions.assertEquals(1, favoriteRouteDTO.getId());
    }

    // ------------------------------------------- getFavouriteRoutes ----------------------------------------------- //
    @Test
    void getFavouriteRoutesUnauthorizedTest() throws Exception {
        mockMvc.perform(get("/api/ride/{passenger_id}/favorite", passengerId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    void getFavouriteRoutesForbiddenTest() throws Exception {
        mockMvc.perform(get("/api/ride/{passenger_id}/favorite", passengerId)
                        .header("Authorization", "Bearer " + driverAccessToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getFavouriteNotFoundTest() throws Exception {
        mockMvc.perform(get("/api/ride/{passenger_id}/favorite", secondaryPassengerId)
                        .header("Authorization", "Bearer " + secondaryPassengerAccessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getFavouriteTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/ride/{passenger_id}/favorite", passengerId)
                        .header("Authorization", "Bearer " + passengerAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<FavoriteRouteDTO> favoriteRouteDTOS = List.of(objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FavoriteRouteDTO[].class));
        Assertions.assertEquals(2, favoriteRouteDTOS.size());
        for(FavoriteRouteDTO favorite : favoriteRouteDTOS)
            Assertions.assertEquals(passengerId, favorite.getPassengerId());

    }

}