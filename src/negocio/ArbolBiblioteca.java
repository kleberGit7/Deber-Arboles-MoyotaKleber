package negocio;

import enums.TipoUnidad;
import java.util.List;
import modelo.UnidadBiblioteca;

public class ArbolBiblioteca {
    private final NodoUnidad raiz;

    public ArbolBiblioteca() {
        this(new UnidadBiblioteca("BIB", "Biblioteca FISEI", TipoUnidad.BIBLIOTECA));
    }

    public ArbolBiblioteca(UnidadBiblioteca biblioteca) {
        if (biblioteca == null || biblioteca.getTipo() != TipoUnidad.BIBLIOTECA) {
            throw new IllegalArgumentException("La raiz debe ser una BIBLIOTECA.");
        }
        raiz = new NodoUnidad(biblioteca);
    }

    public NodoUnidad getRaiz() { return raiz; }

    public boolean agregar(String codigoPadre, UnidadBiblioteca nueva) {
        if (nueva == null || buscar(nueva.getCodigo()) != null) {
            return false;
        }
        NodoUnidad padre = buscar(codigoPadre);
        if (padre == null || !padre.getDato().getTipo().puedeTenerHijo(nueva.getTipo())) {
            return false;
        }
        padre.agregarHijo(new NodoUnidad(nueva));
        return true;
    }

    public NodoUnidad buscar(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }
        return buscar(raiz, codigo.trim());
    }

    private NodoUnidad buscar(NodoUnidad nodo, String codigo) {
        if (nodo.getDato().getCodigo().equalsIgnoreCase(codigo)) {
            return nodo;
        }
        return buscarEnHijos(nodo.getHijos(), 0, codigo);
    }

    private NodoUnidad buscarEnHijos(List<NodoUnidad> hijos, int indice, String codigo) {
        if (indice == hijos.size()) {
            return null;
        }
        NodoUnidad encontrado = buscar(hijos.get(indice), codigo);
        return encontrado != null ? encontrado : buscarEnHijos(hijos, indice + 1, codigo);
    }

    public int altura() { return altura(raiz); }

    private int altura(NodoUnidad nodo) {
        // Sin hijos, el maximo es -1 y la altura de esta hoja es 0.
        return 1 + alturaHijos(nodo.getHijos(), 0);
    }

    private int alturaHijos(List<NodoUnidad> hijos, int indice) {
        if (indice == hijos.size()) {
            return -1;
        }
        return Math.max(altura(hijos.get(indice)), alturaHijos(hijos, indice + 1));
    }

    public int contarHojas() { return contarHojas(raiz); }

    private int contarHojas(NodoUnidad nodo) {
        if (nodo.getHijos().isEmpty()) {
            return 1;
        }
        return contarHojasHijos(nodo.getHijos(), 0);
    }

    private int contarHojasHijos(List<NodoUnidad> hijos, int indice) {
        if (indice == hijos.size()) {
            return 0;
        }
        return contarHojas(hijos.get(indice)) + contarHojasHijos(hijos, indice + 1);
    }

    public int nivel(String codigo) {
        return codigo == null ? -1 : nivel(raiz, codigo.trim(), 0);
    }

    private int nivel(NodoUnidad nodo, String codigo, int profundidad) {
        if (nodo.getDato().getCodigo().equalsIgnoreCase(codigo)) {
            return profundidad;
        }
        return nivelHijos(nodo.getHijos(), 0, codigo, profundidad + 1);
    }

    private int nivelHijos(List<NodoUnidad> hijos, int indice, String codigo, int profundidad) {
        if (indice == hijos.size()) {
            return -1;
        }
        int encontrado = nivel(hijos.get(indice), codigo, profundidad);
        return encontrado >= 0 ? encontrado : nivelHijos(hijos, indice + 1, codigo, profundidad);
    }

    public String mostrarJerarquia() {
        StringBuilder salida = new StringBuilder();
        mostrar(raiz, "", salida);
        return salida.toString();
    }

    private void mostrar(NodoUnidad nodo, String sangria, StringBuilder salida) {
        salida.append(sangria).append(nodo.getDato()).append('\n');
        mostrarHijos(nodo.getHijos(), 0, sangria + "  ", salida);
    }

    private void mostrarHijos(List<NodoUnidad> hijos, int indice, String sangria, StringBuilder salida) {
        if (indice == hijos.size()) {
            return;
        }
        mostrar(hijos.get(indice), sangria, salida);
        mostrarHijos(hijos, indice + 1, sangria, salida);
    }

    public int contarLibrosTotal() { return contarLibrosTotal(raiz); }

    private int contarLibrosTotal(NodoUnidad nodo) {
        int propios = nodo.getCatalogo() == null ? 0 : nodo.getCatalogo().contar();
        return propios + contarLibrosHijos(nodo.getHijos(), 0);
    }

    private int contarLibrosHijos(List<NodoUnidad> hijos, int indice) {
        if (indice == hijos.size()) {
            return 0;
        }
        return contarLibrosTotal(hijos.get(indice)) + contarLibrosHijos(hijos, indice + 1);
    }
}
