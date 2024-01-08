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
            System.out.println("No results found");
        } else {
            for (ResponseToUserPetition response : responses) {
                System.out.println("Hotel: " + response.hotel());
                System.out.println("Destination: " + response.island());
                System.out.println("Check-in date: " + response.checkIn());
                System.out.println("Check-out date: " + response.checkOut());
                System.out.println("Final price: " + response.totalRate() + "€");
                System.out.println("Mean temperature: " + response.meanTemp() + "ºC");
                System.out.println("Mean probability of precipitation: " + response.meanRain());
                System.out.println("Mean humidity: " + response.meanHumidity() + "%");
                System.out.println("Mean clouds: " + response.meanClouds() + "%");
                System.out.println("Mean wind speed: " + response.meanWindSpeed() + "m/s");
                System.out.println();
            }
        }
    }

}