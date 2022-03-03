package com.lecture.carrental.service;

import com.lecture.carrental.domain.Car;
import com.lecture.carrental.domain.Reservation;
import com.lecture.carrental.domain.User;
import com.lecture.carrental.domain.dto.ReservationDTO;
import com.lecture.carrental.domain.enumeration.ReservationStatus;
import com.lecture.carrental.exception.BadRequestException;
import com.lecture.carrental.exception.ResourceNotFoundException;
import com.lecture.carrental.repository.CarRepository;
import com.lecture.carrental.repository.ReservationRepository;
import com.lecture.carrental.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AllArgsConstructor
@Service
public class ReservationService {
    //    @Autowired
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final static String USER_NOT_FOUND_MSG = "user with id %d not found";
    private final static String CAR_NOT_FOUND_MSG = "car with id %d not found";
    private final static String RESERVATION_NOT_FOUND_MSG = "reservation with id %d not found";
    private ReservationService reservationService;

    //    public ReservationService(ReservationRepository reservationRepository) {
    //        this.reservationRepository = reservationRepository;
    //    }
    public List<ReservationDTO> fetchAllReservations() {
        return reservationRepository.findAllBy();
    }
    public ReservationDTO findById(Long id) {
        return reservationRepository.findByIdOrderById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(RESERVATION_NOT_FOUND_MSG, id)));
    }
    public ReservationDTO findByIdAndUserId(Long id, Long userId) throws ResourceNotFoundException{
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
        return reservationRepository.findByIdAndUserId(id, user).orElseThrow(() ->
                new ResourceNotFoundException(String.format(RESERVATION_NOT_FOUND_MSG, id)));
    }
    public List<ReservationDTO> findAllByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
        return reservationRepository.findAllByUserId(user);
    }
    public void addReservation(Reservation reservation, Long userId, Car carId) throws BadRequestException {
        boolean checkStatus = carAvailability(carId.getId(), reservation.getPickUpTime(),
                reservation.getDropOffTime());
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
        if (!checkStatus)
            reservation.setStatus(ReservationStatus.CREATED);
        else
            throw new BadRequestException("Car is already reserved! Please choose another");
        reservation.setCarId(carId);
        reservation.setUserId(user);
        Double totalPrice = totalPrice(reservation.getPickUpTime(),
                reservation.getDropOffTime(), carId.getId());
        reservation.setTotalPrice(totalPrice);
        reservationRepository.save(reservation);
    }
    public void updateReservation(Car carId, Long reservationId, Reservation reservation) throws BadRequestException {
        boolean checkStatus = carAvailability(carId.getId(), reservation.getPickUpTime(), reservation.getDropOffTime());

        Reservation reservationExist = reservationRepository.findById(reservationId).orElseThrow(()->
                new ResourceNotFoundException(String.format(RESERVATION_NOT_FOUND_MSG, reservationId)));

        if (reservation.getPickUpTime().compareTo(reservationExist.getPickUpTime()) == 0 &&
                reservation.getDropOffTime().compareTo(reservationExist.getDropOffTime()) == 0 &&
                carId.getId().equals(reservationExist.getCarId().getId())) {

            reservationExist.setStatus(reservation.getStatus());

        }
        else if (checkStatus)
            throw new BadRequestException("Car is already reserved! Please chose another!");

        Double totalPrice = totalPrice(reservation.getPickUpTime(), reservation.getDropOffTime(), carId.getId());

        reservationExist.setTotalPrice(totalPrice);

        reservationExist.setCarId(carId);
        reservationExist.setPickUpTime(reservation.getPickUpTime());
        reservationExist.setDropOffTime(reservation.getDropOffTime());
        reservationExist.setPickUpLocation(reservation.getPickUpLocation());
        reservationExist.setDropOffLocation(reservation.getDropOffLocation());

        reservationRepository.save(reservationExist);
    }

    public void removeById(Long id) throws BadRequestException {
        boolean reservationsExists = reservationRepository.existsById(id);
        if (!reservationsExists){
            throw new ResourceNotFoundException("reservation does not exists");
        }
        reservationRepository.deleteById(id);
    }

    public boolean carAvailability(Long carId, LocalDateTime pickUpTime, LocalDateTime dropOffTime){
        List<Reservation> checkStatus = reservationRepository
                .checkStatus(carId, pickUpTime, dropOffTime,
                        ReservationStatus.DONE, ReservationStatus.CANCELED);
        return checkStatus.size() > 0;
    }
    public Double totalPrice(LocalDateTime pickUpTime, LocalDateTime dropOffTime, Long carId) {
        Car car = carRepository.findById(carId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(CAR_NOT_FOUND_MSG, carId)));
        Long hours = (new Reservation()).getTotalHours(pickUpTime, dropOffTime);
        return car.getPricePerHour() * hours;
    }

    @GetMapping("/auth")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> checkCarAvailability (@RequestParam(value = "carId") Long carId,
                                                                     @RequestParam(value = "pickUpDateTime")
                                                                     @DateTimeFormat(pattern = "MM/dd/yyyy " +
                                                                             "HH:mm:ss")
                                                                             LocalDateTime pickUptime,
                                                                     @RequestParam(value = "dropOffDateTime")
                                                                     @DateTimeFormat(pattern = "MM/dd/yyyy " +
                                                                             "HH:mm:ss")
                                                                             LocalDateTime dropOffTime) {

        boolean availability = reservationService.carAvailability(carId, pickUptime, dropOffTime);
        Double totalPrice = reservationService.totalPrice(pickUptime, dropOffTime, carId);

        Map<String, Object> map = new HashMap<>();
        map.put("isAvailable", !availability);
        map.put("totalPrice", totalPrice);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}