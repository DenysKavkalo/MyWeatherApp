package datalakemc;

import datalakemc.control.DatalakeBuilder;

public class Main {
    public static void main(String[] args) {
        String dir0 = args[0];
        String dir1 = args[1];
        String weatherTopic = args[2];
        String hotelTopic = args[3];
        String weatherId = args[4];
        String weatherSubName = args[5];
        String hotelId = args[6];
        String hotelSubName = args[7];

        DatalakeBuilder weatherBuilder = new DatalakeBuilder(dir0, dir1, weatherTopic, weatherId, weatherSubName);
        weatherBuilder.start();

        DatalakeBuilder hotelBuilder = new DatalakeBuilder(dir0, dir1, hotelTopic, hotelId, hotelSubName);
        hotelBuilder.start();
    }
}
