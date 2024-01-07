package businessmvc.model;

public record ResponseToUserPetition(String hotel, String island, String checkIn, String checkOut,
                                     Float totalRate, Float meanTemp, Float meanRain, Integer meanHumidity,
                                     Integer meanClouds, Float meanWindSpeed) {
}
