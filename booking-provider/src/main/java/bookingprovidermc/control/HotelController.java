package bookingprovidermc.control;

import bookingprovidermc.model.Booking;
import bookingprovidermc.model.Hotel;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HotelController {
    private final HotelProvider hotelProvider;
    private final HotelStorage hotelStorage;
    private final ArrayList<Hotel> hotels;
    private HotelTask hotelTask;
    private long period;

    public HotelController(HotelProvider hotelProvider, HotelStorage hotelStorage, String hotelFile) {
        this.hotels = loadLocationsFromFile(hotelFile);
        this.hotelProvider = hotelProvider;
        this.hotelStorage = hotelStorage;
    }

    public ArrayList<Hotel> loadLocationsFromFile(String filePath) {
        ArrayList<Hotel> hotelList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");

                if (parts.length >= 3) {
                    String name = parts[0];
                    String island = parts[1];
                    String key = parts[2];
                    Hotel hotel = new Hotel(name, island, key);
                    hotelList.add(hotel);
                } else {
                    System.err.println("La línea no tiene suficientes partes: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hotelList;
    }

    public void configure(int numDays, int hoursPeriodicity) {
        long hoursInMillis = (long) hoursPeriodicity * 60 * 60 * 1000;

        hotelTask = new HotelTask(numDays);
        period = hoursInMillis;
    }

    public void execute() {
        Timer timer = new Timer(true);
        timer.schedule(hotelTask, 0, period);
    }

    private class HotelTask extends TimerTask {
        private final int numDays;

        public HotelTask(int numDays) {
            this.numDays = numDays;
        }

        @Override
        public void run() {

            Instant now = Instant.now();
            LocalTime noon = LocalTime.of(12, 0);

            for (Hotel hotel : hotels) {

                Instant mediodia = now.truncatedTo(ChronoUnit.DAYS).plus(noon.getHour(), ChronoUnit.HOURS);
                int numDaysToAdd = (now.toEpochMilli() < mediodia.toEpochMilli()) ? 0 : 1;

                LocalDate checkIn = LocalDate.now();
                checkIn = checkIn.plusDays(numDaysToAdd);

                for (int i = 0; i < numDays; i++) {
                    LocalDate checkOut = checkIn.plusDays(1);
                    Booking booking = hotelProvider.get(hotel, checkIn.toString(), checkOut.toString());
                    if (booking != null) {
                        hotelStorage.publish(booking);
                    }
                    checkIn = checkOut;
                }
            }
        }
    }
}
