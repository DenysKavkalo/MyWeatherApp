package control;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import predictionprovidermc.model.Weather;


public class EventStorage {
    private static final String BASE_DIR = "eventstore/prediction.Weather";

    public void storeEvent(Weather weather) {
        // Obtener la fecha en formato YYYYMMDD
        Instant timestamp = weather.ts();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);
        String dateStr = formatter.format(timestamp);

        File baseDir = new File(BASE_DIR);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        // Crear la estructura de directorios para el ss específico si no existe
        String ssDir = BASE_DIR + "/" + weather.ss();
        File ssDirectory = new File(ssDir);
        if (!ssDirectory.exists()) {
            ssDirectory.mkdirs();
        }

        // Construir la ruta del archivo
        String filePath = ssDir + "/" + dateStr + ".events";

        // Serializar el evento a JSON
        Gson gson = new Gson();
        String jsonEvent = gson.toJson(weather);

        // Escribir el evento en el archivo
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(jsonEvent + "\n");  // Añadir una nueva línea para cada evento
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
