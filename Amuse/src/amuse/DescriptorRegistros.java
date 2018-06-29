package amuse;


import java.util.ArrayList;

public class DescriptorRegistros{
    String[] ts = new String[10];
    String[] as = new String[4];
    String[] ss = new String[8];


    public DescriptorRegistros(){
        for(int i = 0; i < 10; i++){
            ts[i] = "";
        }
        for(int i = 0; i < 4; i++){
            as[i] = "";
        }
        for(int i = 0; i < 8; i++){
            ss[i] = "";
        }
    }

    public int getFreeT(){
        for(int i = 0; i < 10; i++){
            if(ts[i].equals("")){
                return i;
            }
        }
        return -1;
    }

    public void freeTS(){
        for(int i = 0; i < 10; i++){
            ts[i] = "";
        }
    }
    
    public int getFreeA(){
        for(int i = 0; i < 4; i++){
            if(as[i].equals("")){
                return i;
            }
        }
        return -1;
    }

    public void freeAS(){
        for(int i = 0; i < 4; i++){
            as[i] = "";
        }
    }

    public int getFreeS(){
        for(int i = 0; i < 8; i++){
            if(ss[i].equals("")){
                return i;
            }
        }
        return -1;
    }

    public void freeSS(){
        for(int i = 0; i < 8; i++){
            ss[i] = "";
        }
    }
}