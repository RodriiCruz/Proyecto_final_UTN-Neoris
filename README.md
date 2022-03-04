# Proyecto final UTN - Neoris

Proyecto final presentado en el curso "Desarrollador Java" impartido por la UTN, en colaboración con Neoris.
Es un programa que simula una busqueda de sitios de interes disponibles en un radio de 100 km a la ubicación de la persona que consulta en un determinado horario y, en base a ello, genera un archivo de salida con las coincidencias encontradas.

Obtiene estos datos de los siguientes archivos .csv:

**- Sitios.csv:** contiene información de los sitios de interés como su nombre, categoría(cine, teatro o galería de arte), dirección, ubicación (latitud y longitud), horario de apertura y cierre, entre otros datos.

**- Turistas.csv:** contiene información de la persona interesada en obtener los sitios más cercanos a su ubicación (latitud y longitud), como también sus gustos (cine, teatro, o galeria de arte)

En base a estos, se escribe en la salida los sitios cuya categoria coincida con los gustos de la persona, que se encuentren abiertos a la hora de la consulta y dentro de un radio de 100 km de distancia.

Los argumentos que se deben ingresar a la clase Main son los siguientes:
```
--sitios: path del archivo de sitios.
--turistas: path del archivo de turistas.
--salida: path del archivo que contendrá la información generada por el programa.
--cantidad (opcional): en él se ingresa n cantidad de sitios que se desea encontrar. El valor por defecto es "0", donde se busca y muestra la primera coincidencia de cada categoria de interés de la persona.
```

Este proyecto está realizado con Maven para el manejo de las siguientes dependencias:

_ArgParse4J 0.9.0_

_Jackson_

_Log4J 2.6.1_

_JUnit 4.12_
