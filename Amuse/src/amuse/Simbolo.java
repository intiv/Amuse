package amuse;

import java.util.ArrayList;

public class Simbolo{
    public String tipo;
    public String id;
    public Value valor;
    public boolean enabled;
        // public ArrayList<Simbolo> args = new ArrayList();

    public Simbolo(String tipo, String id, Value valor){
        this.tipo = tipo;
        this.id = id;
        this.valor = valor;
        this.enabled = true;
    }

    public void disable(){
        this.enabled = false;
    }

    public void enable(){
        this.enabled = true;
    }

    // public Simbolo(String tipo, String id, ArrayList<Simbolo> args){
    //     this.tipo = tipo;
    //     this.id = id;
    //     this.args = args;
    // }
}