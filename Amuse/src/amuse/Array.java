/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amuse;

/**
 *
 * @author juany
 */
public class Array {
    int inicio, fin;
    String tipo;
    public Value[] tabla;
    
    public Array(int inicio, int fin, String tipo) {
        this.inicio = inicio;
        this.fin = fin;
        this.tipo = tipo;
        if(this.tipo.equals("num")){
            this.tabla= new Value[fin];
        }else if(this.tipo.equals("char")){
            this.tabla= new Value[fin];
        }else if(this.tipo.equals("bool")){
            this.tabla= new Value[fin];
        }
    }

    public boolean contains(int index){
        if(tabla[index]!=null){
            return true;
        }else{
            return false;
        }
    }

    public void addInt(int index, int value){
        tabla[index] = new Value("num", value+"");
    }

    public void addChar(int index, char value){
        tabla[index] = new Value("char", value+"");
    }

    public void addBool(int index, String value){
        if(value.equals("true") || value.equals("false")){
            tabla[index] = new Value("bool", value);
        }
    }

    public Value getValue(int index){
        if(index>=0 && index<fin){
            return tabla[index];
        }
        return null;
    }
    
    
}
