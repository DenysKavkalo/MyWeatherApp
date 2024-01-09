  # MyWeatherApp (Weather Driven Booking Engine)
  
  **Subject:** Development of Applications for Data Science
  
  **Year:** 3rd
  
  **Degree:** Data Science and Engineering
  
  **School:** School of Computer Engineering
  
  **University:** University of Las Palmas de Gran Canaria
  
  ## Summary of functionality
  
  This project consists of a system for collecting meteorological forecast data from the OpenWeatherMap API and combine it with hotel reservations from the Xotelo API. It allows the user to search reservation prices in real time based on a range of dates (check-in and check-out) and temperatures that suit their preferences. Additionally, it includes a module that logs all events from the data collection process and saves the events in different directories based on their characteristics.
  
  ## How to use
  
  To begin, you will need three files:
  - A text file with the API key (apikey.txt) (this one must be created manually, the other files are provided by me) 
  - A file that contains the names of locations along with their coordinates separeted with tabs (locations.tsv)
  - A file that contains the hotels (hotels.tsv)
  
  **You can use the locations.tsv and hotels.tsv files inside the /out/artifacts/ folder from the project**
  
  ![ubicaciones](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/e884d32f-eab8-48b2-b589-be3c95f8e51f)
  
  Create 4 folders for each app, and place locations.tsv and apikey.txt inside the prediction provider folder. And the hotels.tsv inside the booking provider folder.
  
  That way, you can run 4 Command Prompts and locate your .jar files there.
  
  Now run ActiveMQ, then run the apps in different CLI in the following order:
  
  - Datalake builder
  - Weather driven booking engine
  - Prediction provider
  - Booking provider
  
  cd C:\Users\Usuario\Desktop\dacd\datalake_builder_jar
  
  java -jar datalake-builder.jar datalake eventstore prediction.Weather booking.Hotel weather01 weatherSub01 hotel01 hotelSub01
  
  
  
  cd C:\Users\Usuario\Desktop\dacd\weather_driven_booking_engine_jar
  
  java -jar weather-driven-booking-engine.jar prediction.Weather booking.Hotel
  
  
  
  cd C:\Users\Usuario\Desktop\dacd\prediction_provider_jar
  
  java -jar prediction-provider.jar apikey.txt locations.tsv prediction.Weather
  
  
  cd C:\Users\Usuario\Desktop\dacd\booking_provider_jar
  
  java -jar booking-provider.jar hotels.tsv booking.Hotel
  
  
  **You can use the arguments above to run the .jar files.**
  
  In the Weather driven booking engine, you can type the check-in/chech-out dates and the minimum and maximum temperature that you want for the destination:
  
  ![temp](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/a4871c22-2bd2-4d67-9785-88cc8f438fd3)
  
  If there are results for the specific conditions, the app will show them to you. Keep in mind that it takes some time to get the booking data!:
  
  ![image](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/58099d0d-d569-4523-98dc-80f3743d3326)
  
  While the apps are running, all the messages that are consumed by the durable subscribers from the Data Lake Builder are placed in their respective folders.
  
  **Important: Keep in mind that I don't use the events from the Data Lake because the provider modules are working each 6 hours (though the frequency can be simply changed) and they provide recent data which will most likely be changed because of the forecast system that updates the meteorological measurements, and the prices of bookings that even disappear because the available rooms in the hotels are sold out. So I considered to only store events.**
  
  ![image](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/1cf91860-7a6b-437f-b161-aabb5cc2df72)
  
  ## Resources used
  
  - **Development Environment:** IntelliJ IDEA
  - **Version Control:** Git
  - **Google Gson (Version 2.10.1):** Gson is used for handling JSON data.
  - **JMS (Version 2.0.1):** Provides a common interface for Java applications to send and receive messages asynchronously
  - **JSoup (Version 1.16.2):** JSoup is employed for HTML parsing.
  - **ActiveMQ Client (Version 5.18.3):** Provides the necessary classes and methods for Java applications to interact with an ActiveMQ message broker. Allowing the creation, sending, and receiving of messages in a messaging system.
  - **ActiveMQ (Version 6.0.0):** Open-source messaging broker that facilitates communication in distributed systems. Its primary function is to enable asynchronous communication between various components of a distributed system.
  - **Logback Classic (Version 1.4.12):** Logback Classic provides robust logging capabilities for the project.
  
  ## Design
  
  **Observer Pattern (to some extent):** The use of TimerTask in WeatherTask for executing periodic tasks exhibits behavior similar to the Observer pattern, allowing components to react to changes.
  
  **Use of Records:** The utilization of records for the Location and Weather classes contributes to enhanced code readability and conciseness by providing a concise syntax for immutable data.
  
  **Use of Interfaces:** The use of interfaces (WeatherProvider and WeatherStorage) promotes flexibility and extensibility, as specific implementations can change without affecting the code that uses them.
  
  **Encapsulation:** Implementation details are encapsulated within specific classes, promoting information hiding and making it easier to maintain and evolve each component independently.


**Single Responsibility Principle (SRP):**

The BusinessController class handles business logic, such as processing weather and booking data, generating responses, and storing data.
The DataSubscriber class manages the subscription to weather and booking data, processing this data for use by BusinessController.
The UserInteraction class is responsible for user interaction, collecting user input, and displaying results.

**Open/Closed Principle (OCP):**

The design allows for extending the system with new functionalities without modifying existing code. For example, you can add new data classes without changing existing classes.

**Dependency Inversion Principle (DIP):**

The DataSubscriber class depends on interfaces (BusinessController, CountDownLatch), enabling dependency inversion and facilitating the substitution of concrete implementations.

**Encapsulation:**

Classes encapsulate their internal functionality and expose only what is necessary through public methods and properties.

**Composition over Inheritance:**

Instead of heavily relying on inheritance, the code utilizes composition, as seen in the relationship between BusinessController and DataSubscriber.
  
  ![ZLHTRziW57tth-2HBKb_W5L5RRT6QvKrKRD9awPfmlPjme8XWkikqRR_Fi3W13lHzcB3lUSUxiU16uDCOrEBZ4i4_S9A81yKWbXg](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/f5399631-5703-4ebc-a9dc-933fc366ea95)
![VLHDJyCm3BttL-IOQfq4rnxGO6DeI0EaJUA0E6IjsJBQ94gy0KByTzoFTMswhPSczlFpUsxIaKDXSPTdGP89vzXSICL-0qRFWEMquGkqKgtVtfcmQnV6RAzb6lm5DNuYK6JY4-vsCal1rgL3baeB2MhxSnMpYi5FN7i9r4OcJo87Bvva4XJEKY_iTYkNM6bGQjMh6lYwU5zFT49A (1)](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/35b3fa2c-5892-4ad5-a4a6-961c81f1e432)
![image](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/52330ccd-2607-474c-966e-11375342045c)
  ![image](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/2d86d195-6cdb-4b45-9341-ea971450e852)
