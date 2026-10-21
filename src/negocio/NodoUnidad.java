package negocio;

import enums.TipoUnidad;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modelo.UnidadBiblioteca;

public class NodoUnidad {
    private final UnidadBiblioteca dato;
    private final List<NodoUnidad> hijos;
    private final CatalogoABB catalogo;

    public NodoUnidad(UnidadBiblioteca dato) {
        if (dato == null) {
            throw new IllegalArgumentException("La unidad no puede ser null.");
        }
        this.dato = dato;
        this.hijos = new ArrayList<>();
        this.catalogo = dato.getTipo() == TipoUnidad.ESTANTE ? new CatalogoABB() : null;
    }

    public UnidadBiblioteca getDato() { return dato; }
    public CatalogoABB getCatalogo() { return catalogo; }

    public List<NodoUnidad> getHijos() {
        // Permite consultar los hijos sin saltarse las validaciones del arbol.
        return Collections.unmodifiableList(hijos);
    }

    void agregarHijo(NodoUnidad hijo) {
        hijos.add(hijo);
    }
}
