# MyWeatherApp

**Subject:** Development of Applications for Data Science

**Year:** 3rd

**Degree:** Data Science and Engineering

**School:** School of Computer Engineering

**University:** University of Las Palmas de Gran Canaria

## Summary of Functionality

This project implements a weather forecasting system that uses data from specific locations based on latitude and longitude to provide accurate meteorological information. The system runs periodically every 6 hours and stores the results in a SQLite database.

## How to use

To begin, you will need to create two files named as you want:
- A text file with the API key (.txt)
- A file that contains the names of locations along with their coordinates separeted with tabs (.tsv)

Create these two files in your "\MyWeatherApp\src\main\java\" folder.

![locations_creation](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/51289904-48bb-4697-a1f6-07752879192d)

Also, it's advisable to install the CSV Editor plugin from Martin Sommer in order to edit the file directly on the environment. It supports tsv files too.

![TSV Editor](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/65e4e3cc-cbd2-4585-aa3c-50ded41c15d2)

Here's the latitude and longitude from the eight islands that you can paste into your tsv file:

Fuerteventura	28.418821	-13.988788

LaGraciosa	29.257304	-13.509626

Lanzarote	29.054609	-13.678312

GranCanaria	27.849489	-15.657668

Tenerife	28.120999	-16.731369

LaGomera	28.062241	-17.236938

LaPalma	28.603362	-17.781484

ElHierro	27.706905	-17.977953

Check if your tsv file looks like this, the three columns must be separeted by tabs and each row has a newline:

![locations_created](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/1a02529c-99a9-4ba3-a0c0-5c72783a4c61)

Now you need to set up your Working directory to the **java** folder (...\MyWeatherApp\src\main\java), where the .tsv and .txt files are located. Select the Main class as the Build and run class.

![Directory](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/99dda971-6ee1-4b5b-ac51-8ec7d0373f6d)

The folder should be looking like this:

![result](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/b74b70ec-3331-43ba-8a86-a25d039809f3)

Now, run the main method on the Main class. Type the name of your API key file with the extension then press enter. Repeat the process with the .tsv file. Here's how the run console looks like:

![mainmethod](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/468e3a2d-0ad9-48b8-83a0-d5da32e5ee78)

The data base should be created in the **java** folder, and you can open it from that folder with DB Browser for SQLite:

![databasecreated](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/8f9fe017-e4b4-4bff-8055-14561d5a9f24)

## Resources Used

- **Development Environment:** IntelliJ IDEA
- **Version Control:** Git
- **Google Gson (Version 2.10.1):** Gson is used for handling JSON data.
- **JSoup (Version 1.16.2):** JSoup is employed for HTML parsing.
- **SQLite JDBC (Version 3.43.2.2):** the SQLite JDBC driver is utilized to establish a connection with SQLite databases.
- **Logback Classic (Version 1.2.9):** Logback Classic provides robust logging capabilities for the project.

## Design

**Single Responsibility Principle (SRP):** for instance, `OpenWeatherProvider` specifically handles providing weather data from OpenWeatherMap. `SQLiteWeatherStorage` focuses on storing weather data in a SQLite database. 

**Dependency Injection:** it is used in `WeatherController` where dependencies (`weatherProvider` and `weatherStorage`) are passed to the constructor instead of being created internally. This makes `WeatherController` more flexible and easier to test.

**Observer Pattern (to some extent):** although not a classic Observer pattern, the use of the `TimerTask` class in `WeatherTask` to execute periodic tasks could be considered a way to implement behavior similar to the Observer pattern.

**Use of Records:** the utilization of records for the `Location` and `Weather` classes contributes to enhanced code readability and conciseness.

**Use of Interfaces:** the use of interfaces (`WeatherProvider` and `WeatherStorage`) allows for greater flexibility and extensibility, as specific implementations can change without affecting the code that uses them.

![Diagrama de clases](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/4bb6a568-d2bf-47c1-9915-61468474dd52)

**Association Relationship:**

- Main is associated with WeatherController, indicating a general connection between these classes.

**Composition Relationship:**

- WeatherController has a composition relationship with WeatherTask. This implies a stronger connection, where the lifecycle of WeatherTask is controlled by WeatherController.

**Dependency Relationship:**

- WeatherController has a dependency on WeatherProvider, indicating that it relies on the WeatherProvider interface. This dependency is also present with WeatherStorage, suggesting that WeatherController depends on the WeatherStorage interface.

- WeatherTask has a dependency on Weather, signifying that it relies on the Weather class.

- WeatherController also has a dependency on Location, indicating that it relies on the Location class.

**Inheritance and Implementation Relationship:**

- OpenWeatherProvider implements the WeatherProvider interface, implying an inheritance and implementation relationship. It signifies that OpenWeatherProvider is a specific implementation of the WeatherProvider interface.

- Similarly, SQLiteWeatherStorage implements the WeatherStorage interface, showcasing an inheritance and implementation relationship. It indicates that SQLiteWeatherStorage is a specific implementation of the WeatherStorage interface.

**Association Relationship (Different Context):**

- Weather is associated with Location, suggesting that Weather contains an object of type Location. This association is different from the one mentioned earlier, involving Main and WeatherController.
