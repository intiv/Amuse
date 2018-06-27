package amuse;

import java.util.ArrayList;

public class Simbolo{
    public String tipo;
    public String id;
    public Value valor;
    public String ambito;
    public boolean param = false;
    public Integer offset;
        // public ArrayList<Simbolo> args = new ArrayList();

    public Simbolo(String tipo, String id, Value valor, String ambito, boolean param, Integer offset){
        this.tipo = tipo;
        this.id = id;
        this.valor = valor;
        this.ambito = ambito;
        this.param = param;
        this.offset = offset;
    }

    public boolean isParam(){
        return this.param;
    }

}