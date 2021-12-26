package ar.edu.utn.buscador.servicios;

import org.junit.Test;
import static org.junit.Assert.*;
import ar.edu.utn.buscador.servicios.ServicioBuscador;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;

public class ServicioBuscadorTest {

    private ServicioBuscador servi;

    public ServicioBuscadorTest() {
    }

    @Before
    public void setUp() {
        try {

            Path csvTuristas = FileSystems.getDefault().getPath("proyecto-final-master(2)", "Turistas.csv");
            Path csvSitios = FileSystems.getDefault().getPath("proyecto-final-master(2)", "Sitios.csv");
            Path output = FileSystems.getDefault().getPath("proyecto-final-master(2)", "output.txt");

            String eleccion = "intereses";
            BufferedReader brTuristas = Files.newBufferedReader(csvTuristas, Charset.forName("ISO-8859-1"));
            BufferedReader brSitios = Files.newBufferedReader(csvSitios, Charset.forName("ISO-8859-1"));

            Files.deleteIfExists(output);
            Files.createFile(output);
            
            servi = new ServicioBuscador(brTuristas, brSitios, output, eleccion);
        } catch (IOException ex) {
            Logger.getLogger(ServicioBuscadorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testBuscarSitios() throws IOException
    {
        servi.buscarSitios();
    }

}
