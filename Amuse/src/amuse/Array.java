/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amuse;

/**
 *
 * @author juany
 */
public class Array {
    int inicio, fin;
    String tipo;
    public Value[] tabla;

    public Array(int inicio, int fin, String tipo) {
        this.inicio = inicio;
        this.fin = fin;
        this.tipo = tipo;
        if (this.tipo.equals("num")) {
            this.tabla = new Value[fin + 1];
        } else if (this.tipo.equals("char")) {
            this.tabla = new Value[fin + 1];
        } else if (this.tipo.equals("bool")) {
            this.tabla = new Value[fin + 1];
        }
    }

    public boolean contains(int index) {
        if (tabla[index] != null) {
            return true;
        } else {
            return false;
        }
    }

    public void addInt(int index, int value) {
        tabla[index] = new Value("num", value + "");
    }

    public void addChar(int index, char value) {
        tabla[index] = new Value("char", value + "");
    }

    public void addBool(int index, String value) {
        tabla[index] = new Value("bool", value);
    }

    public Value getValue(int index) {
        if (index >= 0 && index < fin) {
            return tabla[index];
        }
        return null;
    }

    public boolean assignValue(int index, String value) {
        if (index >= 0 && index < fin) {
            tabla[index] = new Value("bool", value);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Array{" + "inicio=" + inicio + ", fin=" + fin + ", tipo=" + tipo + ", tabla=" + toStringVals() + '}';
    }

    public String toStringVals() {
        String contenido = "";
        for (int i = 0; i < tabla.length - 1; i++) {
            contenido += tabla[i].val + ",";
        }
        contenido += tabla[tabla.length - 1].val;
        return contenido;
    }

    public int getInicio() {
        return inicio;
    }

    public void setInicio(int inicio) {
        this.inicio = inicio;
    }

    public int getFin() {
        return fin;
    }

    public void setFin(int fin) {
        this.fin = fin;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Value[] getTabla() {
        return tabla;
    }

    public void setTabla(Value[] tabla) {
        this.tabla = tabla;
    }

}
