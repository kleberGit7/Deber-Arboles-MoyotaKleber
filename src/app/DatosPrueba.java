package app;

import enums.TipoUnidad;
import modelo.Libro;
import modelo.UnidadBiblioteca;
import negocio.ArbolBiblioteca;
import negocio.CatalogoABB;

public class DatosPrueba {
    private DatosPrueba() { }

    public static ArbolBiblioteca crearBiblioteca() {
        ArbolBiblioteca biblioteca = new ArbolBiblioteca();
        biblioteca.agregar("BIB", new UnidadBiblioteca("SW", "Software", TipoUnidad.AREA));
        biblioteca.agregar("BIB", new UnidadBiblioteca("TI", "Tecnologias de la Informacion", TipoUnidad.AREA));
        biblioteca.agregar("BIB", new UnidadBiblioteca("TEL", "Telecomunicaciones", TipoUnidad.AREA));
        biblioteca.agregar("SW", new UnidadBiblioteca("SW-E1", "Estante 1 de Software", TipoUnidad.ESTANTE));
        biblioteca.agregar("SW", new UnidadBiblioteca("SW-E2", "Estante 2 de Software", TipoUnidad.ESTANTE));
        biblioteca.agregar("TI", new UnidadBiblioteca("TI-E1", "Estante 1 de TI", TipoUnidad.ESTANTE));
        cargar(biblioteca.buscar("SW-E1").getCatalogo(), new int[] {60, 35, 80, 20, 45, 70, 90, 40, 75}, 0);
        cargar(biblioteca.buscar("SW-E2").getCatalogo(), new int[] {15, 10, 25}, 0);
        cargar(biblioteca.buscar("TI-E1").getCatalogo(), new int[] {50}, 0);
        return biblioteca;
    }

    public static CatalogoABB crearCatalogoSW() {
        CatalogoABB catalogo = new CatalogoABB();
        cargar(catalogo, new int[] {60, 35, 80, 20, 45, 70, 90, 40, 75}, 0);
        return catalogo;
    }

    private static void cargar(CatalogoABB catalogo, int[] codigos, int indice) {
        if (indice == codigos.length) {
            return;
        }
        // El enunciado fija los codigos; titulo, autor y anio son datos de ejemplo.
        catalogo.insertar(new Libro(codigos[indice], "Libro " + codigos[indice], "Autor de prueba", 2026));
        cargar(catalogo, codigos, indice + 1);
    }
}
