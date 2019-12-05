/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizadorlexico;

import static analizadorlexico.Lexer.analizarArchivo;
import javax.swing.JFileChooser;

/**
 *
 * @author Ricardo
 */
public class AnalizadorLexico {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        /**
         * llamamos el metodo que permite cargar la ventana
         */
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(fileChooser);
        String ruta = fileChooser.getSelectedFile().getAbsolutePath();
        System.out.println(ruta);
        analizarArchivo(ruta);
        Parser parser = new Parser();
        parser.parsearArchivo(ruta.replace("fuente", "output"));
        
    }

}
