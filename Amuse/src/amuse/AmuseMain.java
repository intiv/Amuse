/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amuse;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;

/**
 *
 * @author Inti Velasquez
 */
public class AmuseMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
        File file = new File("./src/amuse/in.txt");
        FileReader fr = new FileReader(file);
        scanner lex = new scanner(fr);
        Sintactico sint = new Sintactico(lex);
        Symbol simbolo = sint.parse();
        System.out.println(simbolo);
        }catch(IOException ioex){
            ioex.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(AmuseMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
