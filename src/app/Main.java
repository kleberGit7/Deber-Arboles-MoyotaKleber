package app;

import enums.TipoRecorrido;
import enums.TipoUnidad;
import java.util.Scanner;
import modelo.Libro;
import modelo.UnidadBiblioteca;
import negocio.ArbolBiblioteca;
import negocio.CatalogoABB;
import negocio.NodoUnidad;

public class Main {

    private final Scanner entrada = new Scanner(System.in);
    private ArbolBiblioteca biblioteca = new ArbolBiblioteca();

    public static void main(String[] args) {
        new Main().ejecutar();
    }

    private void ejecutar() {
        boolean continuar = true;

        while (continuar) {
            mostrarMenu();

            try {
                int opcion = leerEntero("Seleccione una opcion (0-15): ");

                switch (opcion) {
                    case 1:
                        biblioteca = DatosPrueba.crearBiblioteca();
                        System.out.println(
                                "Biblioteca inicial cargada: 7 unidades y 13 libros.");
                        break;

                    case 2:
                        System.out.print(biblioteca.mostrarJerarquia());
                        break;

                    case 3:
                        agregarUnidad();
                        break;

                    case 4:
                        buscarUnidad();
                        break;

                    case 5:
                        mostrarMetricas();
                        break;

                    case 6:
                        insertarLibro();
                        break;

                    case 7:
                        buscarLibro();
                        break;

                    case 8:
                        modificarLibro();
                        break;

                    case 9:
                        eliminarLibro();
                        break;

                    case 10:
                        mostrarRecorrido();
                        break;

                    case 11:
                        contarRango();
                        break;

                    case 12:
                        mostrarCamino();
                        break;

                    case 13:
                        validarABB();
                        break;

                    case 14:
                        PruebasBiblioteca.main(new String[0]);
                        break;

                    case 15:
                        reconstruirPreorden();
                        break;

                    case 0:
                        continuar = false;
                        break;

                    default:
                        System.out.println("Opcion no valida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero entero valido.");

            } catch (IllegalArgumentException e) {
                System.out.println("Dato invalido: " + e.getMessage());

            } catch (java.util.NoSuchElementException e) {
                continuar = false;
            }
        }

        entrada.close();
        System.out.println("Programa finalizado.");
    }

    private void mostrarMenu() {
        System.out.println("\nCATALOGO DE LA BIBLIOTECA FISEI");
        System.out.println("1. Cargar biblioteca inicial (reinicia los datos)");
        System.out.println("2. Mostrar jerarquia");
        System.out.println("3. Agregar unidad");
        System.out.println("4. Buscar unidad");
        System.out.println("5. Consultar altura, hojas, libros y nivel");
        System.out.println("6. Insertar libro en un estante");
        System.out.println("7. Buscar libro en un estante");
        System.out.println("8. Modificar libro");
        System.out.println("9. Eliminar libro");
        System.out.println("10. Mostrar recorrido y traza");
        System.out.println("11. Contar libros en un rango");
        System.out.println("12. Mostrar camino de busqueda");
        System.out.println("13. Validar ABB de un estante");
        System.out.println("14. Ejecutar pruebas automaticas");
        System.out.println("15. Reconstruir un catalogo desde preorden (extra)");
        System.out.println("0. Salir");
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return entrada.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        return Integer.parseInt(leerTexto(mensaje));
    }

    private CatalogoABB seleccionarEstante() {
        String codigo = leerTexto("Codigo del estante: ");
        NodoUnidad unidad = biblioteca.buscar(codigo);

        if (unidad == null || unidad.getCatalogo() == null) {
            System.out.println(
                    "No existe ese estante; debe seleccionar una unidad ESTANTE.");
            return null;
        }

        return unidad.getCatalogo();
    }

    private void agregarUnidad() {
        String padre = leerTexto("Codigo del padre: ");
        String codigo = leerTexto("Codigo de la nueva unidad: ");
        String nombre = leerTexto("Nombre: ");
        int opcion = leerEntero("Tipo (1 AREA, 2 ESTANTE): ");

        if (opcion != 1 && opcion != 2) {
            throw new IllegalArgumentException("Tipo de unidad no valido.");
        }

        TipoUnidad tipo = opcion == 1
                ? TipoUnidad.AREA
                : TipoUnidad.ESTANTE;

        UnidadBiblioteca nueva = new UnidadBiblioteca(codigo, nombre, tipo);
        boolean resultado = biblioteca.agregar(padre, nueva);

        System.out.println("Agregar: " + resultado);

        if (!resultado) {
            System.out.println(
                    "Revise el padre, la jerarquia y los codigos repetidos.");
        }
    }

    private void buscarUnidad() {
        String codigo = leerTexto("Codigo de unidad: ");
        NodoUnidad unidad = biblioteca.buscar(codigo);

        if (unidad == null) {
            System.out.println("Unidad: null (no existe).");
        } else {
            System.out.println(unidad.getDato());
        }
    }

    private void mostrarMetricas() {
        System.out.println("Altura: " + biblioteca.altura());
        System.out.println("Hojas: " + biblioteca.contarHojas());
        System.out.println("Libros: " + biblioteca.contarLibrosTotal());

        String codigo = leerTexto("Codigo para consultar nivel: ");

        System.out.println(
                "Nivel de " + codigo + ": " + biblioteca.nivel(codigo));
    }

    private void insertarLibro() {
        CatalogoABB catalogo = seleccionarEstante();

        if (catalogo == null) {
            return;
        }

        int codigo = leerEntero("Codigo del libro: ");
        String titulo = leerTexto("Titulo: ");
        String autor = leerTexto("Autor: ");
        int anio = leerEntero("Anio (1900-2026): ");

        Libro libro = new Libro(codigo, titulo, autor, anio);

        System.out.println("Insertar: " + catalogo.insertar(libro));
    }

    private void buscarLibro() {
        CatalogoABB catalogo = seleccionarEstante();

        if (catalogo == null) {
            return;
        }

        int codigo = leerEntero("Codigo del libro: ");
        Libro libro = catalogo.buscar(codigo);

        if (libro == null) {
            System.out.println("Libro: null (no existe).");
        } else {
            System.out.println(libro);
        }
    }

    private void modificarLibro() {
        CatalogoABB catalogo = seleccionarEstante();

        if (catalogo == null) {
            return;
        }

        int codigo = leerEntero("Codigo del libro: ");
        String titulo = leerTexto("Nuevo titulo: ");
        String autor = leerTexto("Nuevo autor: ");
        int anio = leerEntero("Nuevo anio: ");

        boolean resultado = catalogo.modificar(codigo, titulo, autor, anio);

        System.out.println("Modificar: " + resultado);
    }

    private void eliminarLibro() {
        CatalogoABB catalogo = seleccionarEstante();

        if (catalogo == null) {
            return;
        }

        int codigo = leerEntero("Codigo del libro: ");

        System.out.println("Eliminar: " + catalogo.eliminar(codigo));
    }

    private void mostrarRecorrido() {
        CatalogoABB catalogo = seleccionarEstante();

        if (catalogo == null) {
            return;
        }

        int opcion = leerEntero(
                "Recorrido (1 PREORDEN, 2 INORDEN, 3 POSORDEN): ");

        TipoRecorrido tipo;

        switch (opcion) {
            case 1:
                tipo = TipoRecorrido.PREORDEN;
                break;

            case 2:
                tipo = TipoRecorrido.INORDEN;
                break;

            case 3:
                tipo = TipoRecorrido.POSORDEN;
                break;

            default:
                throw new IllegalArgumentException("Recorrido no valido.");
        }

        System.out.print(catalogo.trazaRecorrido(tipo));
    }

    private void contarRango() {
        CatalogoABB catalogo = seleccionarEstante();

        if (catalogo == null) {
            return;
        }

        int a = leerEntero("Limite inferior: ");
        int b = leerEntero("Limite superior: ");

        System.out.println("Libros en rango: " + catalogo.contarEnRango(a, b));
    }

    private void mostrarCamino() {
        CatalogoABB catalogo = seleccionarEstante();

        if (catalogo == null) {
            return;
        }

        int codigo = leerEntero("Codigo buscado: ");

        System.out.println("Camino: " + catalogo.caminoBusqueda(codigo));
        System.out.println("Encontrado: " + (catalogo.buscar(codigo) != null));
    }

    private void validarABB() {
        CatalogoABB catalogo = seleccionarEstante();

        if (catalogo != null) {
            System.out.println("ABB valido: " + catalogo.esABBValido());
        }
    }

    private void reconstruirPreorden() {
        CatalogoABB catalogo = seleccionarEstante();

        if (catalogo == null) {
            return;
        }

        System.out.println(
                "Se reemplazaran los libros del estante por los codigos ingresados.");

        String texto = leerTexto(
                "Preorden separado por espacios (vacio = catalogo vacio): ");

        String[] partes = texto.isEmpty()
                ? new String[0]
                : texto.split("\\s+");

        int[] codigos = new int[partes.length];

        leerCodigos(partes, codigos, 0);

        CatalogoABB nuevo = CatalogoABB.desdePreorden(codigos);
        catalogo.setRaiz(nuevo.getRaiz());

        System.out.println(
                "Catalogo reconstruido con datos descriptivos de ejemplo.");

        System.out.print(catalogo.trazaRecorrido(TipoRecorrido.PREORDEN));
    }

    private void leerCodigos(String[] partes, int[] codigos, int indice) {
        if (indice == partes.length) {
            return;
        }

        codigos[indice] = Integer.parseInt(partes[indice]);
        leerCodigos(partes, codigos, indice + 1);
    }
}
