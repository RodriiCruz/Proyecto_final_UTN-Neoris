package ar.edu.utn.buscador.servicios;

import ar.edu.utn.buscador.entidades.SitioDeInteres;
import ar.edu.utn.buscador.entidades.Turista;
import ar.edu.utn.buscador.utilidades.OrdenarPorDistancia;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicioBuscador {

    final static Logger log = LoggerFactory.getLogger(ServicioBuscador.class);

    private ServicioTurista st;
    private ServicioSitio ss;
    private List<Turista> turistas;
    private List<SitioDeInteres> sitios;

    public ServicioBuscador() {
        this.st = new ServicioTurista();
        this.ss = new ServicioSitio();
        this.turistas = new ArrayList();
        this.sitios = new ArrayList();
    }

    public void buscarSitios(BufferedReader turistaCsv, BufferedReader sitiosCsv) throws IOException {

        this.turistas = st.turistasAObjetos(turistaCsv);
        this.sitios = ss.sitiosAObjetos(sitiosCsv);

        double latA, longA, latB, longB;

        for (Turista turista : turistas) {
            latA = turista.getLatitud();
            longA = turista.getLongitud();

            for (SitioDeInteres sitio : sitios) {

                latB = sitio.getLatitud();
                longB = sitio.getLongitud();

                sitio.setDistancia(this.distanciaAlSitio(latA, longA, latB, longB));

                if (turista.getInteres().equals(sitio.getCategoria()) && sitio.getDistancia() <= 100.00 && sitio.estaAbierto(turista.getHoraDeConsulta())) {
                    turista.agregarSitioPorVisitar(sitio);
                }
            }

            if (!turista.getSitiosPorVisitar().isEmpty()) {
                Collections.sort(turista.getSitiosPorVisitar(), new OrdenarPorDistancia());
                log.info("Se han cargado " + turista.getSitiosPorVisitar().size() + " sitio(s) de interés para " + turista.getNombre() + " " + turista.getApellido());
            } else {
                log.error("No se han encontrado sitios de interés cercanos al turista " + turista.getNombre() + " " + turista.getApellido());
            }
        }
    }

    public void escribirTuristaJson(Path output) throws IOException {
        String json = "";

        for (Turista turista : this.turistas) {
            ObjectMapper objectMapper = new ObjectMapper();

            log.debug("Tratando de serializar turista: " + turista.getNombre() + " " + turista.getApellido());
            json = objectMapper.writeValueAsString(turista);

            log.debug("Tratando de escribir turista: " + turista.getNombre() + " " + turista.getApellido());
            Files.write(output, (String.join(",", json) + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);

            log.info("OK. Turista serializado y escrito en archivo.");
        }
    }

    private Double distanciaAlSitio(double latUno, double longUno, double latDos, double longDos) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(latDos - latUno);
        double dLng = Math.toRadians(longDos - longUno);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(latUno)) * Math.cos(Math.toRadians(latDos));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distMi = earthRadius * c;
        double distKm = distMi / 0.62137;
        return distKm;
    }
}
