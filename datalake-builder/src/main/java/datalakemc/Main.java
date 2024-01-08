package datalakemc;

import datalakemc.control.DatalakeBuilder;

public class Main {
    public static void main(String[] args) {

        DatalakeBuilder weatherBuilder = new DatalakeBuilder(args[0], args[1], args[2], args[4], args[5]);
        weatherBuilder.start();

        DatalakeBuilder hotelBuilder = new DatalakeBuilder(args[0], args[1], args[3], args[6], args[7]);
        hotelBuilder.start();
    }
}
