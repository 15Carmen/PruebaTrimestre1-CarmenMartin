import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utilidades {
    private static String servidor = "jdbc:mysql://dns11036.phdns11.es";
    private static Connection connection;
    private static Statement st = null;

    public static Scanner sc = new Scanner(System.in);

    public static String[] Partidas = {"idPartida int PRIMARY KEY AUTO_INCREMENT",
            "equipoGanador varchar(5)",
            "numSupervivientes int"};

    public static String[] Equipos = {"idPartida int",
            "equipo varchar(5)",
            "idJugador int",
            "vidas int",
            "PRIMARY KEY (idPartida, equipo, idJugador)",
            "FOREIGN KEY (idPartida) REFERENCES Partidas(idPartida) ON DELETE CASCADE ON UPDATE CASCADE"};

    public static String[] Jugadores = {"idJugador int",
            "nombre varchar(45)",
            "numAciertos int",
            "PRIMARY KEY (idJugador)",
    };

    /**
     * Metodo que usaremos para conectarnos con la base de datos
     * @return
     */

    public static Connection crearConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(servidor + "/ad2223_cmartin", "ad2223_cmartin", "Marnu");

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    /**
     * Metodo que va a crear las tablas de las bases de datos, pasándoles por parámetros
     * @param tabla que es un array de string con los datos de las tablas y
     * @param nombreTabla que es el nombre que se le pondrá a la tabla
     */

    public static void crearTablas(String[] tabla, String nombreTabla) {
        String sql = "CREATE TABLE ad2223_cmartin." + nombreTabla + " (";
        for (int i = 0; i < tabla.length; i++) {
            sql += tabla[i];
            if (i < tabla.length - 1) {
                sql += ",";
            }
        }
        sql += " );";

        System.out.println(sql);

        try {
            st = connection.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo que se va a encargar de introducir los datos del txt Personas en la columna nombre de la tabla Jugadores
     */

    public static void insertarDatosJugadores() {

        List<String> lista = new ArrayList<>();
        String sql = "Insert into ad2223_cmartin.Jugadores (nombre) VALUES ";

        try (FileReader fr = new FileReader("src/fichero/Personas.txt");
             BufferedReader br = new BufferedReader(fr)) {

            String linea;
            while ((linea = br.readLine()) != null) {
                lista.add(linea);
            }

            st = connection.createStatement();
            for (int i = 0; i < lista.size(); i++) {
                sql+=lista.get(i);
                st.executeUpdate(sql);
            }

            st.close();

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Metodo que va a actualizar la base de datos si se realiza un disparo certero de tal forma que
     * @param idSoldado perderá una vida y
     * @param idJugador ganará un acierto
     */
    public static void disparoCertero(int idSoldado, int idJugador){

        String sql, sql2;

            System.out.println("Has dado un disparo certero!");
            sql = "Update ad2223_cmartin.Equipos Set vidas = vidas-1 Where idJugador = '" + idSoldado + "'";
            sql2 = "Update ad2223_cmartin.Jugadores Set numAciertos = numAciertos+1 Where idJugador = '" + idJugador + "'";
            try {
                st = connection.createStatement();
                st.executeUpdate(sql);
                st.executeUpdate(sql2);
                st.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    /**
     * Metodo que realizará el disparo a un jugador aleatorio del equipo del jugador siguiendo más o menos la misma mecánica
     * que al principio del método iniciarPartida()
     */

    public static void disparoMaquina(){

    }

    /**
     * Metodo que actualizará la tabla de equipos si un soldado pierde sus 2 vidas, haciendo que este soldado desaparezca hasta
     * que se reinicie la partida
     */
    public static void muerteSoldado(){
        String sql;
        sql= "DELETE FROM ad2223_cmartin.Equipos WHERE vidas = 0";
        try {
            st = connection.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void iniciarPartida(){

        String equipo;
        int idSoldado;
        int idJugador;
        double punteria = Math.random()*1;
        String equipoJugador;

        System.out.println("Elija a que equipo perteneces");
        equipo = sc.nextLine();

        System.out.println("Indique el numero de identificacion de su tirador");
        idJugador = sc.nextInt();

        if (equipo.equals("Verde")||equipo.equals("verde")){

            equipoJugador = "Select idJugador, vidas from ad2223_cmartin.Equipos Where equipo like 'Verde'";

            System.out.println("Este es tu equipo: \n");
            System.out.println(equipoJugador);

            System.out.println("Elija un soldado del equipo enemigo al que disparar");
            idSoldado = sc.nextInt();

            if (punteria>=0.8) {
                disparoCertero(idSoldado, idJugador);
            }else {
                System.out.println("Has fallado! Mejor suerte la proxima vez");
            }

            disparoMaquina();



        }else if (equipo.equals("Rojo")||equipo.equals("rojo")){

            equipoJugador = "Select idJugador, vidas from ad2223_cmartin.Equipos Where equipo like 'Rojo'";

            System.out.println("Este es tu equipo: \n");
            System.out.println(equipoJugador);

            System.out.println("Elija un soldado del equipo enemigo al que disparar");
            idSoldado = sc.nextInt();

            if (punteria>=0.8) {
                disparoCertero(idSoldado, idJugador);
            }else {
                System.out.println("Has fallado! Mejor suerte la proxima vez");
            }
        }

    }

    /**
     * Metodo que se va a utilizar para repartir de forma aleatoria los equipos
     */
    public static void generarEquipos(){
        PreparedStatement ps;
        String sql = "INSERT INTO ad2223_cmartin.Equipos (equipo, vidas) VALUES (?, ?);";

        try {
            ps = connection.prepareStatement(sql);

            int equipo = (int) (Math.random()*2);

            if (equipo == 0){
                ps.setString(1, "Rojo");
            }else {
                ps.setString(1, "Verde");
            }

            ps.setInt(2, 2);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Metodo que devolverá el jugador con mejor punteria
     */
    public static void jugadorMasPunteria(){
        String sql = "Select nombre from ad2223_cmartin.Jugadores where numAciertos = (SELECT MAX(numAciertos ) FROM ad2223_cmartin.Jugadores)";
        try {
            st = connection.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo que devolverá el jugador con peor punteria
     */
    public static void jugadorMenosPunteria(){
        String sql = "Select nombre from ad2223_cmartin.Jugadores where numAciertos = (SELECT MIN(numAciertos ) FROM ad2223_cmartin.Jugadores)";
        try {
            st = connection.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
