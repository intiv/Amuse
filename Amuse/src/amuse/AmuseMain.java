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
//            try{
//                String cmd = "";
//                Runtime.getRuntime().exec(cmd);
//            }catch(IOException e){
//                System.out.println(e);
//            }
            File file = new File("./src/amuse/in.txt");
            FileReader fr = new FileReader(file);
            scanner lex = new scanner(fr);
            Sintactico amuse = new Sintactico(lex);
            Symbol simbolo = amuse.parse();
            amuse.printCuadruplos();
            CodigoFinal(amuse);

        //    if(sint.hayErrores==0){
        //        System.out.println("No hubieron errores");
        //        Nodo raiz = sint.padre;
        //        Graficar(recorrido1(raiz),"Amuse Tree");
        //    }
            // System.out.println(simbolo);
        } catch (Exception ex) {
            Logger.getLogger(AmuseMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String recorrido1(Nodo raiz){
        String cuerpo = "";
        for(Nodo hijos:raiz.hijos){
            cuerpo += raiz.idNod +"[label =\""+cambiar(raiz.valor)+"("+raiz.Etiqueta+")\"]\n";
                    cuerpo += hijos.idNod +"[label =\""+cambiar(hijos.valor)+"("+hijos.Etiqueta+")\"]\n";
                    cuerpo += raiz.idNod + "->" +hijos.idNod+"\n";
                    cuerpo +=  recorrido1(hijos);                
        }
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

    public static void CodigoFinal(Sintactico amuse){
        int cont = 0;
        int operation = 10;
        int maxoffset = amuse.tabla.getMaxOffset("Main");

        String code = ".text\n.globl main\nmain:\n\tmove $fp, $sp\n\tsub $sp, $sp, "+maxoffset+"\n";
        for(Cuadruplo cuad : amuse.cuadruplos){
            code+= "_etiq"+cont+":\n";
            if(cuad.operator.equals("READ")){
                Simbolo sym = amuse.tabla.getSymbol(cuad.result, "Main");

                if(sym.tipo.equals("num") || sym.tipo.equals("bool")){
                    code += line("li $v0, 5") +
                            line("syscall") +
                            line("sw $v0, -"+sym.offset+"($fp)");
                }else if(sym.tipo.equals("char")){
                    // 
                }
                
            }else if(cuad.operator.equals("WRITE")){
                Simbolo sym = amuse.tabla.getSymbol(cuad.result, "Main");
                if(sym.tipo.equals("num") || sym.tipo.equals("bool")){
                    code += line("lw $a0, -"+sym.offset+"($fp)") +
                            line("li $v0, 1") +
                            line("syscall");
                }
            }else if(cuad.operator.equals("GOTO")){
                code += line("b _etiq"+cuad.result);
            }else if(cuad.operator.contains("IF")){
                boolean isid1 = amuse.isID(cuad.arg1);
                boolean isid2 = amuse.isID(cuad.arg2);
                if(isid1){
                    Simbolo id1 = amuse.tabla.getSymbol(cuad.arg1, "Main");
                    if(id1.tipo.equals("num") || id1.tipo.equals("bool")){
                        code += line("lw $t0, -"+id1.offset+"($fp)");
                    }
                    //else if tipo.equals("char") cargar asciiz
                }else{
                    code += line("li $t0, "+cuad.arg1); //Se asume que es numero/bool, falta char
                }
                if(isid2){
                    Simbolo id2 = amuse.tabla.getSymbol(cuad.arg2, "Main");
                    if(id2.tipo.equals("num") || id2.tipo.equals("bool")){
                        code += line("lw $t1, -"+id2.offset+"($fp)");
                    }
                    //else if tipo.equals("char") cargar asciiz
                }else{
                    code += line("li $t1, "+cuad.arg2);
                } 
                String tipoIf = cuad.operator.substring(2, cuad.operator.length());
                if(tipoIf.equals("<")){
                    code += line("blt $t0, $t1, _etiq"+cuad.result);
                }else if(tipoIf.equals("<=")){
                    code += line("ble $t0, $t1, _etiq"+cuad.result);
                }else if(tipoIf.equals(">")){
                    code += line("bgt $t0, $t1, _etiq"+cuad.result);
                }else if(tipoIf.equals(">=")){
                    code += line("bge $t0, $t1, _etiq"+cuad.result);
                }else if(tipoIf.equals(":=")){
                    code += line("beq $t0, $t1, _etiq"+cuad.result);
                }else if(tipoIf.equals("!=")){
                    code += line("bne $t0, $t1, _etiq"+cuad.result);
                }
            }else if(cuad.operator.equals(":=")){
                Simbolo sym = amuse.tabla.getSymbol(cuad.result, "Main");
                if(cuad.arg1.contains("$t")){
                    code += line("sw "+cuad.arg1+", -"+sym.offset+"($fp)");
                }else{
                    if(isID(cuad.arg1)){

                    }else{
                        code += line("li $t0, "+cuad.arg1) +
                                line("sw $t0, -"+sym.offset+"($fp)");
                    }
                    //se asume es numero, falta char
                    
                }
                
                // if(cuad.arg1.contains("$t") || cuad.arg2.contains("$t")){
                    
                //     //obtener valor de temporal adecuado 
                // }else{

                // }

            }
            cont++;
        }
        System.out.println(code);

    }

    public static String line(String code){
        return "\t"+code+"\n";
    }
    
    public static void Graficar(String cadena, String cad){
        FileWriter fichero = null;
        PrintWriter pw = null;
        String nombre = cad;
        String archivo = nombre+".dot";
        try{
            fichero = new FileWriter(archivo);
            pw = new PrintWriter(fichero);
            pw.println("diagraph G {node[shape=box, stile=filled, color=Gray95]; edge[color=black];rankdir=TB \n}");
            pw.println(cadena);
            pw.println("\n");
        }catch(Exception e){
            System.out.println(e);
        }
        try{
            String cmd = "dot.exe -Tpng "+nombre + "dot -o " + cad + ".png";
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
