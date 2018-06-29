package amuse;

import java.util.ArrayList;

public class Parametros{
    public ArrayList<String> tipos = new ArrayList();
    public ArrayList<String> ids = new ArrayList();
    public int offset = -1;

    public Parametros(ArrayList<String> tipos, ArrayList<String> ids){
        this.tipos = tipos;
        this.ids = ids;
    }

    public Parametros(){
        this.tipos = new ArrayList();
        this.ids = new ArrayList();
    }

    @Override
    public String toString(){
        return "Parametro - tipos: "+tipos+", ids: "+ids+"\n";
    }
}