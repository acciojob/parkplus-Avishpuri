package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        //Reserve a spot in the given parkingLot such that the total price is minimum. Note that the price per hour for each spot is different
        //Note that the vehicle can only be parked in a spot having a type equal to or larger than given vehicle
        //If parkingLot is not found, user is not found, or no spot is available, throw "Cannot make reservation" exception.
        try {
            Reservation reservation = new Reservation(timeInHours);
            User user = userRepository3.findById(userId).get();
            ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();
            reservation.setUser(user);
            List<Spot> spots = parkingLot.getSpotList();
            Spot spot = null;
            int wheels = 0;
            int price = Integer.MAX_VALUE;
            for (Spot spot1 : spots) {
                if (spot1.getSpotType() == SpotType.TWO_WHEELER) {
                    wheels = 2;
                } else if (spot1.getSpotType() == SpotType.FOUR_WHEELER) {
                    wheels = 4;
                } else {
                    wheels = 100;
                }
                if (!spot1.getOccupied() && wheels > numberOfWheels && spot1.getPricePerHour() < price) {
                    spot = spot1;
                    price = spot1.getPricePerHour();
                }
            }
            if (spot == null) {
                throw new Exception("Cannot make reservation");
            }
            reservation.setSpot(spot);
            spot.setOccupied(true);
            user.getReservationList().add(reservation);
            spot.getReservationList().add(reservation);
            spotRepository3.save(spot);
            userRepository3.save(user);
            return reservation;
        }
        catch (Exception e){
            throw new Exception("Cannot make reservation");
        }
    }
}
