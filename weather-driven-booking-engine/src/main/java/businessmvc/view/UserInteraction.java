package businessmvc.view;

import businessmvc.model.*;

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
        System.out.println("Enter the date of the check-in in this format, YYYY-MM-DD:");
        checkIn = scanner.nextLine();
        System.out.println("Enter the date of the check-out in this format, YYYY-MM-DD:");
        checkOut = scanner.nextLine();
        System.out.println("Searching for results...\n");
    }

    public void printResponses(List<ResponseToUserPetition> responses) {
        if (responses.isEmpty()) {
            System.out.println("No results found.");
        } else {
            for (ResponseToUserPetition response : responses) {
                System.out.printf("Hotel: %s%nDestination: %s%nCheck-in date: %s%nCheck-out date: " +
                                "%s%nFinal price: %.2f€%nMean temperature: %.2fºC%nMean " +
                                "probability of precipitation: %.2f%nMean " +
                                "humidity: %.2f%%%nMean clouds: %.2f%%%nMean wind speed: %.2fm/s%n%n",
                        response.hotel(), response.island(), response.checkIn(), response.checkOut(),
                        response.totalRate(), response.meanTemp(), response.meanRain(), response.meanHumidity(),
                        response.meanClouds(), response.meanWindSpeed());
            }
        }
    }

}