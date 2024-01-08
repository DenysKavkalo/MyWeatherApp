package businessmvc.view;

import businessmvc.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class UserInteraction {

    private String checkIn;
    private String checkOut;

    public String getCheckIn() {
        return checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void getUserInput() {
        Scanner scanner = new Scanner(System.in);
        boolean validCheckIn = false;
        boolean validCheckOut = false;

        while (!validCheckIn) {
            System.out.println("Enter the date of the check-in in this format, YYYY-MM-DD:");
            checkIn = scanner.nextLine();
            validCheckIn = isValidDate(checkIn);

            if (!validCheckIn) {
                System.out.println("Invalid date format. Please enter a valid date of the check-in.");
            }
        }

        while (!validCheckOut) {
            System.out.println("Enter the date of the check-out in this format, YYYY-MM-DD:");
            checkOut = scanner.nextLine();
            validCheckOut = isValidDate(checkOut);

            if (!validCheckOut) {
                System.out.println("Invalid date format. Please enter a valid date of the check-out.");
            }
        }

        System.out.println("Searching for results...\n");
    }

    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    public void printResponses(List<ResponseToUserPetition> responses) {
        if (responses.isEmpty()) {
            System.out.println("No results found.");
        } else {
            for (ResponseToUserPetition response : responses) {
                System.out.printf("Hotel: %s%nDestination: %s%nCheck-in date: %s%nCheck-out date: " +
                                "%s%nFinal price: %.2feuros%nMean temperature: %.2fºC%nMean " +
                                "probability of precipitation: %.2f%nMean " +
                                "humidity: %.2f%%%nMean clouds: %.2f%%%nMean wind speed: %.2fm/s%n%n",
                        response.hotel(), response.island(), response.checkIn(), response.checkOut(),
                        response.totalRate(), response.meanTemp(), response.meanRain(), response.meanHumidity(),
                        response.meanClouds(), response.meanWindSpeed());
            }
        }
    }

}