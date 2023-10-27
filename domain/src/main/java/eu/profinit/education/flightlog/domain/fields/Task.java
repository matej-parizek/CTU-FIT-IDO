package eu.profinit.education.flightlog.domain.fields;

import eu.profinit.education.flightlog.domain.JpaConstants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Task {

    public final static Task TowelPlaneTask = new Task("VLEK");

    @Getter
    @Column(name= JpaConstants.Columns.TASK)
    private String value;

    public static Task of(String value) {
        return new Task(value);
    }

}
