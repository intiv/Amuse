package amuse;

import java.util.ArrayList;

public class TablaSimbolos{
    public ArrayList<Simbolo> simbolos = new ArrayList();

    public TablaSimbolos(){
        simbolos = new ArrayList();
    }

    //Retorna indice del simbolo id en ambito
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


    //Retorna un simbolo dado el id en el ambito proporcionado
    public Simbolo getSymbol(String id, String ambito){
        int index = this.contains(id, ambito);
        if(index>=0){
            return simbolos.get(index);
        }
        return null;
    }

    //Retorna un simbolo en el rango de las funciones, en otra palabra un simbolo de funcion
    public Simbolo getFunction(String id, int nFuncs){
        for(int i = 0; i < nFuncs; i++){
            if(simbolos.get(i).id.equals(id)){
                return simbolos.get(i);
            }
        }
        return null;
    }

    public int getMaxOffset(String ambito){
        int retVal = 0;
        for(int i = 0; i < simbolos.size() ; i++){
            if(simbolos.get(i).ambito.equals(ambito)){
                retVal = simbolos.get(i).offset;
            }
        }
        return retVal;
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

    public boolean removeVar(String id, String ambito){
        for(int i = 0; i < simbolos.size(); i++){
            if(simbolos.get(i).id.equals(id) && simbolos.get(i).ambito.equals(ambito)){
                simbolos.remove(i);
                return true;
            }
        }
        return false;
    }

    //Deshabilitirar vars exepto funciones, ya no se usa
    // public void disableVars(int nFuncs){
    //     for(int i = simbolos.size() - 1; i >= nFuncs; i-- ){
    //         simbolos.get(i).disable();
    //     }
    // }


    //Agregar simbolo para variable sin offset
    public void addVar(String tipo, String id, Value valor, String ambito){
        simbolos.add(new Simbolo(tipo, id, valor, ambito, false, null));
    }

    //Agregar simbolo para variable con offset
    public void addVar(String tipo, String id, Value valor, String ambito, Integer offset){
        simbolos.add(new Simbolo(tipo, id, valor, ambito, false, offset));
    }

    //Agregar simbolo para parametro de funcion sin offset
    public void addParam(String tipo, String id, Value valor, String ambito){
        simbolos.add(new Simbolo(tipo, id, valor, ambito, true, null));
    }

    //Agregar simbolo para parametro de funcion con offset
    public void addParam(String tipo, String id, Value valor, String ambito, Integer offset){
        simbolos.add(new Simbolo(tipo, id, valor, ambito, true, offset));
    }


    // //Borrar todas las vars excepto funciones,  ya no se usa
    // public void clearVars(int nFuncs){
    //     for(int i = simbolos.size() - 1; i >= nFuncs; i--){
    //         simbolos.remove(i);
    //     }
    // }

    //Limpiar tabla de simbolos
    public void clear(){
        this.simbolos.clear();
    }

    //Asignar valor a un simbolo en la tabla
    public void assignValue(int index, Value value){
        this.simbolos.get(index).valor = value;
    }

    @Override
    public String toString(){
        String retVal = "----------------------------------------------------------------------------------------------------------------------------------------------------------------\n\t\t\tTabla de simbolos\n----------------------------------------------------------------------------------------------------------------------------------------------------------------";
        for(int i = 0; i<simbolos.size(); i++){
            Simbolo sym = simbolos.get(i);
            retVal += ("\nID: "+sym.id+", TIPO: "+sym.tipo);
            retVal += ", AMBITO: "+sym.ambito+", PARAM: "+sym.param;
            if(sym.offset != null){
                retVal += ", OFFSET: "+sym.offset.intValue();
            }else{
                retVal += ", OFFSET: -";
            }

        }
        retVal += "\n----------------------------------------------------------------------------------------------------------------------------------------------------------------\n";
        return retVal;
    }
}