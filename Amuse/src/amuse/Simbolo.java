package amuse;

import java.util.ArrayList;

public class Simbolo{
    public String tipo;
    public String id;
    public Value valor;
        // public ArrayList<Simbolo> args = new ArrayList();

    public Simbolo(String tipo, String id, Value valor){
        this.tipo = tipo;
        this.id = id;
        this.valor = valor;
    }

    // public Simbolo(String tipo, String id, ArrayList<Simbolo> args){
    //     this.tipo = tipo;
    //     this.id = id;
    //     this.args = args;
    // }
}