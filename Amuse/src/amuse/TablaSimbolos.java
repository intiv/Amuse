package amuse;

import java.util.ArrayList;

public class TablaSimbolos{
    public ArrayList<Simbolo> simbolos = new ArrayList();

    public TablaSimbolos(){
        simbolos = new ArrayList();
    }

    public int contains(String id, String ambito){
        int temp = -1;
        for(int i = 0; i < simbolos.size(); i++){
            
            if(simbolos.get(i).id.equals(id)){
                if(simbolos.get(i).ambito.equals(ambito)){
                    return i;
                }else if(simbolos.get(i).ambito.equals("global")){
                    temp = i;
                }
            }
        }
        return temp;
    }

    public Simbolo getSymbol(String id, String ambito){
        int index = this.contains(id, ambito);
        if(index>=0){
            return simbolos.get(index);
        }
        return null;
    }

    public Simbolo getFunction(String id, int nFuncs){
        for(int i = 0; i < nFuncs; i++){
            if(simbolos.get(i).id.equals(id)){
                return simbolos.get(i);
            }
        }
        return null;
    }

    public int getIndexVal(String id, String ambito){
        try {
            int index = this.contains(id, ambito);
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

    public boolean removeVar(String id, String ambito){
        for(int i = 0; i < simbolos.size(); i++){
            if(simbolos.get(i).id.equals(id) && simbolos.get(i).ambito.equals(ambito)){
                simbolos.remove(i);
                return true;
            }
        }
        return false;
    }

    // public void disableVars(int nFuncs){
    //     for(int i = simbolos.size() - 1; i >= nFuncs; i-- ){
    //         simbolos.get(i).disable();
    //     }
    // }

    public void addVar(String tipo, String id, Value valor, String ambito){
        simbolos.add(new Simbolo(tipo, id, valor, ambito));
    }

    public void clearVars(int nFuncs){
        for(int i = simbolos.size() - 1; i >= nFuncs; i--){
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
            retVal += ", AMBITO: "+sym.ambito;
        }
        retVal += "\n----------------------------------------\n";
        return retVal;
    }
}