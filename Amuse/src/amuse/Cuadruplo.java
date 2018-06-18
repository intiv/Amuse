/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amuse;

/**
 *
 * @author Inti Velasquez
 */
public class Cuadruplo {
    public String operator;
    public String arg1;
    public String arg2;
    public String result;
    
    public Cuadruplo(String op, String a1, String a2, String res){
        this.operator = op;
        this.arg1 = a1;
        this.arg2 = a2;
        this.result = res;
    }

    public Cuadruplo(String op, String res){
        this.operator = op;
        this.arg1 = "";
        this.arg2 = "";
        this.result = res;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    
    
}
