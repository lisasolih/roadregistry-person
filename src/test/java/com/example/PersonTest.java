package com.example;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersonTest {

    @BeforeEach
    void cleanupFiles() {
        File personsFile = new File("persons.txt");
        if (personsFile.exists()) personsFile.delete();
        File demeritsFile = new File("demerits.txt");
        if (demeritsFile.exists()) demeritsFile.delete();
    }

    // --- Tests for addPerson() ---

    @Test
    void addPerson_validData_returnsTrueAndCreatesFile() throws Exception {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        assertTrue(new File("persons.txt").exists());
        String content = Files.readString(new File("persons.txt").toPath());
        assertTrue(content.contains("56s_d%&fAB|Lisa|Solih|12|King St|Melbourne|Victoria|Australia|15-09-2002"));
    }

    @Test
    void addPerson_invalidID_returnsFalse() {
        Person p = new Person("12abcDEFgh", "Lisa", "Solih",
                "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertFalse(p.addPerson());
    }

    @Test
    void addPerson_invalidAddress_returnsFalse() {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                "12|King St|Sydney|New South Wales|Australia", "15-09-2002");
        assertFalse(p.addPerson());
    }

    @Test
    void addPerson_invalidBirthdate_returnsFalse() {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                "12|King St|Melbourne|Victoria|Australia", "2002-09-15");
        assertFalse(p.addPerson());
    }

    @Test
    void addPerson_allInvalid_returnsFalse() {
        Person p = new Person("ABC", "Lisa", "Solih",
                "Wrong|Format", "2002");
        assertFalse(p.addPerson());
    }

    // --- Tests for updatePersonalDetails() ---

    @Test
    void updatePersonalDetails_validUpdate_returnsTrue() throws Exception {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        boolean result = p.updatePersonalDetails("56z_x@#zAB", "Lisa", "Chen",
                "34|Queen St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(result);

        String content = Files.readString(new File("persons.txt").toPath());
        assertTrue(content.contains("56z_x@#zAB|Lisa|Chen|34|Queen St|Melbourne|Victoria|Australia|15-09-2002"));
    }

    @Test
    void updatePersonalDetails_under18CannotChangeAddress_returnsFalse() {
        Person p = new Person("56s_d%&fAB", "Tim", "Young",
                "12|King St|Melbourne|Victoria|Australia", "15-09-2010");
        assertTrue(p.addPerson());

        assertFalse(p.updatePersonalDetails("56s_d%&fAB", "Tim", "Young",
                "34|Queen St|Melbourne|Victoria|Australia", "15-09-2010"));
    }

    @Test
    void updatePersonalDetails_changeBirthAndAnythingElse_returnsFalse() {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        assertFalse(p.updatePersonalDetails("56s_d%&fAB", "Lisa", "Smith",
                "12|King St|Melbourne|Victoria|Australia", "16-09-2002"));
    }

    @Test
    void updatePersonalDetails_evenIDCannotChangeID_returnsFalse() {
        Person p = new Person("24s_d%&zAB", "Lisa", "Solih",
                "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        assertFalse(p.updatePersonalDetails("56x_x@#xAB", "Lisa", "Solih",
                "12|King St|Melbourne|Victoria|Australia", "15-09-2002"));
    }

    @Test
    void updatePersonalDetails_invalidNewData_returnsFalse() {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        assertFalse(p.updatePersonalDetails("56s_d%&fAB", "Lisa", "Solih",
                "12|King St|Melbourne|NSW|Australia", "15-09-2002"));
    }

    @Test
    void updatePersonalDetails_noChange_returnsTrue() {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        assertTrue(p.updatePersonalDetails("56s_d%&fAB", "Lisa", "Solih",
                "12|King St|Melbourne|Victoria|Australia", "15-09-2002"));
    }

    // --- Tests for addDemeritPoints() ---

    @Test
    void addDemeritPoints_validDataUnder21_setsSuspendedCorrectly() {
        Person p = new Person("56s_d%&fAB", "Tim", "Young",
                "12|King St|Melbourne|Victoria|Australia", "15-09-2006");
        assertTrue(p.addPerson());

        assertEquals("Success", p.addDemeritPoints("01-01-2024", 3));
        assertEquals("Success", p.addDemeritPoints("01-06-2024", 4));
        assertTrue(p.isSuspended());
    }

    @Test
    void addDemeritPoints_validDataOver21_setsSuspendedCorrectly() {
        Person p = new Person("56s_d%&fAB", "Tim", "Old",
                "12|King St|Melbourne|Victoria|Australia", "15-09-1990");
        assertTrue(p.addPerson());

        assertEquals("Success", p.addDemeritPoints("01-01-2023", 6));
        assertEquals("Success", p.addDemeritPoints("01-06-2023", 6));
        assertTrue(p.isSuspended());
    }

    @Test
    void addDemeritPoints_invalidPoints_returnsFailed() {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        assertEquals("Failed", p.addDemeritPoints("01-01-2024", 9));
    }

    @Test
    void addDemeritPoints_invalidDate_returnsFailed() {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        assertEquals("Failed", p.addDemeritPoints("2024-01-01", 3));
    }

    @Test
    void addDemeritPoints_successCreatesFile() throws Exception {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        assertEquals("Success", p.addDemeritPoints("01-01-2024", 2));
        assertTrue(new File("demerits.txt").exists());
    }
}
