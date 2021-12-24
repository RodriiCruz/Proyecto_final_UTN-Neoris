package ar.edu.utn.buscador.servicios;

import ar.edu.utn.buscador.entidades.SitioDeInteres;
import ar.edu.utn.buscador.entidades.Turista;
import ar.edu.utn.buscador.utilidades.OrdenarPorDistancia;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
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
    final Double distanciaMaxima = 100.00;

    private ServicioTurista st;
    private ServicioSitio ss;
    private List<Turista> turistas;
    private List<SitioDeInteres> sitios;
    private BufferedReader turistaCsv;
    private BufferedReader sitiosCsv;
    private Path output;
    private String eleccion;

    public ServicioBuscador() {
        this.st = new ServicioTurista();
        this.ss = new ServicioSitio();
        this.turistas = new ArrayList();
        this.sitios = new ArrayList();
    }

    public ServicioBuscador(Path csvTuristas, Path csvSitios, Path output, String eleccion) throws IOException {
        this();
        this.turistaCsv = Files.newBufferedReader(csvTuristas, Charset.forName("ISO-8859-1"));
        this.sitiosCsv = Files.newBufferedReader(csvSitios, Charset.forName("ISO-8859-1"));
        this.output = output;
        this.eleccion = eleccion;
        this.turistas = st.turistasAObjetos(this.turistaCsv);
        this.sitios = ss.sitiosAObjetos(this.sitiosCsv);
    }

    public void buscarSitios() throws IOException {
//
//        double latA, longA, latB, longB;
//
//        for (Turista turista : this.turistas) {
//            latA = turista.getLatitud();
//            longA = turista.getLongitud();
//
//            for (SitioDeInteres sitio : this.sitios) {
//
//                latB = sitio.getLatitud();
//                longB = sitio.getLongitud();
//
//                //sitio.setDistanciaEnKm(this.distanciaAlSitio(latA, longA, latB, longB));
//                for (String sitioInteresante : turista.getIntereses()) {
//                    if ((sitioInteresante.equals(sitio.getCategoria())) && (sitio.estaAbierto(turista.getHoraDeConsulta()))) {
//                        sitio.setDistanciaEnKm(this.distanciaAlSitio(latA, longA, latB, longB));//ESTABA FUERA DEL FOR
//
//                        if (sitio.getDistanciaEnKm() <= this.distanciaMaxima) {
//                            turista.agregarSitioPorVisitar(sitio);
//                            System.out.println("categoria: " + sitio.getCategoria());
//                        }
//                    }
//                }
//            }
//
//            if (!turista.getSitiosPorVisitar().isEmpty()) {
//                Collections.sort(turista.getSitiosPorVisitar(), new OrdenarPorDistancia());
//                log.info("Se han cargado " + turista.getSitiosPorVisitar().size() + " sitio(s) de interés para " + turista.getNombre() + " " + turista.getApellido());
//            } else {
//                log.error("No se han encontrado sitios de interés cercanos al turista " + turista.getNombre() + " " + turista.getApellido());
//            }
//        }
    }

    public void buscarNSitios() throws IOException, NumberFormatException {

        int cantidadSitios = Math.abs(Integer.parseInt(this.eleccion)); //Ignoramos el signo

        double latA, longA, latB, longB;

        for (Turista turista : this.turistas) {
            latA = turista.getLatitud();
            longA = turista.getLongitud();

            for (SitioDeInteres sitio : this.sitios) {

                latB = sitio.getLatitud();
                longB = sitio.getLongitud();

                for (String sitioInteresante : turista.getIntereses()) {
                    if ((sitioInteresante.equals(sitio.getCategoria())) && (sitio.estaAbierto(turista.getHoraDeConsulta()))) {
                        sitio.setDistanciaEnKm(this.distanciaAlSitio(latA, longA, latB, longB));

                        if (sitio.getDistanciaEnKm() <= this.distanciaMaxima) {
                            turista.agregarSitioPorVisitar(sitio);
                        }
                    }
                }
            }

            if (!turista.getSitiosPorVisitar().isEmpty()) {
                Collections.sort(turista.getSitiosPorVisitar(), new OrdenarPorDistancia()); //Ordena la coleccion por cercania
                List listaReducida = new ArrayList(turista.getSitiosPorVisitar().subList(0, cantidadSitios)); //crea una lista con la cantidad de sitios especificados
                turista.setSitiosPorVisitar(listaReducida); //setea la nueva coleccion al turista
                
                log.info("Se han cargado " + turista.getSitiosPorVisitar().size() + " sitio(s) de interés para " + turista.getNombre() + " " + turista.getApellido());
            } else {
                log.error("No se han encontrado sitios de interés cercanos al turista " + turista.getNombre() + " " + turista.getApellido());
            }
        }
    }

    public void escribirTuristaJson() throws IOException {
        String json = "";

        for (Turista turista : this.turistas) {
            ObjectMapper objectMapper = new ObjectMapper();

            log.debug("Tratando de serializar turista: " + turista.getNombre() + " " + turista.getApellido());
            json = objectMapper.writeValueAsString(turista);

            log.debug("Tratando de escribir turista: " + turista.getNombre() + " " + turista.getApellido());
            Files.write(this.output, (String.join(",", json) + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);

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
        return Math.round(distKm * 100) / 100d; //Redondea a dos decimales
    }
}
