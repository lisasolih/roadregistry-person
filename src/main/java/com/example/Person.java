package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate; // DD-MM-YYYY
    private final HashMap<String, Integer> demeritPoints = new HashMap<>();
    private boolean isSuspended;

    public Person(String personID, String firstName, String lastName, String address, String birthdate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthdate;
        this.isSuspended = false;
    }

    // --- 1. addPerson() ---
    public boolean addPerson() {
        if (!validatePersonID(personID)) {
            System.out.println("Invalid ID: " + personID);
            return false;
        }
        if (!validateAddress(address)) {
            System.out.println("Invalid address: " + address);
            return false;
        }
        if (!validateBirthdate(birthdate)) {
            System.out.println("Invalid birthdate: " + birthdate);
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("persons.txt", true))) {
            writer.write(personID + "|" + firstName + "|" + lastName + "|" + address + "|" + birthdate + "\n");
            return true;
        } catch (IOException e) {
            System.out.println("File writing error: " + e.getMessage());
            return false;
        }
    }

    // --- 2. updatePersonalDetails() ---
    public boolean updatePersonalDetails(String newID, String newFirst, String newLast, String newAddress, String newBirth) {
        try {
            int age = getAge(this.birthdate);
            if (age < 18 && !this.address.equals(newAddress)) return false;
            if (!this.birthdate.equals(newBirth)) {
                if (!this.personID.equals(newID) || !this.firstName.equals(newFirst) || !this.lastName.equals(newLast) || !this.address.equals(newAddress)) return false;
            }
            if (isEvenID(this.personID) && !this.personID.equals(newID)) return false;
            if (!validatePersonID(newID) || !validateAddress(newAddress) || !validateBirthdate(newBirth)) return false;

            // Update in file
            updateFile(this.personID, newID, newFirst, newLast, newAddress, newBirth);
            this.personID = newID;
            this.firstName = newFirst;
            this.lastName = newLast;
            this.address = newAddress;
            this.birthdate = newBirth;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // --- 3. addDemeritPoints() ---
    public String addDemeritPoints(String offenseDate, int points) {
        if (!validateDate(offenseDate) || points < 1 || points > 6) return "Failed";
        demeritPoints.put(offenseDate, points);

        int age = getAge(birthdate);
        int totalPoints = calculatePointsLast2Years(offenseDate);
        if ((age < 21 && totalPoints >= 6) || (age >= 21 && totalPoints >= 12)) {
            isSuspended = true;
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("demerits.txt", true))) {
            writer.write(personID + "|" + offenseDate + "|" + points + "\n");
            return "Success";
        } catch (IOException e) {
            return "Failed";
        }
    }

    // --- Helper Functions ---
    private boolean validatePersonID(String id) {
        if (id.length() != 10) return false;

        String firstTwo = id.substring(0, 2);
        String middle = id.substring(2, 8);
        String lastTwo = id.substring(8, 10);

        if (!firstTwo.matches("[2-9][0-9]")) return false;
        if (!lastTwo.matches("[A-Z]{2}")) return false;

        int specialCount = 0;
        for (char c : middle.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) specialCount++;
        }
        return specialCount >= 2;
    }

    private boolean validateAddress(String addr) {
        String[] parts = addr.split("\\|");
        if (parts.length != 5) {
            System.out.println("Invalid address: incorrect number of parts (" + parts.length + ")");
            return false;
        }
        if (!parts[3].trim().equalsIgnoreCase("Victoria")) {
            System.out.println("Invalid address: state is not Victoria (" + parts[3].trim() + ")");
            return false;
        }
        return true;
    }

    private boolean validateBirthdate(String date) {
        return date.matches("\\d{2}-\\d{2}-\\d{4}");
    }

    private boolean validateDate(String date) {
        return validateBirthdate(date);
    }

    private int getAge(String birth) {
        try {
            Date birthDate = new SimpleDateFormat("dd-MM-yyyy").parse(birth);
            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(birthDate);
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) age--;
            return age;
        } catch (ParseException e) {
            return 0;
        }
    }

    private boolean isEvenID(String id) {
        return Character.getNumericValue(id.charAt(0)) % 2 == 0;
    }

    private int calculatePointsLast2Years(String offenseDate) {
        try {
            Date cutoff = new SimpleDateFormat("dd-MM-yyyy").parse(offenseDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(cutoff);
            cal.add(Calendar.YEAR, -2);
            Date start = cal.getTime();

            int total = 0;
            for (Map.Entry<String, Integer> entry : demeritPoints.entrySet()) {
                Date d = new SimpleDateFormat("dd-MM-yyyy").parse(entry.getKey());
                if (d.after(start) && d.before(cutoff) || d.equals(cutoff)) total += entry.getValue();
            }
            return total;
        } catch (ParseException e) {
            return 0;
        }
    }

    private void updateFile(String oldID, String newID, String newFirst, String newLast, String newAddress, String newBirth) throws IOException {
        File inputFile = new File("persons.txt");
        File tempFile = new File("temp.txt");
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts[0].equals(oldID)) {
                writer.write(newID + "|" + newFirst + "|" + newLast + "|" + newAddress + "|" + newBirth + "\n");
            } else {
                writer.write(line + "\n");
            }
        }
        reader.close();
        writer.close();
        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    // ✅ Public getter so test can access isSuspended
    public boolean isSuspended() {
        return isSuspended;
    }

    // --- Main Method for Quick Testing ---
    public static void main(String[] args) {
        Person p = new Person("56s_d%&fAB", "Lisa", "Solih", "12|King St|Melbourne|Victoria|Australia", "15-09-2002");
        boolean added = p.addPerson();
        System.out.println("Person added: " + added);
    }
}
