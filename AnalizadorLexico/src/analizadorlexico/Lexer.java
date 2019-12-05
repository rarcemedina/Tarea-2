/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizadorlexico;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Ricardo
 */
public class Lexer {

    private static final String L_CORCHETE = "L_CORCHETE";
    private static final String R_CORCHETE = "R_CORCHETE";
    private static final String L_LLAVE = "L_LLAVE";
    private static final String R_LLAVE = "R_LLAVE";
    private static final String COMA = "COMA";
    private static final String DOS_PUNTOS = "DOS_PUNTOS";
    private static final String LITERAL_CADENA = "LITERAL_CADENA";
    private static final String LITERAL_NUM = "LITERAL_NUM";
    private static final String PR_TRUE = "PR_TRUE";
    private static final String PR_FALSE = "PR_FALSE";
    private static final String PR_NULL = "PR_NULL";
    private static final String EOF = "EOF";

    public static void analizarArchivo(String ruta) {
        File file = new File(ruta);
        FileReader fr = null;
        BufferedReader br = null;

        String componenteLexico = "";

        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);

        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo " + file.getName());
        }

        try {
            String lines = "";
            int corchetes = 0;
            int llaves = 0;
            int finLinea = 0;
            boolean esString = false;

            while ((lines = br.readLine()) != null) {
                System.out.println(lines);
                char[] caracteres = lines.toCharArray();
                finLinea = caracteres.length - 1;
                for (int i = 0; i < caracteres.length; i++) {
//                    System.out.println("El caracter " + i + " es " + caracteres[i]);
                    String caracter = caracteres[i] + "";
                    if (caracter.equals(" ") || caracter.equals("\t")) {
//                        System.out.println("Espacios");
                    } else if (caracter.equals("{")) {
                        componenteLexico += L_LLAVE + " ";
                        llaves++;
                    } else if (caracter.equals("[")) {
                        componenteLexico += L_CORCHETE + " ";
                        corchetes++;
                    } else if (caracter.equals("}")) {
                        componenteLexico += R_LLAVE + " ";
                        llaves--;
                    } else if (caracter.equals("]")) {
                        componenteLexico += R_CORCHETE + " ";
                        corchetes--;
                    } else if (caracter.equals(":")) {
                        componenteLexico += DOS_PUNTOS + " ";
                        corchetes++;
                    } else if (caracter.equals(",")) {
                        componenteLexico += COMA + " ";
                        corchetes++;
                    } else if (caracter.equals("\"")) {
                        esString = true;
                        while (esString) {
                            i++;
                            caracter = caracteres[i] + "";
//                            caracter = reemplazarCaracter(caracter);
//                            System.out.println(caracteres[i]);
//                            caracteres[i] = reemplazarCaracter(caracteres[i]);
                            if (!Character.isLetter(caracteres[i]) && !caracter.equals("\"")
                                    && !caracter.equals(".") && !caracter.equals("*") && !caracter.equals(" ")) {
//                                System.err.println("Error en componente lexico");
                                componenteLexico += "Error en " + LITERAL_CADENA;
                                esString = false;
                            } else if (caracter.equals("\"")) {
                                componenteLexico += LITERAL_CADENA + " ";
                                esString = false;
//                                System.err.println(componenteLexico);
                            } else if (i == finLinea) {
                                componenteLexico += "Error en " + LITERAL_CADENA;
                                esString = false;

                            }
                        }
                    } else if (Character.isDigit(caracteres[i])) {
                        boolean esNumero = true;
                        boolean necesitoNumero = false;
                        boolean recorrer = true;
                        int estado = 0;
                        caracter = caracteres[i] + "";
                        if (Character.isDigit(caracteres[i]) || caracter.equals(".") || caracter.equalsIgnoreCase("e")) {
                            while (recorrer) {
                                i++;
                                if (i <= finLinea) {
                                    caracter = caracteres[i] + "";
                                    switch (estado) {
                                        case 0:
                                            if (Character.isDigit(caracteres[i])) {
                                                esNumero = true;
                                                estado = 0;
//                                            i++;
                                            } else if (caracter.equals(".") && necesitoNumero == false) {
                                                estado = 1;
                                                necesitoNumero = true;
                                                esNumero = false;
//                                            i++;
                                            } else if (caracter.equalsIgnoreCase("e") && necesitoNumero == false) {
                                                estado = 2;
                                                necesitoNumero = true;
                                                esNumero = false;
//                                            i++;
                                            } else if (!caracter.equals(",")) {
                                                System.out.println(caracter);
                                                estado = -1;
                                                esNumero = false;
//                                            i++;
                                            } else {
                                                System.err.println("No es digito");
                                                estado = -1;
                                                i--;
                                            }
                                            break;
                                        case 1:
                                            if (Character.isDigit(caracteres[i])) {
                                                necesitoNumero = false;
                                                esNumero = true;
//                                            i++;
                                            } else if (caracter.equalsIgnoreCase("e") && necesitoNumero == false) {
                                                estado = 2;
                                                esNumero = false;
//                                            i++;
                                            } else if (!caracter.equals(",")) {
                                                estado = -1;
                                                esNumero = false;
//                                            i++;
                                            } else {
                                                System.err.println("No es digito");
                                                estado = -1;
                                                i--;
                                            }
                                            break;
                                        case 2:
                                            if (Character.isDigit(caracteres[i])) {
                                                necesitoNumero = false;
                                                esNumero = true;
//                                                i++;
                                            } else if (caracter.equals("+") || caracter.equals("-")) {
                                                estado = 3;
                                                necesitoNumero = true;
                                                esNumero = false;
//                                            i++;
                                            } else if (!caracter.equals(",")) {
                                                estado = -1;
                                                esNumero = false;
//                                            i++;
                                            } else {
                                                System.err.println("No es digito");
                                                estado = -1;
                                                i--;
                                            }
                                            break;
                                        case 3:
                                            if (Character.isDigit(caracteres[i])) {
                                                necesitoNumero = false;
                                                esNumero = true;
//                                            i++;
                                            } else if (!caracter.equals(",")) {
                                                estado = -1;
                                                esNumero = false;
//                                            i++;
                                            } else {
                                                System.err.println("No es digito");
                                                estado = -1;
                                                i--;
                                            }
                                            break;
                                        case -1:
                                            if (esNumero && i <= finLinea) {
                                                --i;
                                                recorrer = false;
                                                componenteLexico += LITERAL_NUM + " ";
                                            } else if (!esNumero) {
                                                componenteLexico += "Error en " + LITERAL_NUM + " ";
                                                recorrer = false;
                                                i = finLinea;
                                            }
                                            break;
                                    }
                                } else if (esNumero) {
//                                 --i;
                                    recorrer = false;
                                    componenteLexico += LITERAL_NUM + " ";
                                } else {
                                    recorrer = false;
                                }
//                                i++;
                            }
                        } else if (esNumero) {
                            componenteLexico += LITERAL_NUM + " ";
                        }
                    } else if (Character.isLetter(caracteres[i])) {
                        String cadena = caracteres[i] + "";
                        i++;
                        while (Character.isLetter(caracteres[i])) {
                            cadena += caracteres[i] + "";
                            i++;
                        }
                        if (cadena.equalsIgnoreCase("true")) {
                            componenteLexico += PR_TRUE + " ";
                            --i;
                        } else if (cadena.equalsIgnoreCase("false")) {
                            componenteLexico += PR_FALSE + " ";
                            --i;
                        } else if (cadena.equalsIgnoreCase("null")) {
                            componenteLexico += PR_NULL + " ";
                            --i;
                        } else {
                            componenteLexico += "Error en BOOLEANO O NULL";
                            i = finLinea;
                        }
                    }
                }
                componenteLexico += "\n";
            }

            createArchivo(componenteLexico, ruta);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(
                "Los comonentes lexicos son:{\n" + componenteLexico);
    }

    public static String getErrorLexico(String cadena) {

        return "";
    }

    public static void createArchivo(String contenido, String ruta) {
        try {
            ruta = ruta.replace("fuente", "output");
            File file = new File(ruta);
            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(contenido);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String reemplazarCaracter(String caracter) {
        String[] caracteresMalos = {"ñ", "à", "á", "À", "Á", "è", "é", "È", "É", "ì", "í", "Ì", "Í", "ò", "ó", "Ò", "Ó", "ù", "ú", "Ù", "Ú"};
        String[] caracteresBuenos = {"n", "a", "a", "A", "A", "e", "e", "E", "E", "i", "i", "I", "I", "o", "o", "O", "O", "u", "u", "U", "U"};

        for (String letraMala : caracteresMalos) {
            if (caracter.contains(letraMala)) {
                caracter = caracter.replace(letraMala, caracteresBuenos[Arrays.asList(caracteresMalos).indexOf(letraMala)]);
            }
        }

        return caracter;
    }

    public static char reemplazarCaracter(char caracter) {
        String[] caracteresMalos = {"ñ", "à", "á", "À", "Á", "è", "é", "È", "É", "ì", "í", "Ì", "Í", "ò", "ó", "Ò", "Ó", "ù", "ú", "Ù", "Ú"};
        String[] caracteresBuenos = {"n", "a", "a", "A", "A", "e", "e", "E", "E", "i", "i", "I", "I", "o", "o", "O", "O", "u", "u", "U", "U"};

        for (String letraMala : caracteresMalos) {
            String letra = caracter + "";
            if (letra.contains(letraMala)) {
                letra = letra.replace(letraMala, caracteresBuenos[Arrays.asList(caracteresMalos).indexOf(letraMala)]);
                caracter = letra.charAt(0);
            }
        }

        return caracter;
    }

}
