package eu.profinit.education.flightlog.domain.repositories;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import eu.profinit.education.flightlog.IntegrationTestConfig;
import eu.profinit.education.flightlog.domain.entities.Flight;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IntegrationTestConfig.class)
@Transactional
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class FlightRepositoryTest {

    @Autowired
    private FlightRepository testSubject;

    @Test
    public void shouldLoadFlights() {

        List<Flight> flights = testSubject.findAll();

        assertEquals("There should be 5 flights", 5, flights.size());

    }

//    @Ignore("Test is not implemented")
    @Test
    public void shouldLoadGliderFlights() {
        List<Flight> gliderFlights = testSubject.findAllByFlightType(Flight.Type.GLIDER);
        assertEquals("There should be 2 glider flights", 2, gliderFlights.size());
    }

    @Test
    public void shouldLoadFlightsInTheAir() {
        List<Flight> flights = testSubject.findAllByLandingTimeNullOrderByTakeoffTimeAscIdAsc();

        assertEquals("There should be 3 flights", 3, flights.size());
        assertEquals("Flight with ID 5 started first and should be first", 5L,
                flights.get(0).getId().getId().longValue());
        assertEquals("Flight with ID 1 should be second", 1L, flights.get(1).getId().getId().longValue());
        assertEquals("Flight with ID 2 should be third", 2L, flights.get(2).getId().getId().longValue());
    }

}