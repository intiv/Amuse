package amuse;

import java.util.ArrayList;

public class TablaSimbolos{
    public ArrayList<Simbolo> simbolos = new ArrayList();

    public TablaSimbolos(){
        simbolos = new ArrayList();
    }

    public int contains(String id){
        for(int i = 0; i < simbolos.size(); i++){
            if(simbolos.get(i).id.equals(id)){
                return i;
            }
        }
        return -1;
    }

    public Simbolo getSymbol(String id){
        int index = this.contains(id);
        if(index>=0){
            return simbolos.get(index);
        }
        return null;
    }

    public int getIndexVal(String id){
        try {
            int index = this.contains(id);
            if(index>=0){
                return Integer.parseInt(simbolos.get(index).valor.val);
            }
            //no existe
            return -2;
        } catch (Exception e) {
            //no es arreglo definitivamente
            return -1;
        }        
    }

//    public void addFunction(String tipo, String id, ArrayList<String> args){
//        
//        simbolos.add(new Simbolo(tipo, id, args));
//    }

    public boolean removeVar(String id){
        for(int i = 0; i < simbolos.size(); i++){
            if(id.equals(simbolos.get(i).id)){
                simbolos.remove(i);
                return true;
            }
        }
        return false;
    }

    public void addVar(String tipo, String id, Value valor){
        simbolos.add(new Simbolo(tipo, id, valor));
    }

    public void clearVars(int nFuncs){
        for(int i = simbolos.size() - 1; i > nFuncs-1; i--){
            simbolos.remove(i);
        }
    }

    public void clear(){
        this.simbolos.clear();
    }

    public void assignValue(int index, Value value){
        this.simbolos.get(index).valor = value;
    }

    @Override
    public String toString(){
        String retVal = "\tTabla de simbolos\n----------------------------------------";
        for(int i = 0; i<simbolos.size(); i++){
            Simbolo sym = simbolos.get(i);
            retVal += ("\nID: "+sym.id+", TIPO: "+sym.tipo);
            if(sym.valor != null){
                retVal+=", VALOR: "+sym.valor.val;
            }
        }
        retVal += "\n----------------------------------------\n";
        return retVal;
    }
}