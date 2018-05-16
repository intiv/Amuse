/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amuse;

import java.util.ArrayList;

/**
 *
 * @author juany
 */
public class Nodo {
    
    public String Etiqueta;
    public String valor;
    public int idNod;
    public ArrayList<Nodo> hijos = new ArrayList<>();
    
    public Nodo() {
    }

    public Nodo(String Etiqueta, String valor, int idNod) {
        this.Etiqueta = Etiqueta;
        this.valor = valor;
        this.idNod = idNod;
    }
    
    public void addHijos(Nodo hijo){
        hijos.add(hijo);
    }
    
    public void setEtiqueta(String Etiqueta){
        this.Etiqueta = Etiqueta;
    }
    
     public String getEtiqueta() {
        return Etiqueta;
    }

    public ArrayList<Nodo> getHijos() {
        return hijos;
    }

    public void setHijos(ArrayList<Nodo> hijos) {
        this.hijos = hijos;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int getIdNod() {
        return idNod;
    }

    public void setIdNod(int idNod) {
        this.idNod = idNod;
    }
    
}
