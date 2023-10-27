package eu.profinit.education.flightlog.dao;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("stub")
public class ClubDatabaseDaoStub implements ClubDatabaseDao {

    @Override
    public List<User> getUsers() {
        String backOffice = "BACKOFFICE";
        String pilot = "PILOT";
        return Arrays.asList(
            new User(1L, "Kamila", "Spoustová", List.of(pilot)),
            new User(2L, "Naděžda", "Pavelková", List.of(pilot)),
            new User(3L, "Silvie", "Hronová", List.of(pilot)),
            new User(9L, "Miloš", "Korbel", Arrays.asList(pilot, backOffice)),
            new User(10L, "Petr", "Hrubec", Arrays.asList(pilot, backOffice)),
            new User(13L, "Michal", "Vyvlečka", List.of(backOffice))
        );
    }
}
