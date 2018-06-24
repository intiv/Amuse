package amuse;

import java.util.ArrayList;

public class Simbolo{
    public String tipo;
    public String id;
    public Value valor;
    public String ambito;
        // public ArrayList<Simbolo> args = new ArrayList();

    public Simbolo(String tipo, String id, Value valor, String ambito){
        this.tipo = tipo;
        this.id = id;
        this.valor = valor;
        this.ambito = ambito;
    }

}