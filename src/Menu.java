import java.util.Scanner;

public class Menu {
    public static Scanner sc = new Scanner(System.in);

    public static void pintarMenu() {

        System.out.println("Introduzca la opcion que desea realizar:  ");
        System.out.println("""
                [1] Repartir equipos
                [2] Iniciar partida
                [3] Ver jugador novato y veterano
                [4] Ver jugador con más puntería
                [5] Reiniciar
                [6] Salir
                """);
    }

    public static String leerOpcion() {
        return sc.nextLine();
    }

    public static void menu() {
        String opc;
        boolean salir = false;

        do {
            pintarMenu();
            opc = leerOpcion();

            switch (opc) {

                case "1":
                    Utilidades.generarEquipos();
                    break;

                case "2":
                    Utilidades.iniciarPartida();
                    break;

                case "3":

                    break;

                case "4":
                    System.out.println("El jugador con mejor puntería es: ");
                    Utilidades.jugadorMasPunteria();
                    System.out.println("El jugador con peor puntería es: ");
                    Utilidades.jugadorMenosPunteria();
                    break;

                case "5":

                    break;

                case "6":
                    salir = true;
                    break;
            }

        } while (!salir);
    }
}
