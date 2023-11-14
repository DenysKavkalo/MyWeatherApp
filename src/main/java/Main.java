import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Welcome to the WeatherApp!\nPlease, enter the name of the API key file (.txt): ");
        String apiKeyLocation = scanner.nextLine();

        System.out.print("Please, enter the name of the locations file (.tsv): ");
        String locationsFileLocation = scanner.nextLine();

        scanner.close();

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}