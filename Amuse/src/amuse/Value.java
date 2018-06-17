package amuse;
public class Value{
    public String tipo;
    public String val;

    public Value(String t, String v){
        this.tipo = t;
        this.val = v;
    }

    public char getCharVal(){
        return this.val.charAt(0);
    }

    public boolean getBoolVal(){
        return Boolean.parseBoolean(this.val);
    }

    public int getIntVal(){
        return Integer.parseInt(this.val);
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getTipo() {
        return tipo;
    }

    public String getVal() {
        return val;
    }

    
}