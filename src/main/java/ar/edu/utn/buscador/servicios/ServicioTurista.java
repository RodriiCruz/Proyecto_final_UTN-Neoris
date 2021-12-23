package ar.edu.utn.buscador.servicios;

import ar.edu.utn.buscador.entidades.Turista;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicioTurista {

    final static Logger log = LoggerFactory.getLogger(ServicioTurista.class);

    public List<Turista> turistasAObjetos(BufferedReader turistaCsv) throws IOException {
        String lineaCsv = "";
        List<Turista> turistas = new ArrayList();

        while ((lineaCsv = turistaCsv.readLine()) != null) {
            String[] aux = lineaCsv.split(",");

            if (aux[0].equals("Nro_Documento")) {
                continue;
            }

            if (aux.length == 9) {
                Turista turista = new Turista();

                turista.setDni(Long.valueOf(aux[0]));
                turista.setSexo(aux[1].charAt(0));
                turista.setApellido(aux[2]);
                turista.setNombre(aux[3]);
                turista.setInteres(aux[5]);
                turista.setLatitud(Double.valueOf(aux[6]));
                turista.setLongitud(Double.valueOf(aux[7]));
                turista.setHoraDeConsulta(Integer.valueOf(aux[8]));

                turistas.add(turista);
            } else {
                log.error("ERROR. El registro del turista no tiene todos los campos. " + Arrays.toString(aux));
            }
        }
        return turistas;
    }

    public void escribirTuristaJson(Path output, List<Turista> turistas) throws IOException {
        String json = "";

        for (Turista turista : turistas) {
            ObjectMapper objectMapper = new ObjectMapper();

            log.debug("Tratando de serializar turista: " + turista.getNombre() + " " + turista.getApellido());
            json = objectMapper.writeValueAsString(turista);

            log.debug("Tratando de escribir turista: " + turista.getNombre() + " " + turista.getApellido());
            Files.write(output, (String.join(",", json) + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);

            log.info("OK. Turista serializado y escrito en archivo.");
        }
    }
}
