package amuse;

import java.util.ArrayList;

public class Expresion{
    public ArrayList<Integer> listav = new ArrayList();
    public ArrayList<Integer> listaf = new ArrayList();
    public int cuad;
    public boolean or = false;

    public Expresion(boolean or){
        listav = new ArrayList();
        listaf = new ArrayList();
        this.or = or;
    }

    public Expresion(){
        listav = new ArrayList();
        listaf = new ArrayList();
    }
    
    @Override
    public String toString(){
        String retVal = "Listav:\n";
        for(int i = 0; i < listav.size(); i++){
            retVal+=listav.get(i).intValue()+" ";
        }
        retVal += "\nListaf:\n";
        for(int i = 0; i < listaf.size(); i++){
            retVal+=listaf.get(i).intValue()+" ";
        }
        retVal+="\n";
        return retVal;
    }
    
}