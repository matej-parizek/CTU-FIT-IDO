package eu.profinit.education.flightlog.rest;

import static org.springframework.http.ResponseEntity.status;

import java.io.Serializable;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.profinit.education.flightlog.domain.entities.FlightId;
import eu.profinit.education.flightlog.exceptions.ExternalSystemException;
import eu.profinit.education.flightlog.exceptions.FlightLogException;
import eu.profinit.education.flightlog.exceptions.NotFoundException;
import eu.profinit.education.flightlog.exceptions.ValidationException;
import eu.profinit.education.flightlog.service.CsvExportService;
import eu.profinit.education.flightlog.service.FlightService;
import eu.profinit.education.flightlog.to.FileExportTo;
import eu.profinit.education.flightlog.to.FlightLandingTo;
import eu.profinit.education.flightlog.to.FlightTakeoffTo;
import eu.profinit.education.flightlog.to.FlightTo;
import eu.profinit.education.flightlog.to.FlightTuppleTo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FlightController {

    private final FlightService flightService;
    private final CsvExportService csvExportService;

    @RequestMapping("flight/inAir")
    public List<FlightTo> getFlightInTheAir() {
        List<FlightTo> flightsInTheAir = flightService.getFlightsInTheAir();
        log.debug("Flights in the air:\n{}", flightsInTheAir);
        return flightsInTheAir;
    }

    @GetMapping("flight/report")
    public List<FlightTuppleTo> getFlightReport() {
        return flightService.getFlightsForReport();
    }

    @PostMapping("/flight/takeoff")
    public ResponseEntity<Serializable> takeoff(@RequestBody FlightTakeoffTo start) {
         for (int i = 0; i <= 10; i++) {
         System.out.println("Just printing i:" + i);
         }
        log.debug("Start\n{}", start);

        flightService.takeoff(start);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/flight/land")
    public ResponseEntity<Serializable> land(@RequestBody FlightLandingTo landing) {
        log.debug("Land\n{}", landing);

        flightService.land(FlightId.of(landing.getFlightId()), landing.getLandingTime());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("flight/export")
    public ResponseEntity<byte[]> getCsvExport() {
        FileExportTo flightsCsv = csvExportService.getAllFlightsAsCsv();

        return ResponseEntity.ok()
                .contentType(flightsCsv.getContentType())
                .header("Content-Disposition", "attachment; filename=" + flightsCsv.getFileName())
                .body(flightsCsv.getContent());
    }

    @ExceptionHandler({ NotFoundException.class, ValidationException.class })
    public ResponseEntity<String> handleClientException(FlightLogException e) {
        return status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({ ExternalSystemException.class })
    public ResponseEntity<String> handleExternalSystemException(FlightLogException e) {
        return status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }

}
