/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizadorlexico;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Ricardo
 */
public class Parser {

    final static Logger log4j = Logger.getLogger(Parser.class.getName());

    int cantidadError = 0;	// Cantidad de errores
    String[][] conjuntosPrimero = new String[11][7];//conjuntos primeros de la gramatica 
    String[][] conjuntosSiguiente = new String[11][4];//Conjuntos siguientes de la gramatica 

    String[] token = new String[50];
    int vacio = 0;
    int volver = 0;
    int n = 0;

    String cadena = "";
    String[] entrada;
    int posicion = 0;
    int EOF = 0;
    String[] comp_lex = new String[12];

    private static final int TAMLEX = 50;

    public void parsearArchivo(String ruta) {
        File file = new File(ruta);
        FileReader fr = null;
        BufferedReader br = null;
        iniciarTabla();

        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);

        } catch (FileNotFoundException e) {
            log4j.error("No se encontro el archivo " + file.getName());
        }
        try {
            String lines = "";
            while ((lines = br.readLine()) != null) {
                cadena += lines;
            }
            log4j.info("Cadena " + cadena);
            entrada = cadena.split(" ");
            log4j.info("Longitud Cadena " + entrada.length);
            EOF = entrada.length - 1;
        } catch (IOException ex) {
            log4j.error("Error al leer el archivo " + ex);
        }

        element();
        match("EOF");

        if (cantidadError == 0) {
            log4j.info("El codigo fuente es sintacticamente correcto.");
            JOptionPane.showMessageDialog(null, "El codigo fuente es sintacticamente correcto.");
        } else {
            log4j.info("El codigo fuente no es sintacticamente correcto.");
            JOptionPane.showMessageDialog(null, "El codigo fuente no es sintacticamente correcto.");
        }
    }

    void attribute_value() {
        posicion++;
        log4j.info("Posicion " + entrada[posicion]);
        String[] lexema_tagName = new String[TAMLEX];
        if (entrada[posicion].equals("LITERAL_CADENA")) {
            match("LITERAL_CADENA");
            //log4j.info("LITERAL_CADENA %s %s %s \n",t.pe->lexema, e.lexema, comp_lex[t.compLex]);
//            log4j.info(salida, "\"%s\"", e.lexema);
        } else if (entrada[posicion].equals("LITERAL_NUM")) {
            match("LITERAL_NUM");
            //log4j.info("%s", comp_lex[t.compLex]);
//            log4j.info(salida, "%s", e.lexema);
        } else if (entrada[posicion].equals("PR_TRUE")) {
            match("PR_TRUE");
//            log4j.info(salida, "true");
        } else if (entrada[posicion].equals("PR_FALSE")) {
            match("PR_FALSE");
//            log4j.info(salida, "false");
        } else if (entrada[posicion].equals("PR_NULL")) {
            match("PR_NULL");
//            log4j.info(salida, "null");
        } else if (entrada[posicion].equals("L_CORCHETE")) {
            element();
        } else if (entrada[posicion].equals("L_LLAVE")) {
            element();
        } else {
            cantidadError = cantidadError + 1;
            log4j.info("Error Sintactico");
            volver = panicModeConSincronizacion(8, entrada[posicion]);
            if (volver == 1) {
                attribute_value();
            }
        }

    }

    void attribute_name() {
        log4j.info("Posicion " + entrada[posicion]);
        if (entrada[posicion].equals("LITERAL_CADENA")) {
            match("LITERAL_CADENA");
            //log4j.info(salida,"false");
        } else {
            cantidadError = cantidadError + 1;
            log4j.info("Error Sintactico");
            volver = panicModeConSincronizacion(7, entrada[posicion]);
            if (volver == 1) {
                attribute_name();
            }
        }
    }

    public void attribute() {

        String[] lexema_tagName = new String[TAMLEX];
        String[] lexema_elementList = new String[TAMLEX];
        log4j.info("Posicion " + entrada[posicion]);
        if (entrada[posicion].equals("LITERAL_CADENA")) {
//		if (n != 0) {
//                log4j.info(salida, "\n");
//                int s;
//                for (s = 0; s < n; s++) {
//                    log4j.info(salida, "\t");
//                }
//            }
//            log4j.info(salida, "<");
//            //sacarComillas(lexema_tagName);
//            log4j.info(salida, "%s", lexema_tagName);
            attribute_name();
//            log4j.info(salida, ">");
            posicion++;
            log4j.info("Posicion " + entrada[posicion]);
            match("DOS_PUNTOS");
            attribute_value();
//            if (n == 1) {
//                log4j.info(salida, "\n");
//            }
//            log4j.info(salida, "</");
//            log4j.info(salida, "%s", lexema_tagName);
//            //sacarComillas(lexema_tagName);
//            log4j.info(salida, ">");
        } else {
            cantidadError = cantidadError + 1;
            log4j.info("Error Sintactico");
            volver = panicModeConSincronizacion(6, entrada[posicion]);
            if (volver == 1) {
                attribute();
            }
        }
    }

    public void a() {
        posicion++;
        log4j.info("Posicion " + entrada[posicion]);
        if (entrada[posicion].equals("R_LLAVE")) {
            match("R_LLAVE");
        } else if (entrada[posicion].equals("COMA")) {
            match("COMA");
            posicion++;
            attribute();
            a();
        } else {
            boolean existeSig = existeTokenSiguiente(5, entrada[posicion]);
            if (!existeSig) {
                log4j.info("Posicion " + entrada[posicion]);
                if (posicion <= EOF) {
                    cantidadError = cantidadError + 1;
                    log4j.info("Error Sintactico");
                    scan();
                    a();
                }
            }
        }
    }

    public void array() {
        log4j.info("Posicion " + entrada[posicion]);
        if (entrada[posicion].equals("L_CORCHETE")) {
            match("L_CORCHETE");
            posicion++;
            log4j.info("Posicion " + entrada[posicion]);
            if (!entrada[posicion].equals("R_CORCHETE")) {
                log4j.info("Posicion " + entrada[posicion]);
                n++;
                element_list();
                match("R_CORCHETE");
//                log4j.info(salida, "\n");
//                int s;
//                for (s = 0; s < n - 1; s++) {
//                    log4j.info(salida, "\t");
//                }
//                n--;
            } else {
                match("R_CORCHETE");
            }
        } else {
            cantidadError = cantidadError + 1;
            log4j.info("Error Sintactico ");
            volver = panicModeConSincronizacion(3, entrada[posicion]);
            if (volver == 1) {
                array();
            }
        }
    }

    public void object() {
        log4j.info("Posicion " + entrada[posicion]);
        if (entrada[posicion].equals("L_LLAVE")) {
            match("L_LLAVE");
            posicion++;
            log4j.info("Posicion " + entrada[posicion]);
            if (!entrada[posicion].equals("R_LLAVE")) {
                attributes_list();
                match("R_LLAVE");
            } else {
                match("R_LLAVE");
            }
        } else {
            cantidadError = cantidadError + 1;
            log4j.info("Error Sintactico");
            volver = panicModeConSincronizacion(2, entrada[posicion]);
            if (volver == 1) {
                object();
            }
        }
    }

    public void element() {
        String[] lexema_tagName = new String[TAMLEX];
        String[] lexema_elementList = new String[TAMLEX];
        log4j.info("Posicion " + entrada[posicion]);
        if (entrada[posicion].equals("L_LLAVE")) {
            object();
        } else if (entrada[posicion].equals("L_CORCHETE")) {
            array();
        } else {
            cantidadError = cantidadError + 1;
            log4j.info("Error Sintactico");
            volver = panicModeConSincronizacion(1, entrada[posicion]);
            if (volver == 1) {
                element();
            }
        }

    }

    public void attributes_list() {
        log4j.info("Posicion " + entrada[posicion]);
        if (entrada[posicion].equals("LITERAL_CADENA")) {
            attribute();
            a();
        } else {
            cantidadError = cantidadError + 1;
            log4j.info("Error Sintactico");
            volver = panicModeConSincronizacion(4, entrada[posicion]);
            if (volver == 1) {
                attributes_list();
            }
        }
    }

    public void element_list() {

//        char lexema_tagName[TAMLEX];
//	char lexema_elementList[TAMLEX];
        log4j.info("Posicion " + entrada[posicion]);
        if (entrada[posicion].equals("L_CORCHETE") || entrada[posicion].equals("L_LLAVE")) {
//            log4j.info(salida, "\n");
//            int s;
//            for (s = 0; s < n; s++) {
//                log4j.info(salida, "\t");
//            }
//            n++;
//            log4j.info(salida, "<item>");
            element();
//            log4j.info(salida, "\n");
//            //int s;
//            for (s = 0; s < n - 1; s++) {
//                log4j.info(salida, "\t");
//            }
//            n--;
//            log4j.info(salida, "</item>");
            e1();
        } else {
            cantidadError = cantidadError + 1;
            log4j.info("Error Sintactico");
            volver = panicModeConSincronizacion(9, entrada[posicion]);
            if (volver == 1) {
                element_list();
            }
        }
    }

    public void e1() {
        posicion++;
        log4j.info("Posicion " + entrada[posicion]);
        if (entrada[posicion].equals("R_CORCHETE") || entrada[posicion].equals("R_LLAVE")) {
//            log4j.info("NO pasa nada");
        } else if (entrada[posicion].equals("COMA")) {
            int s;
            match("COMA");
//            log4j.info(salida, "\n");
//            for (s = 0; s < n; s++) {
//                log4j.info(salida, "\t");
//            }
//            n++;
//            log4j.info(salida, "<item>");
            posicion++;
            log4j.info("Posicion " + entrada[posicion]);
            element();
//            log4j.info(salida, "\n");
//            //int s;
//            for (s = 0; s < n - 1; s++) {
//                log4j.info(salida, "\t");
//            }
//            n--;
//            log4j.info(salida, "</item>");
            e1();
        } else {
            boolean existeSig = existeTokenSiguiente(10, entrada[posicion]);
            if (!existeSig) {
                log4j.info("Posicion " + entrada[posicion]);
                if (posicion <= EOF) {
                    cantidadError = cantidadError + 1;
                    log4j.info("Error Sintactico");
                    scan();
                    e1();
                }
            }
        }
    }

    public void match(String proximoToken) {
        if (entrada[posicion].equals(proximoToken)) {
            log4j.info("Match");
//            if (!strcmp(comp_lex[t.compLex], "EOF") == 0) {
            //log4j.info("%s %s %s \n", comp_lex[t.compLex], t.pe->lexema, e.lexema);
//                anaLex();
//            }
        } else if (proximoToken.equals("EOF")) {
            if (posicion == EOF) {
                log4j.info("Fin del Archivo");
            }
        } else {
            cantidadError = cantidadError + 1;
            log4j.info("Error en linea  se esperaba: " + proximoToken + " en vez de " + entrada[posicion]);
        }

    }

    void initConjuntosPrimero() {
        int i = 0;
        //carga los conjuntos primeros en la matriz
	/*	0:json
         1:element
         2:object
         3:array
         4:attributes_list
         5:a
         6:attribute
         7:attribute_name
         8:attribute_value
         9:element_list
         10:e
         */
        conjuntosPrimero[1][0] = "L_CORCHETE";
        conjuntosPrimero[1][1] = "L_LLAVE";
        conjuntosPrimero[2][0] = "L_LLAVE";
        conjuntosPrimero[3][0] = "L_CORCHETE";
        conjuntosPrimero[4][0] = "LITERAL_CADENA";
        conjuntosPrimero[5][0] = "COMA";
        conjuntosPrimero[5][1] = "EMPTY";
        conjuntosPrimero[6][0] = "LITERAL_CADENA";
        conjuntosPrimero[7][0] = "LITERAL_CADENA";
        conjuntosPrimero[8][0] = "LITERAL_CADENA";
        conjuntosPrimero[8][1] = "LITERAL_NUM";
        conjuntosPrimero[8][2] = "PR_TRUE";
        conjuntosPrimero[8][3] = "PR_FALSE";
        conjuntosPrimero[8][4] = "PR_NULL";
        conjuntosPrimero[8][5] = "L_CORCHETE";
        conjuntosPrimero[8][6] = "L_LLAVE";
        conjuntosPrimero[9][0] = "L_CORCHETE";
        conjuntosPrimero[9][1] = "L_LLAVE";
        conjuntosPrimero[10][0] = "COMA";
        conjuntosPrimero[10][1] = "EMPTY";
    }

    void initconjuntosSiguiente() {
        int i = 0;
        //carga los conjuntos siguiente en la matriz
	/*	0:json
         1:element
         2:object
         3:array
         4:attributes_list
         5:a
         6:attribute
         7:attribute_name
         8:attribute_value
         9:element_list
         10:e
         */

        conjuntosSiguiente[1][0] = "EOF";
        conjuntosSiguiente[1][1] = "COMA";
        conjuntosSiguiente[1][2] = "R_LLAVE";
        conjuntosSiguiente[1][3] = "R_CORCHETE";
        conjuntosSiguiente[2][0] = "COMA";
        conjuntosSiguiente[2][1] = "EOF";
        conjuntosSiguiente[2][2] = "R_LLAVE";
        conjuntosSiguiente[2][3] = "R_CORCHETE";
        conjuntosSiguiente[3][0] = "COMA";
        conjuntosSiguiente[3][1] = "EOF";
        conjuntosSiguiente[3][2] = "R_LLAVE";
        conjuntosSiguiente[3][3] = "R_CORCHETE";
        conjuntosSiguiente[4][0] = "R_LLAVE";
        conjuntosSiguiente[5][0] = "R_LLAVE";
        conjuntosSiguiente[6][0] = "COMA";
        conjuntosSiguiente[6][1] = "R_LLAVE";
        conjuntosSiguiente[7][0] = "DOS_PUNTOS";
        conjuntosSiguiente[8][0] = "COMA";
        conjuntosSiguiente[8][1] = "R_LLAVE";
        conjuntosSiguiente[9][0] = "R_CORCHETE";
        conjuntosSiguiente[10][0] = "R_CORCHETE";

    }

    public boolean existeTokenPrimero(int produccion, String tokenActual) {
        int i = 0;
        for (i = 0; i < 7; i++) {
            //busca el token actual en la matriz de conjunto primero
            if (tokenActual.equals(conjuntosPrimero[produccion][i])) {
                return true;
            }
        }
        return false;
    }

    public boolean existeTokenSiguiente(int produccion, String tokenActual) {

        int i = 0;
        for (i = 0; i < 4; i++) {
            //busca el token actual en la matriz de conjunto siguiente
            if ((tokenActual.equals(conjuntosSiguiente[produccion][i]))) {
                return true;
            }
        }
        return false;
    }

    public int panicModeConSincronizacion(int produccion, String tokenActual) {
        int retorno = 0;
        boolean existeSiguiente = existeTokenSiguiente(produccion, tokenActual);
        if (!existeSiguiente) {
            boolean existePrimero = existeTokenPrimero(produccion, tokenActual);
            if (!existePrimero) {
                scan();
                retorno = 1;
            }
        } else if (existeSiguiente || posicion == EOF) {
            pop();
            retorno = 0;
        }
        return retorno;
    }

    public void scan() {
        int k = 0;
        int ban = 0;
        for (k = 0; k < 12; k++) {
            if (entrada[posicion].equalsIgnoreCase(comp_lex[k])) {
                ban = 1;
            }
        }
        if (ban == 0) {
            //log4j.info("En linea %d, error_lexico\n", numLinea);
        } else {
            log4j.info("SCAN. En linea , token "+ entrada[posicion]);
            posicion++;
        }
//        anaLex();
    }

    public void pop() {
        log4j.info("Posicion " + entrada[posicion]);
        log4j.info("POP. En linea , token " + entrada[posicion]);
        //genera una produccion con EMPTY
    }

//    void sacarComillas(String original){
//	char find = '\"';
//        char *to = (char *) malloc(TAMLEX);
//        strncpy(to, original + 1, TAMLEX);
//        constchar * ptr = strchr(to, find);
//        char *nuevo = (char *) malloc(TAMLEX);
//        strncpy(nuevo, to, (ptr - to));
//        //log4j.info("%s",nuevo);
//        log4j.info(salida, "%s", nuevo);
//        //log4j.info("%s",nuevo);
//        //return nuevo;
//    }
    public void iniciarTabla() {
        comp_lex[0] = "L_CORCHETE";
        comp_lex[1] = "R_CORCHETE";
        comp_lex[2] = "L_LLAVE";
        comp_lex[3] = "R_LLAVE";
        comp_lex[4] = "COMA";
        comp_lex[5] = "DOS_PUNTOS";
        comp_lex[6] = "LITERAL_CADENA";
        comp_lex[7] = "LITERAL_NUM";
        comp_lex[8] = "PR_TRUE";
        comp_lex[9] = "PR_FALSE";
        comp_lex[10] = "PR_NULL";
        comp_lex[11] = "EOF";
    }
}
