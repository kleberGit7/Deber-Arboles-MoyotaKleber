package app;

import enums.TipoRecorrido;
import enums.TipoUnidad;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import modelo.Libro;
import modelo.UnidadBiblioteca;
import negocio.ArbolBiblioteca;
import negocio.CatalogoABB;
import negocio.NodoLibro;

public class PruebasBiblioteca {
    private static int total;
    private static int aprobadas;

    public static void main(String[] args) {
        total = 0;
        aprobadas = 0;
        System.out.println("PRUEBAS BIBLIOTECA FISEI");

        prueba("Raiz unica: altura 0, una hoja y cero libros", () -> {
            ArbolBiblioteca b = new ArbolBiblioteca();
            igual(0, b.altura()); igual(1, b.contarHojas()); igual(0, b.contarLibrosTotal());
        });
        prueba("Biblioteca inicial: altura 2, cuatro hojas y trece libros", () -> {
            ArbolBiblioteca b = DatosPrueba.crearBiblioteca();
            igual(2, b.altura()); igual(4, b.contarHojas()); igual(13, b.contarLibrosTotal());
        });
        prueba("Niveles BIB, sw, SW-E2 y XYZ: 0, 1, 2 y -1", () -> {
            ArbolBiblioteca b = DatosPrueba.crearBiblioteca();
            igual(0, b.nivel("BIB")); igual(1, b.nivel("sw"));
            igual(2, b.nivel("SW-E2")); igual(-1, b.nivel("XYZ"));
        });
        prueba("TEL existe, es hoja y no tiene catalogo", () -> {
            ArbolBiblioteca b = DatosPrueba.crearBiblioteca();
            igual(TipoUnidad.AREA, b.buscar("TEL").getDato().getTipo());
            igual(true, b.buscar("TEL").getHijos().isEmpty());
            igual(null, b.buscar("TEL").getCatalogo());
            igual(null, b.buscar("BIB").getCatalogo());
        });
        prueba("Busqueda de unidad sin distinguir mayusculas", () -> {
            ArbolBiblioteca b = DatosPrueba.crearBiblioteca();
            igual("SW-E2", b.buscar(" sw-e2 ").getDato().getCodigo());
            igual(null, b.buscar("XYZ")); igual(null, b.buscar(null));
        });
        prueba("ESTANTE directamente bajo BIBLIOTECA: false", () -> {
            ArbolBiblioteca b = DatosPrueba.crearBiblioteca();
            igual(false, b.agregar("BIB", unidad("E", TipoUnidad.ESTANTE)));
            igual(null, b.buscar("E"));
        });
        prueba("Codigo de unidad repetido: false", () -> {
            igual(false, DatosPrueba.crearBiblioteca().agregar("BIB", unidad("sw", TipoUnidad.AREA)));
        });
        prueba("Padre inexistente y unidad null: false", () -> {
            ArbolBiblioteca b = new ArbolBiblioteca();
            igual(false, b.agregar("XYZ", unidad("SW", TipoUnidad.AREA)));
            igual(false, b.agregar("BIB", null));
        });
        prueba("Un estante no puede recibir hijos", () -> {
            igual(false, DatosPrueba.crearBiblioteca().agregar("SW-E1", unidad("X", TipoUnidad.ESTANTE)));
        });
        prueba("Un AREA no puede recibir otra AREA", () -> {
            igual(false, DatosPrueba.crearBiblioteca().agregar("SW", unidad("X", TipoUnidad.AREA)));
        });
        prueba("Agregar estante a TEL y actualizar hojas", () -> {
            ArbolBiblioteca b = DatosPrueba.crearBiblioteca();
            igual(true, b.agregar("tel", unidad("tel-e1", TipoUnidad.ESTANTE)));
            igual(4, b.contarHojas()); igual(2, b.altura());
            igual(0, b.buscar("TEL-E1").getCatalogo().contar());
        });
        prueba("Jerarquia exacta en preorden con dos espacios por nivel", () -> {
            String esperada = "BIB Biblioteca FISEI [BIBLIOTECA]\n"
                    + "  SW Software [AREA]\n"
                    + "    SW-E1 Estante 1 de Software [ESTANTE]\n"
                    + "    SW-E2 Estante 2 de Software [ESTANTE]\n"
                    + "  TI Tecnologias de la Informacion [AREA]\n"
                    + "    TI-E1 Estante 1 de TI [ESTANTE]\n"
                    + "  TEL Telecomunicaciones [AREA]\n";
            igual(esperada, DatosPrueba.crearBiblioteca().mostrarJerarquia());
        });
        prueba("Catalogo vacio: cero libros, recorridos vacios y ABB valido", () -> {
            CatalogoABB c = new CatalogoABB();
            igual(0, c.contar()); igual(null, c.buscar(1)); igual(false, c.eliminar(1));
            igual(0, c.recorrer(TipoRecorrido.INORDEN).size()); igual(true, c.esABBValido());
            igual(0, c.caminoBusqueda(1).size()); igual(0, c.contarEnRango(1, 9));
        });
        prueba("Preorden exacto SW-E1", () -> igual("60 35 20 45 40 80 70 75 90",
                secuencia(DatosPrueba.crearCatalogoSW(), TipoRecorrido.PREORDEN)));
        prueba("Inorden exacto SW-E1", () -> igual("20 35 40 45 60 70 75 80 90",
                secuencia(DatosPrueba.crearCatalogoSW(), TipoRecorrido.INORDEN)));
        prueba("Posorden exacto SW-E1", () -> igual("20 40 45 35 75 70 90 80 60",
                secuencia(DatosPrueba.crearCatalogoSW(), TipoRecorrido.POSORDEN)));
        prueba("Codigo de libro repetido: false y conteo sin cambios", () -> {
            CatalogoABB c = DatosPrueba.crearCatalogoSW();
            igual(false, c.insertar(libro(60))); igual(9, c.contar());
        });
        prueba("Camino de 42 ausente: [60, 35, 45, 40]", () -> {
            igual(Arrays.asList(60, 35, 45, 40), DatosPrueba.crearCatalogoSW().caminoBusqueda(42));
        });
        prueba("Rango inclusivo 40 a 75: cinco libros", () -> {
            igual(5, DatosPrueba.crearCatalogoSW().contarEnRango(40, 75));
        });
        prueba("Rango puntual, invertido, vacio y completo", () -> {
            CatalogoABB c = DatosPrueba.crearCatalogoSW();
            igual(1, c.contarEnRango(60, 60)); igual(0, c.contarEnRango(75, 40));
            igual(0, c.contarEnRango(1, 19)); igual(9, c.contarEnRango(1, Integer.MAX_VALUE));
        });
        prueba("Insertar 42: aparece como hijo derecho de 40", () -> {
            CatalogoABB c = DatosPrueba.crearCatalogoSW();
            igual(true, c.insertar(libro(42)));
            igual(Arrays.asList(60, 35, 45, 40, 42), c.caminoBusqueda(42));
            igual(10, c.contar()); igual(true, c.esABBValido());
        });
        prueba("Insertar 42 y eliminar hoja 20", () -> {
            CatalogoABB c = DatosPrueba.crearCatalogoSW();
            c.insertar(libro(42)); igual(true, c.eliminar(20));
            igual("60 35 45 40 42 80 70 75 90", secuencia(c, TipoRecorrido.PREORDEN));
            igual(null, c.buscar(20)); igual(true, c.esABBValido());
        });
        prueba("Eliminar 70 con un hijo: asciende 75", () -> {
            CatalogoABB c = DatosPrueba.crearCatalogoSW();
            c.insertar(libro(42)); c.eliminar(20); igual(true, c.eliminar(70));
            igual("60 35 45 40 42 80 75 90", secuencia(c, TipoRecorrido.PREORDEN));
            igual(true, c.esABBValido());
        });
        prueba("Eliminar 60 con dos hijos: sucesor 75 y preorden final exacto", () -> {
            CatalogoABB c = DatosPrueba.crearCatalogoSW();
            c.insertar(libro(42)); c.eliminar(20); c.eliminar(70); igual(true, c.eliminar(60));
            igual(75, c.getRaiz().getDato().getCodigo()); igual(7, c.contar());
            igual("75 35 45 40 42 80 90", secuencia(c, TipoRecorrido.PREORDEN));
            igual(true, c.esABBValido());
        });
        prueba("Buscar, modificar y eliminar inexistente: null, false y false", () -> {
            CatalogoABB c = DatosPrueba.crearCatalogoSW();
            igual(null, c.buscar(999)); igual(false, c.modificar(999, "Titulo", "Autor", 2026));
            igual(false, c.eliminar(999)); igual(9, c.contar());
        });
        prueba("Modificar libro conserva codigo y estructura", () -> {
            CatalogoABB c = DatosPrueba.crearCatalogoSW();
            igual(true, c.modificar(45, "  Nuevo titulo  ", "Nuevo autor", 2000));
            igual("Nuevo titulo", c.buscar(45).getTitulo()); igual("Nuevo autor", c.buscar(45).getAutor());
            igual(2000, c.buscar(45).getAnio()); igual(45, c.buscar(45).getCodigo());
            igual("60 35 20 45 40 80 70 75 90", secuencia(c, TipoRecorrido.PREORDEN));
        });
        prueba("Modificacion invalida no deja cambios parciales", () -> {
            CatalogoABB c = DatosPrueba.crearCatalogoSW();
            ilegal(() -> c.modificar(45, "Otro", "Otra persona", 2027));
            igual("Libro 45", c.buscar(45).getTitulo());
            igual("Autor de prueba", c.buscar(45).getAutor()); igual(2026, c.buscar(45).getAnio());
        });
        prueba("Libro con codigo cero o negativo lanza IllegalArgumentException", () -> {
            ilegal(() -> libro(0)); ilegal(() -> libro(-1));
        });
        prueba("Titulo o autor vacio o null lanza IllegalArgumentException", () -> {
            ilegal(() -> new Libro(1, " ", "Autor", 2026));
            ilegal(() -> new Libro(1, null, "Autor", 2026));
            ilegal(() -> new Libro(1, "Titulo", " ", 2026));
            ilegal(() -> new Libro(1, "Titulo", null, 2026));
        });
        prueba("Anio fuera de 1900-2026 lanza IllegalArgumentException", () -> {
            ilegal(() -> new Libro(1, "Titulo", "Autor", 1899));
            ilegal(() -> new Libro(1, "Titulo", "Autor", 2027));
            igual(1900, new Libro(1, "Titulo", "Autor", 1900).getAnio());
            igual(2026, libro(1).getAnio());
        });
        prueba("ABB invalido por nieto 70 dentro del subarbol izquierdo de 60", () -> {
            CatalogoABB c = new CatalogoABB();
            NodoLibro raiz = new NodoLibro(libro(60));
            raiz.setIzquierdo(new NodoLibro(libro(35)));
            raiz.getIzquierdo().setDerecho(new NodoLibro(libro(70)));
            c.setRaiz(raiz); igual(false, c.esABBValido());
        });
        prueba("ABB invalido por codigo repetido en un hijo", () -> {
            CatalogoABB c = new CatalogoABB();
            NodoLibro raiz = new NodoLibro(libro(60));
            raiz.setDerecho(new NodoLibro(libro(60))); c.setRaiz(raiz);
            igual(false, c.esABBValido());
        });
        prueba("Limites admiten codigo Integer.MAX_VALUE", () -> {
            CatalogoABB c = new CatalogoABB();
            c.insertar(libro(Integer.MAX_VALUE)); c.insertar(libro(1));
            igual(true, c.esABBValido()); igual(2, c.contarEnRango(1, Integer.MAX_VALUE));
        });
        prueba("Eliminar raiz unica deja catalogo vacio", () -> {
            CatalogoABB c = new CatalogoABB(); c.insertar(libro(1));
            igual(true, c.eliminar(1)); igual(null, c.getRaiz()); igual(0, c.contar());
        });
        prueba("Eliminar raiz con solo hijo izquierdo o derecho", () -> {
            CatalogoABB c = new CatalogoABB(); c.insertar(libro(10)); c.insertar(libro(5));
            c.eliminar(10); igual(5, c.getRaiz().getDato().getCodigo());
            CatalogoABB d = new CatalogoABB(); d.insertar(libro(10)); d.insertar(libro(15));
            d.eliminar(10); igual(15, d.getRaiz().getDato().getCodigo());
        });
        prueba("Sucesor con hijo derecho conserva ese hijo y sus metadatos", () -> {
            CatalogoABB c = new CatalogoABB();
            c.insertar(libro(50)); c.insertar(libro(30)); c.insertar(libro(80));
            c.insertar(new Libro(60, "Sucesor", "Autor sucesor", 2001)); c.insertar(libro(65));
            c.eliminar(50);
            igual("60 30 80 65", secuencia(c, TipoRecorrido.PREORDEN));
            igual("Sucesor", c.getRaiz().getDato().getTitulo()); igual(true, c.esABBValido());
        });
        prueba("Traza PREORDEN exacta de un nodo", () -> {
            CatalogoABB c = new CatalogoABB(); c.insertar(libro(10));
            igual("entra 10\nVISITA 10\n  null -> regresa\n  null -> regresa\nSecuencia PREORDEN: 10\n",
                    c.trazaRecorrido(TipoRecorrido.PREORDEN));
        });
        prueba("Traza INORDEN exacta de un nodo", () -> {
            CatalogoABB c = new CatalogoABB(); c.insertar(libro(10));
            igual("entra 10\n  null -> regresa\nVISITA 10\n  null -> regresa\nSecuencia INORDEN: 10\n",
                    c.trazaRecorrido(TipoRecorrido.INORDEN));
        });
        prueba("Traza POSORDEN exacta de un nodo", () -> {
            CatalogoABB c = new CatalogoABB(); c.insertar(libro(10));
            igual("entra 10\n  null -> regresa\n  null -> regresa\nVISITA 10\nSecuencia POSORDEN: 10\n",
                    c.trazaRecorrido(TipoRecorrido.POSORDEN));
        });
        prueba("Traza vacia tiene caso base y secuencia final", () -> {
            igual("null -> regresa\nSecuencia PREORDEN:\n",
                    new CatalogoABB().trazaRecorrido(TipoRecorrido.PREORDEN));
        });
        prueba("Total de libros cambia con insercion y eliminacion", () -> {
            ArbolBiblioteca b = DatosPrueba.crearBiblioteca();
            b.buscar("SW-E1").getCatalogo().insertar(libro(42)); igual(14, b.contarLibrosTotal());
            b.buscar("SW-E1").getCatalogo().eliminar(20); igual(13, b.contarLibrosTotal());
        });
        prueba("Unidades normalizan codigo y validan datos", () -> {
            igual("SW", unidad(" sw ", TipoUnidad.AREA).getCodigo());
            ilegal(() -> unidad(" ", TipoUnidad.AREA));
            ilegal(() -> new UnidadBiblioteca("X", " ", TipoUnidad.AREA));
            ilegal(() -> unidad("X", null));
        });
        prueba("Enum valida todas las relaciones permitidas", () -> {
            igual(true, TipoUnidad.BIBLIOTECA.puedeTenerHijo(TipoUnidad.AREA));
            igual(true, TipoUnidad.AREA.puedeTenerHijo(TipoUnidad.ESTANTE));
            igual(false, TipoUnidad.BIBLIOTECA.puedeTenerHijo(TipoUnidad.ESTANTE));
            igual(false, TipoUnidad.ESTANTE.puedeTenerHijo(TipoUnidad.AREA));
            igual(false, TipoUnidad.AREA.puedeTenerHijo(null));
        });
        prueba("Extra reconstruye preorden sin alterar los recorridos", () -> {
            CatalogoABB c = CatalogoABB.desdePreorden(new int[] {60, 35, 20, 45, 40, 80, 70, 75, 90});
            igual("60 35 20 45 40 80 70 75 90", secuencia(c, TipoRecorrido.PREORDEN));
            igual("20 35 40 45 60 70 75 80 90", secuencia(c, TipoRecorrido.INORDEN));
            igual("20 40 45 35 75 70 90 80 60", secuencia(c, TipoRecorrido.POSORDEN));
            igual(9, c.contar()); igual(true, c.esABBValido());
        });
        prueba("Extra rechaza preorden incompatible, repetidos y no positivos", () -> {
            ilegal(() -> CatalogoABB.desdePreorden(new int[] {60, 80, 35}));
            ilegal(() -> CatalogoABB.desdePreorden(new int[] {60, 60}));
            ilegal(() -> CatalogoABB.desdePreorden(new int[] {0}));
            ilegal(() -> CatalogoABB.desdePreorden(null));
        });
        prueba("Extra reconstruye vacio y codigos extremos", () -> {
            igual(0, CatalogoABB.desdePreorden(new int[0]).contar());
            CatalogoABB c = CatalogoABB.desdePreorden(new int[] {1, Integer.MAX_VALUE});
            igual(2, c.contar()); igual(true, c.esABBValido());
        });

        System.out.println("\n" + aprobadas + "/" + total + " pruebas OK");
        if (aprobadas != total) {
            throw new AssertionError("Hay " + (total - aprobadas) + " pruebas fallidas.");
        }
    }

    private static Libro libro(int codigo) {
        return new Libro(codigo, "Libro " + codigo, "Autor de prueba", 2026);
    }

    private static UnidadBiblioteca unidad(String codigo, TipoUnidad tipo) {
        return new UnidadBiblioteca(codigo, "Unidad de prueba", tipo);
    }

    private static void prueba(String nombre, Runnable caso) {
        total++;
        try {
            caso.run();
            aprobadas++;
            System.out.println("[OK " + total + "] " + nombre);
        } catch (RuntimeException | AssertionError e) {
            System.out.println("[FALLO " + total + "] " + nombre + " -> " + e.getMessage());
        }
    }

    private static void igual(Object esperado, Object obtenido) {
        if (!Objects.equals(esperado, obtenido)) {
            throw new AssertionError("Esperado: " + esperado + "; obtenido: " + obtenido);
        }
    }

    private static void ilegal(Runnable accion) {
        try {
            accion.run();
        } catch (IllegalArgumentException e) {
            return;
        }
        throw new AssertionError("Se esperaba IllegalArgumentException.");
    }

    private static String secuencia(CatalogoABB catalogo, TipoRecorrido tipo) {
        return codigos(catalogo.recorrer(tipo), 0).trim();
    }

    private static String codigos(List<Libro> libros, int indice) {
        if (indice == libros.size()) { return ""; }
        return libros.get(indice).getCodigo() + " " + codigos(libros, indice + 1);
    }
}
