/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amuse;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
//            buildParser();
            File file = new File("./src/amuse/Ejemplos/ejemplo1.txt");
            FileReader fr = new FileReader(file);
            scanner lex = new scanner(fr);
            Sintactico sint = new Sintactico(lex);
            Symbol simbolo = sint.parse();
            if(sint.hayErrores==0){
                System.out.println("No hubieron errores");
                Nodo raiz = sint.padre;
                Graficar(recorrido1(raiz),"Amuse_Tree");
            }
            System.out.println(simbolo);
        } catch (Exception ex) {
            Logger.getLogger(AmuseMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String recorrido1(Nodo raiz){
        String cuerpo = "";
        cuerpo += "nodo"+raiz.idNod +"[label=\""+cambiar(raiz.valor)+"("+raiz.Etiqueta+")\"];\n";
//        for(Nodo hijos:raiz.hijos){
//            if(hijos.Etiqueta!=null){
//                cuerpo += raiz.idNod +"[label=\""+cambiar(raiz.valor)+"("+raiz.Etiqueta+")\"]\n";
//                cuerpo += hijos.idNod +"[label=\""+cambiar(hijos.valor)+"("+hijos.Etiqueta+")\"]\n";
//                cuerpo += raiz.idNod + "->" +hijos.idNod+"\n";
//                cuerpo +=  recorrido1(hijos);    
//            }else{
//                System.out.println(raiz.Etiqueta);
//            }
//                        
//       }
        return cuerpo;
    }
    
    public static String cambiar(String texto){
        if(texto != null){
            texto = texto.replaceAll("\"","");
            
        }else{
            texto ="";
        }
        return texto;
    }
    
    public static void Graficar(String cadena, String cad){
        FileWriter fichero = null;
        PrintWriter pw = null;
        String nombre = cad;
        String archivo = nombre+".dot";
        try{
            fichero = new FileWriter(archivo);
            pw = new PrintWriter(fichero);
            pw.println("digraph G {node[shape=box, style=filled, color=Gray95]; edge[color=black];rankdir=TB \n");
            pw.println(cadena);
            pw.println("\n }");
            fichero.close();
        }catch(Exception e){
            System.out.println(e);
        }
        try{
            //String cmd = "dot -Tpng Amuse_Tree.dot -o Amuse_Tree.png";
            String cmd = "dot -Tpng "+ nombre + ".dot -o " + cad + ".png";
            Runtime.getRuntime().exec(cmd);
        }catch(IOException e){
            System.out.println(e);
        }
    }
    
    
    synchronized private static void buildParser() {
       String params[] = new String[7];

       params[0] = "-destdir";
       params[1] = "src/amuse/";
       params[2] = "-parser";
       params[3] = "Sintactico";
       params[4] = "-symbols";
       params[5] = "Amuse";
       
       params[6] = "src/amuse/AmuseSyntactic.cup";
       try {
           java_cup.Main.main(params);
       } catch (Exception ex) {
           Logger.getLogger(Amuse.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
    
}
