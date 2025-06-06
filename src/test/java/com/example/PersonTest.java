package com.example;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PersonTest {


    // --- Tests for addPerson() ---

    @Test
    void addPerson_validData_returnsTrueAndCreatesFile() throws Exception {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                              "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        // Verify that persons.txt was created and contains the correct line
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
        // First add a person
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                              "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        // Now update with valid new data
        boolean result = p.updatePersonalDetails("56z_x@#zAB", "Lisa", "Chen",
                                                 "34|Queen St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(result);

        // Verify the file was updated (first line should contain newID|Lisa|Chen|…)
        String content = Files.readString(new File("persons.txt").toPath());
        assertTrue(content.contains("56z_x@#zAB|Lisa|Chen|34|Queen St|Melbourne|Victoria|Australia|15-09-2002"));
    }

    @Test
    void updatePersonalDetails_under18CannotChangeAddress_returnsFalse() {
        // Person born in 2010 is under 18
        Person p = new Person("56s_d%&fAB", "Tim", "Young",
                              "12|King St|Melbourne|Victoria|Australia", "15-09-2010");
        assertTrue(p.addPerson());

        // Trying to update address should fail
        assertFalse(p.updatePersonalDetails("56s_d%&fAB", "Tim", "Young",
                                            "34|Queen St|Melbourne|Victoria|Australia", "15-09-2010"));
    }

    @Test
    void updatePersonalDetails_changeBirthAndAnythingElse_returnsFalse() {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                              "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        // Changing birthday and last name together should fail
        assertFalse(p.updatePersonalDetails("56s_d%&fAB", "Lisa", "Smith",
                                            "12|King St|Melbourne|Victoria|Australia", "16-09-2002"));
    }

    @Test
    void updatePersonalDetails_evenIDCannotChangeID_returnsFalse() {
        Person p = new Person("24s_d%&zAB", "Lisa", "Solih",
                              "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        // ID starts with '2' (even), so changing it should fail
        assertFalse(p.updatePersonalDetails("56x_x@#xAB", "Lisa", "Solih",
                                            "12|King St|Melbourne|Victoria|Australia", "15-09-2002"));
    }

    @Test
    void updatePersonalDetails_invalidNewData_returnsFalse() {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                              "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        // New address is missing Victoria
        assertFalse(p.updatePersonalDetails("56s_d%&fAB", "Lisa", "Solih",
                                            "12|King St|Melbourne|NSW|Australia", "15-09-2002"));
    }

    @Test
    void updatePersonalDetails_noChange_returnsTrue() {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                              "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        // Changing nothing should return true
        assertTrue(p.updatePersonalDetails("56s_d%&fAB", "Lisa", "Solih",
                                           "12|King St|Melbourne|Victoria|Australia", "15-09-2002"));
    }

    // --- Tests for addDemeritPoints() ---

    @Test
    void addDemeritPoints_validDataUnder21_setsSuspendedCorrectly() {
        Person p = new Person("56s_d%&fAB", "Tim", "Young",
                              "12|King St|Melbourne|Victoria|Australia", "15-09-2006"); // Age 18
        assertTrue(p.addPerson());

        // Add demerit points (3 + 4 = 7 within 2 years → should suspend)
        assertEquals("Success", p.addDemeritPoints("01-01-2024", 3));
        assertEquals("Success", p.addDemeritPoints("01-06-2024", 4));
        assertTrue(p.isSuspended()); // ✅ correct way
    }

    @Test
    void addDemeritPoints_validDataOver21_setsSuspendedCorrectly() {
        Person p = new Person("56s_d%&fAB", "Tim", "Old",
                              "12|King St|Melbourne|Victoria|Australia", "15-09-1990"); // Age 34
        assertTrue(p.addPerson());

        // Add 7 + 6 = 13 within 2 years → should suspend for over 21
        assertEquals("Success", p.addDemeritPoints("01-01-2023", 6));
        assertEquals("Success", p.addDemeritPoints("01-06-2023", 6));
        assertTrue(p.isSuspended());
    }

    @Test
    void addDemeritPoints_invalidPoints_returnsFailed() throws Exception {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                              "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        assertEquals("Failed", p.addDemeritPoints("01-01-2024", 9)); // 9 is >6
    }

    @Test
    void addDemeritPoints_invalidDate_returnsFailed() throws Exception {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih",
                              "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        assertTrue(p.addPerson());

        assertEquals("Failed", p.addDemeritPoints("2024-01-01", 3)); // Wrong format
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
