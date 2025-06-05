import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PersonTest {

    @Test
    public void testAddPerson_Valid() {
        Person p = new Person("56$%abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(p.addPerson());
    }

    @Test
    public void testAddPerson_InvalidID() {
        Person p = new Person("12abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertFalse(p.addPerson());
    }

    @Test
    public void testAddPerson_InvalidAddress() {
        Person p = new Person("56$%abcAB", "John", "Doe", "32|Main Street|Melbourne|NSW|Australia", "15-11-1990");
        assertFalse(p.addPerson());
    }

    @Test
    public void testAddPerson_InvalidBirthdate() {
        Person p = new Person("56$%abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15/11/1990");
        assertFalse(p.addPerson());
    }

    @Test
    public void testAddPerson_ValidEdgeCase() {
        Person p = new Person("29@#defGH", "Jane", "Smith", "45|Queen Street|Melbourne|Victoria|Australia", "01-01-2000");
        assertTrue(p.addPerson());
    }

    @Test
    public void testUpdateDetails_ChangeBirthdayOnly() {
        Person p = new Person("56$%abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-2005");
        p.addPerson();
        assertTrue(p.updatePersonalDetails("56$%abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "16-11-2005"));
    }

    @Test
    public void testUpdateDetails_Under18AddressChangeFail() {
        Person p = new Person("56$%abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-2010");
        p.addPerson();
        assertFalse(p.updatePersonalDetails("56$%abcAB", "John", "Doe", "33|New Street|Melbourne|Victoria|Australia", "15-11-2010"));
    }

    @Test
    public void testUpdateDetails_EvenIDChangeFail() {
        Person p = new Person("28$%abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-1990");
        p.addPerson();
        assertFalse(p.updatePersonalDetails("29$%abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-1990"));
    }

    @Test
    public void testUpdateDetails_ValidUpdate() {
        Person p = new Person("57$%abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-1990");
        p.addPerson();
        assertTrue(p.updatePersonalDetails("57$%abcAB", "Johnny", "Doey", "32|Main Street|Melbourne|Victoria|Australia", "15-11-1990"));
    }

    @Test
    public void testUpdateDetails_BirthdayChangeWithOtherFail() {
        Person p = new Person("57$%abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-1990");
        p.addPerson();
        assertFalse(p.updatePersonalDetails("57$%abcAB", "Johnny", "Doey", "33|New Street|Melbourne|Victoria|Australia", "16-11-1990"));
    }

    @Test
    public void testAddDemeritPoints_Valid() {
        Person p = new Person("57$%abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-1990");
        p.addPerson();
        assertEquals("Success", p.addDemeritPoints("15-11-2023", 4));
    }

    @Test
    public void testAddDemeritPoints_InvalidDate() {
        Person p = new Person("57$%abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-1990");
        p.addPerson();
        assertEquals("Failed", p.addDemeritPoints("15/11/2023", 4));
    }

    @Test
    public void testAddDemeritPoints_InvalidPoints() {
        Person p = new Person("57$%abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-1990");
        p.addPerson();
        assertEquals("Failed", p.addDemeritPoints("15-11-2023", 10));
    }

    @Test
    public void testAddDemeritPoints_SuspendUnder21() {
        Person p = new Person("57$%abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-2005");
        p.addPerson();
        p.addDemeritPoints("01-06-2024", 4);
        p.addDemeritPoints("15-06-2024", 4);
        assertTrue(p.isSuspended);
    }

    @Test
    public void testAddDemeritPoints_SuspendOver21() {
        Person p = new Person("57$%abcAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-1990");
        p.addPerson();
        p.addDemeritPoints("01-06-2024", 6);
        p.addDemeritPoints("15-06-2024", 7);
        assertTrue(p.isSuspended);
    }
}
