package eu.profinit.education.flightlog.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import eu.profinit.education.flightlog.domain.entities.Flight;
import eu.profinit.education.flightlog.domain.repositories.FlightRepository;
import eu.profinit.education.flightlog.exceptions.FlightLogException;
import eu.profinit.education.flightlog.to.FileExportTo;

@Service
public class CsvExportServiceImpl implements CsvExportService {

    private static final String DATE_PATTERN = "dd.MM.yyyy HH:mm:ss";
    public static final MediaType CVS_CONTENT_TYPE = MediaType.valueOf("text/csv");
    private static final Charset ENCODING = StandardCharsets.UTF_8;

    private final FlightRepository flightRepository;

    private final String fileName;

    public CsvExportServiceImpl(FlightRepository flightRepository, @Value("${csv.export.flight.fileName}") String fileName) {
        this.flightRepository = flightRepository;
        this.fileName = fileName;
    }

    @Override
    public FileExportTo getAllFlightsAsCsv() {
        // ID is used to have always same order when takeoffTime is same and coincidentally it will order towplane before glider for each flight
        List<Flight> flights = flightRepository.findAll(Sort.by(Sort.Order.asc("takeoffTime"), Sort.Order.asc("id")));

        try (
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            Writer printWriter = new OutputStreamWriter(byteOutputStream, ENCODING);
            CSVPrinter csvExport = new CSVPrinter(printWriter, CSVFormat.DEFAULT)
        ) {
            csvExport.printRecord("sep=,");
            csvExport.printRecord("FlightID", "TakeoffTime", "LandingTime",
                "Immatriculation", "Type", "Pilot",
                "Copilot", "Task",
                "TowplaneID", "GliderID");

            for (Flight flight : flights) {
                csvExport.printRecord(flight.getId().getId(), formatDateTime(flight.getTakeoffTime()), formatDateTime(flight.getLandingTime()),
                    flight.getAirplane().getSafeImmatriculation(), flight.getAirplane().getSafeType(), flight.getPilot().getFullName(),
                    flight.getCopilot() != null ? flight.getCopilot().getFullName() : null, flight.getTask() != null ? flight.getTask().getValue() : null,
                    flight.getTowplaneFlight() != null ? flight.getTowplaneFlight().getId().getId(): null, flight.getGliderFlight() != null ? flight.getGliderFlight().getId().getId(): null );
            }
            printWriter.flush();
            byteOutputStream.flush();
            return new FileExportTo(fileName, CVS_CONTENT_TYPE, ENCODING, byteOutputStream.toByteArray());
        } catch (IOException exception) {
            throw new FlightLogException("Error during flights CSV export", exception);
        }
    }

    private String formatDateTime(LocalDateTime dateTime){
        if (dateTime == null){
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        return formatter.format(dateTime);
    }

}
