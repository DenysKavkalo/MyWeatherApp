# MyWeatherApp (publisher/subscriber implementation)

**Subject:** Development of Applications for Data Science

**Year:** 3rd

**Degree:** Data Science and Engineering

**School:** School of Computer Engineering

**University:** University of Las Palmas de Gran Canaria

## Summary of Functionality

This project not only implements a weather forecasting system based on specific geographic locations but also incorporates the functionality of the publisher/subscriber pattern. The added capability enables the system to publish meteorological events to the broker's topic, which can be consumed by subscribers. Subsequently, these events are stored in a directory, providing a mechanism for archiving and managing meteorological data.

## How to use

To begin, you will need to create two files:
- A text file with the API key (apikey.txt)
- A file that contains the names of locations along with their coordinates separeted with tabs (locations.tsv)

Create these two files in your "\MyWeatherApp\out\artifacts\prediction_provider_jar" folder.

![howtouse1](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/2e466c89-5b81-414b-9e47-c060a3e05b91)

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

Now, open the Command Prompt and start ActiveMQ.

Next step is running the app. To run the app modules, **open two separate terminals** in the IntelliJ Environment, paste the lines that I'll show you in a moment, and **run the terminals in this specific order**:

- **first** the event-store-builder
- **then** the prediction-provider.

Copy and paste the following lines in the correspondent terminal.

- **Lines for the event-store-builder terminal:**

cd .\out\artifacts\event_store_builder_jar\
java -cp .\event-store-builder.jar eventstoremc.Main prediction.Weather eventstore

- **Lines for the prediction-provider terminal:**

cd .\out\artifacts\prediction_provider_jar\
java -cp .\prediction-provider.jar predictionprovidermc.Main apikey.txt locations.tsv prediction.Weather


**NOTE:** Keep in mind that the Main classes from both modules are using arguments as you can see from the lines above. If you create the .txt and .tsv files with other names, paste those names into the exact arguments that would correspond to the file names that I used. 


The result should be looking like this:

![result](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/9bbf4eab-f225-4eb3-b2b9-4e6c8f256239)

## Resources Used

- **Development Environment:** IntelliJ IDEA
- **Version Control:** Git
- **Google Gson (Version 2.10.1):** Gson is used for handling JSON data.
- **JSoup (Version 1.16.2):** JSoup is employed for HTML parsing.
- **ActiveMQ Client (Version 5.15.12):** Provides the necessary classes and methods for Java applications to interact with an ActiveMQ message broker. Allowing the creation, sending, and receiving of messages in a messaging system.
- **ActiveMQ (Version 6.0.0):** Open-source messaging broker that facilitates communication in distributed systems. Its primary function is to enable asynchronous communication between various components of a distributed system.
- **Logback Classic (Version 1.2.9):** Logback Classic provides robust logging capabilities for the project.

## Design

**Observer Pattern (to some extent):** The use of TimerTask in WeatherTask for executing periodic tasks exhibits behavior similar to the Observer pattern, allowing components to react to changes.

**Use of Records:** The utilization of records for the Location and Weather classes contributes to enhanced code readability and conciseness by providing a concise syntax for immutable data.

**Use of Interfaces:** The use of interfaces (WeatherProvider and WeatherStorage) promotes flexibility and extensibility, as specific implementations can change without affecting the code that uses them.

**Encapsulation:** Implementation details are encapsulated within specific classes, promoting information hiding and making it easier to maintain and evolve each component independently.

![dLPTRzem57tFh_1ZM9F-W6X2fQELsg4M5QO-LYQv1M_6Zlm1YxRzzpbEuZWnKAbllixX-9vxhu-vaGeBfVSq8ao1UCSve5V0QWTYAVY1523GdmYXAxG55LEUOqKuoz2YNIM8C4F1L8OUciKawmYYVr6KKombUYw1dM9D4Ix9TwWoj5A2iAtTad8GtivMTsy_NXRUvjKfmhYXMhWK6_Za](https://github.com/DenysKavkalo/MyWeatherApp/assets/117307592/6fafbbf9-89fa-4f8a-9b46-a0dabf5e15f0)


**Association Relationship:**

- WeatherProvider is associated with Weather: Indicates that a WeatherProvider is associated with the Weather class, suggesting that a WeatherProvider produces or provides instances of the Weather class.

**Composition Relationship:**

- WeatherController has a composition relationship with WeatherTask: Implies that a WeatherController has a composition relationship with WeatherTask, signifying that the WeatherTask instances are part of the WeatherController, and their lifecycle is controlled by it.
Dependency Relationship:

- WeatherController depends on WeatherProvider: Signifies that WeatherController depends on the WeatherProvider interface, indicating reliance on services provided by any class implementing the WeatherProvider interface.

- WeatherController depends on WeatherStorage: Indicates that WeatherController depends on the WeatherStorage interface, suggesting reliance on services provided by any class implementing the WeatherStorage interface.

- WeatherTask depends on Weather: Signifies that WeatherTask depends on the Weather class, implying usage or reliance on functionality provided by Weather instances.

- WeatherController depends on Location: Indicates that WeatherController depends on the Location class, suggesting usage or reliance on the Location class.

**Inheritance and Implementation Relationship:**

- WeatherProvider is implemented by OpenWeatherProvider: Signifies that OpenWeatherProvider implements the WeatherProvider interface, indicating that OpenWeatherProvider is a specific implementation of the WeatherProvider interface, providing concrete functionality for weather data retrieval.

- WeatherStorage is implemented by WeatherPublisher: Indicates that WeatherPublisher implements the WeatherStorage interface, suggesting that WeatherPublisher is a specific implementation of the WeatherStorage interface, providing functionality for publishing weather data.

**Association Relationship (Different Context):**

- Weather is associated with Location: Indicates that the Weather class is associated with the Location class, suggesting that a Weather object contains or references a Location object.
