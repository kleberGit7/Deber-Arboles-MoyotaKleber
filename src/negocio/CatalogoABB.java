package negocio;

import enums.TipoRecorrido;
import java.util.ArrayList;
import java.util.List;
import modelo.Libro;

public class CatalogoABB {
    private NodoLibro raiz;

    public NodoLibro getRaiz() { return raiz; }
    public void setRaiz(NodoLibro raiz) { this.raiz = raiz; }

    public boolean insertar(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("El libro no puede ser null.");
        }
        if (buscar(libro.getCodigo()) != null) {
            return false;
        }
        raiz = insertar(raiz, libro);
        return true;
    }

    private NodoLibro insertar(NodoLibro nodo, Libro libro) {
        if (nodo == null) {
            return new NodoLibro(libro);
        }
        if (libro.getCodigo() < nodo.getDato().getCodigo()) {
            nodo.setIzquierdo(insertar(nodo.getIzquierdo(), libro));
        } else {
            nodo.setDerecho(insertar(nodo.getDerecho(), libro));
        }
        return nodo;
    }

    public Libro buscar(int codigo) { return buscar(raiz, codigo); }

    private Libro buscar(NodoLibro nodo, int codigo) {
        if (nodo == null) {
            return null;
        }
        if (codigo == nodo.getDato().getCodigo()) {
            return nodo.getDato();
        }
        return codigo < nodo.getDato().getCodigo()
                ? buscar(nodo.getIzquierdo(), codigo) : buscar(nodo.getDerecho(), codigo);
    }

    public int contar() { return contar(raiz); }

    private int contar(NodoLibro nodo) {
        if (nodo == null) {
            return 0;
        }
        return 1 + contar(nodo.getIzquierdo()) + contar(nodo.getDerecho());
    }

    public List<Libro> recorrer(TipoRecorrido tipo) {
        validarTipo(tipo);
        List<Libro> resultado = new ArrayList<>();
        recorrer(raiz, tipo, resultado, null, "");
        return resultado;
    }

    public String trazaRecorrido(TipoRecorrido tipo) {
        validarTipo(tipo);
        List<Libro> resultado = new ArrayList<>();
        StringBuilder traza = new StringBuilder();
        recorrer(raiz, tipo, resultado, traza, "");
        traza.append("Secuencia ").append(tipo).append(":");
        escribirSecuencia(resultado, 0, traza);
        return traza.append('\n').toString();
    }

    private void validarTipo(TipoRecorrido tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Seleccione un tipo de recorrido.");
        }
    }

    private void recorrer(NodoLibro nodo, TipoRecorrido tipo, List<Libro> resultado,
            StringBuilder traza, String sangria) {
        if (nodo == null) {
            if (traza != null) {
                traza.append(sangria).append("null -> regresa\n");
            }
            return;
        }
        if (traza != null) {
            traza.append(sangria).append("entra ").append(nodo.getDato().getCodigo()).append('\n');
        }
        if (tipo == TipoRecorrido.PREORDEN) {
            visitar(nodo, resultado, traza, sangria);
        }
        String sangriaHijos = traza == null ? "" : sangria + "  ";
        recorrer(nodo.getIzquierdo(), tipo, resultado, traza, sangriaHijos);
        if (tipo == TipoRecorrido.INORDEN) {
            visitar(nodo, resultado, traza, sangria);
        }
        recorrer(nodo.getDerecho(), tipo, resultado, traza, sangriaHijos);
        if (tipo == TipoRecorrido.POSORDEN) {
            visitar(nodo, resultado, traza, sangria);
        }
    }

    private void visitar(NodoLibro nodo, List<Libro> resultado, StringBuilder traza, String sangria) {
        resultado.add(nodo.getDato());
        if (traza != null) {
            traza.append(sangria).append("VISITA ").append(nodo.getDato().getCodigo()).append('\n');
        }
    }

    private void escribirSecuencia(List<Libro> libros, int indice, StringBuilder salida) {
        if (indice == libros.size()) {
            return;
        }
        salida.append(' ').append(libros.get(indice).getCodigo());
        escribirSecuencia(libros, indice + 1, salida);
    }

    public boolean modificar(int codigo, String titulo, String autor, int anio) {
        Libro libro = buscar(codigo);
        if (libro == null) {
            return false;
        }
        // Valida todos los datos antes de modificar el libro existente.
        Libro validado = new Libro(codigo, titulo, autor, anio);
        libro.setTitulo(validado.getTitulo());
        libro.setAutor(validado.getAutor());
        libro.setAnio(validado.getAnio());
        return true;
    }

    public boolean eliminar(int codigo) {
        if (buscar(codigo) == null) {
            return false;
        }
        raiz = eliminar(raiz, codigo);
        return true;
    }

    private NodoLibro eliminar(NodoLibro nodo, int codigo) {
        if (nodo == null) {
            return null;
        }
        if (codigo < nodo.getDato().getCodigo()) {
            nodo.setIzquierdo(eliminar(nodo.getIzquierdo(), codigo));
        } else if (codigo > nodo.getDato().getCodigo()) {
            nodo.setDerecho(eliminar(nodo.getDerecho(), codigo));
        } else {
            // Estos dos casos incluyen la hoja y el nodo con un solo hijo.
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            }
            if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }
            NodoLibro sucesor = minimo(nodo.getDerecho());
            nodo.setDato(sucesor.getDato());
            nodo.setDerecho(eliminar(nodo.getDerecho(), sucesor.getDato().getCodigo()));
        }
        return nodo;
    }

    private NodoLibro minimo(NodoLibro nodo) {
        return nodo.getIzquierdo() == null ? nodo : minimo(nodo.getIzquierdo());
    }

    public int contarEnRango(int a, int b) {
        return a > b ? 0 : contarEnRango(raiz, a, b);
    }

    private int contarEnRango(NodoLibro nodo, int a, int b) {
        if (nodo == null) {
            return 0;
        }
        int codigo = nodo.getDato().getCodigo();
        if (codigo < a) {
            return contarEnRango(nodo.getDerecho(), a, b);
        }
        if (codigo > b) {
            return contarEnRango(nodo.getIzquierdo(), a, b);
        }
        return 1 + (codigo > a ? contarEnRango(nodo.getIzquierdo(), a, b) : 0)
                + (codigo < b ? contarEnRango(nodo.getDerecho(), a, b) : 0);
    }

    public List<Integer> caminoBusqueda(int codigo) {
        List<Integer> camino = new ArrayList<>();
        caminoBusqueda(raiz, codigo, camino);
        return camino;
    }

    private void caminoBusqueda(NodoLibro nodo, int codigo, List<Integer> camino) {
        if (nodo == null) {
            return;
        }
        int actual = nodo.getDato().getCodigo();
        camino.add(actual);
        if (codigo < actual) {
            caminoBusqueda(nodo.getIzquierdo(), codigo, camino);
        } else if (codigo > actual) {
            caminoBusqueda(nodo.getDerecho(), codigo, camino);
        }
    }

    public boolean esABBValido() {
        return esABBValido(raiz, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean esABBValido(NodoLibro nodo, long minimo, long maximo) {
        if (nodo == null) {
            return true;
        }
        int codigo = nodo.getDato().getCodigo();
        return codigo > minimo && codigo < maximo
                && esABBValido(nodo.getIzquierdo(), minimo, codigo)
                && esABBValido(nodo.getDerecho(), codigo, maximo);
    }

    // Extra: consume el preorden una sola vez; no llama a insertar().
    public static CatalogoABB desdePreorden(int[] preorden) {
        if (preorden == null) {
            throw new IllegalArgumentException("El preorden no puede ser null.");
        }
        CatalogoABB catalogo = new CatalogoABB();
        int[] indice = {0};
        catalogo.raiz = construir(preorden, indice, 0L, Long.MAX_VALUE);
        if (indice[0] != preorden.length) {
            throw new IllegalArgumentException("Preorden invalido, codigo repetido o no positivo.");
        }
        return catalogo;
    }

    private static NodoLibro construir(int[] preorden, int[] indice, long minimo, long maximo) {
        if (indice[0] == preorden.length) {
            return null;
        }
        int codigo = preorden[indice[0]];
        if (codigo <= minimo || codigo >= maximo) {
            return null;
        }
        indice[0]++;
        NodoLibro nodo = new NodoLibro(new Libro(codigo, "Libro " + codigo, "Autor de prueba", 2026));
        nodo.setIzquierdo(construir(preorden, indice, minimo, codigo));
        nodo.setDerecho(construir(preorden, indice, codigo, maximo));
        return nodo;
    }
}
