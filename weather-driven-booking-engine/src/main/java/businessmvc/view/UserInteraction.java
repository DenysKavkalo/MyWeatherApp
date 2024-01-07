package businessmvc.view;

import businessmvc.model.*;
import bookingprovidermc.model.Rate;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
        scanner.close();
        System.out.println("Searching for results...");
    }

}