import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Códigos de color ANSI para la consola. */
class Colores {
    public static final String RESET = "\u001B[0m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
}

/** Representa un libro de la biblioteca. */
class Libro {
    private String codigo;
    private String titulo;
    private String autor;
    private boolean disponible;

    public Libro(String codigo, String titulo, String autor) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.disponible = true;
    }

    public String getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public boolean isDisponible() { return disponible; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    /** Verde si está disponible, amarillo si está prestado. */
    public String getEstadoColor() {
        return disponible
                ? Colores.VERDE + "DISPONIBLE" + Colores.RESET
                : Colores.AMARILLO + "PRESTADO" + Colores.RESET;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-30s | %-20s | %s",
                codigo, titulo, autor, getEstadoColor());
    }
}

/** Representa un usuario de la biblioteca. */
class Usuario {
    private String identificacion;
    private String nombre;
    private String correo;

    public Usuario(String identificacion, String nombre, String correo) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.correo = correo;
    }

    public String getIdentificacion() { return identificacion; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String consultarInformacion() {
        return String.format("ID: %s | Nombre: %s | Correo: %s",
                identificacion, nombre, correo);
    }
}

/** Representa un préstamo de un libro a un usuario. */
class Prestamo {
    private Libro libro;
    private Usuario usuario;
    private LocalDate fechaPrestamo;
    private String estado; // "ACTIVO" o "FINALIZADO"

    public Prestamo(Libro libro, Usuario usuario) {
        this.libro = libro;
        this.usuario = usuario;
        this.fechaPrestamo = LocalDate.now();
        this.estado = "ACTIVO";
    }

    public Libro getLibro() { return libro; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public String getEstado() { return estado; }

    public boolean estaActivo() { return estado.equals("ACTIVO"); }

    /** Registra el préstamo: marca el libro como no disponible. */
    public boolean registrar() {
        if (!libro.isDisponible()) {
            return false;
        }
        libro.setDisponible(false);
        estado = "ACTIVO";
        return true;
    }

    /** Finaliza el préstamo: el libro vuelve a estar disponible. */
    public void finalizar() {
        libro.setDisponible(true);
        estado = "FINALIZADO";
    }

    @Override
    public String toString() {
        String estadoColor = estaActivo()
                ? Colores.AMARILLO + estado + Colores.RESET
                : Colores.VERDE + estado + Colores.RESET;
        return String.format("Libro: %-25s | Usuario: %-15s | Fecha: %s | Estado: %s",
                libro.getTitulo(),
                usuario.getNombre(),
                Colores.ROJO + fechaPrestamo + Colores.RESET,
                estadoColor);
    }
}

/** Administra libros, usuarios y préstamos. */
class Biblioteca {
    private List<Libro> libros = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Prestamo> prestamos = new ArrayList<>();

    // ---------- Búsquedas ----------
    public Libro buscarLibro(String codigo) {
        for (Libro l : libros) {
            if (l.getCodigo().equalsIgnoreCase(codigo)) return l;
        }
        return null;
    }

    public Usuario buscarUsuario(String id) {
        for (Usuario u : usuarios) {
            if (u.getIdentificacion().equals(id)) return u;
        }
        return null;
    }

    // ---------- Registros ----------
    public boolean registrarLibro(Libro libro) {
        if (buscarLibro(libro.getCodigo()) != null) return false;
        libros.add(libro);
        return true;
    }

    public boolean registrarUsuario(Usuario usuario) {
        if (buscarUsuario(usuario.getIdentificacion()) != null) return false;
        usuarios.add(usuario);
        return true;
    }

    // ---------- Consultas ----------
    public void consultarLibros() {
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }
        for (Libro l : libros) System.out.println(l);
    }

    public void consultarUsuarios() {
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }
        for (Usuario u : usuarios) System.out.println(u.consultarInformacion());
    }

    public void consultarPrestamos() {
        if (prestamos.isEmpty()) {
            System.out.println("No hay préstamos registrados.");
            return;
        }
        for (Prestamo p : prestamos) System.out.println(p);
    }

    public void consultarDisponibilidad(String codigo) {
        Libro libro = buscarLibro(codigo);
        if (libro == null) {
            System.out.println("El libro no existe.");
        } else {
            System.out.println("El libro \"" + libro.getTitulo() + "\" está: "
                    + libro.getEstadoColor());
        }
    }

    // ---------- Préstamos y devoluciones ----------
    public void prestarLibro(String codigo, String idUsuario) {
        Libro libro = buscarLibro(codigo);
        Usuario usuario = buscarUsuario(idUsuario);

        if (libro == null) {
            System.out.println("El libro no existe.");
            return;
        }
        if (usuario == null) {
            System.out.println("El usuario no existe.");
            return;
        }

        Prestamo prestamo = new Prestamo(libro, usuario);
        if (prestamo.registrar()) {
            prestamos.add(prestamo);
            System.out.println(Colores.AMARILLO + "Préstamo realizado: \""
                    + libro.getTitulo() + "\" -> " + usuario.getNombre() + Colores.RESET);
        } else {
            System.out.println(Colores.AMARILLO
                    + "El libro ya se encuentra prestado." + Colores.RESET);
        }
    }

    public void devolverLibro(String codigo) {
        for (Prestamo p : prestamos) {
            if (p.estaActivo() && p.getLibro().getCodigo().equalsIgnoreCase(codigo)) {
                p.finalizar();
                System.out.println(Colores.VERDE + "Devolución registrada: \""
                        + p.getLibro().getTitulo() + "\" ya está disponible." + Colores.RESET);
                return;
            }
        }
        System.out.println("No hay un préstamo activo para ese libro.");
    }
}

/** Clase principal con el menú interactivo. */
public class SistemaBiblioteca {

    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n===== MENÚ BIBLIOTECA =====");
            System.out.println("1. Registrar libro");
            System.out.println("2. Registrar usuario");
            System.out.println("3. Consultar libros");
            System.out.println("4. Consultar usuarios");
            System.out.println("5. Prestar libro");
            System.out.println("6. Devolver libro");
            System.out.println("7. Consultar disponibilidad de un libro");
            System.out.println("8. Consultar préstamos");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    System.out.print("Código: ");
                    String cod = sc.nextLine().trim();
                    System.out.print("Título: ");
                    String titulo = sc.nextLine().trim();
                    System.out.print("Autor: ");
                    String autor = sc.nextLine().trim();
                    if (cod.isEmpty() || titulo.isEmpty() || autor.isEmpty()) {
                        System.out.println("Todos los campos son obligatorios.");
                    } else if (biblioteca.registrarLibro(new Libro(cod, titulo, autor))) {
                        System.out.println(Colores.VERDE + "Libro registrado (disponible)." + Colores.RESET);
                    } else {
                        System.out.println("Ya existe un libro con ese código.");
                    }
                    break;
                case 2:
                    System.out.print("Identificación: ");
                    String id = sc.nextLine().trim();
                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine().trim();
                    System.out.print("Correo electrónico: ");
                    String correo = sc.nextLine().trim();
                    if (id.isEmpty() || nombre.isEmpty() || !correo.contains("@")) {
                        System.out.println("Datos inválidos (revise el correo).");
                    } else if (biblioteca.registrarUsuario(new Usuario(id, nombre, correo))) {
                        System.out.println(Colores.VERDE + "Usuario registrado." + Colores.RESET);
                    } else {
                        System.out.println("Ya existe un usuario con esa identificación.");
                    }
                    break;
                case 3:
                    biblioteca.consultarLibros();
                    break;
                case 4:
                    biblioteca.consultarUsuarios();
                    break;
                case 5:
                    System.out.print("Código del libro: ");
                    String codPrestamo = sc.nextLine().trim();
                    System.out.print("Identificación del usuario: ");
                    String idPrestamo = sc.nextLine().trim();
                    biblioteca.prestarLibro(codPrestamo, idPrestamo);
                    break;
                case 6:
                    System.out.print("Código del libro a devolver: ");
                    biblioteca.devolverLibro(sc.nextLine().trim());
                    break;
                case 7:
                    System.out.print("Código del libro: ");
                    biblioteca.consultarDisponibilidad(sc.nextLine().trim());
                    break;
                case 8:
                    biblioteca.consultarPrestamos();
                    break;
                case 0:
                    System.out.println("¡Hasta pronto!");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (opcion != 0);

        sc.close();
    }
}
