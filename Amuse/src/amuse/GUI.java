/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amuse;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import java.util.ArrayList;
/**
 *
 * @author juany
 */
public class GUI extends javax.swing.JFrame {

    /**
     * Creates new form GUI
     */
    public GUI() {
        initComponents();
    }
    public File file = null;
    Sintactico amuse = null;
    DescriptorRegistros desc = new DescriptorRegistros();
    String currAmbito = "Main";
    int inc = 0;
    
    public String CodigoFinal(){
        int cont = 0;
        int funCont = 0;
        int maxoffset = amuse.tabla.getMaxOffset("Main");
        String code = ".text\n.globl main\nmain:\n\tmove $fp, $sp\n\tsub $sp, $sp, "+maxoffset+"\n";
        boolean finMain = false;
        boolean writeFinFunc = true;
        boolean lastFunc = false;
        boolean writeFinMain = false;
        if(amuse.funciones.size() == 0){
            writeFinMain = true;
        }
        for(Cuadruplo cuad : amuse.cuadruplos){
            if(amuse.cuadFuncs.size() > 0 && amuse.cuadFuncs.size() == amuse.funciones.size()){
                if((!finMain &&amuse.cuadFuncs.contains(new Integer(cont)))|| (finMain && amuse.cuadFuncs.contains(new Integer(cont-1))) ){
                    
                    if(!currAmbito.equals("Main") && writeFinFunc ){
                        code += "_FIN_FUN_"+currAmbito+":\n"+line("add $sp, $sp, "+maxoffset);
                        for (int i = 0; i < amuse.funciones.get(funCont-1).params.ids.size(); i++) {
                            if(i<8){
                                code += line("lw $s"+i+", -"+((i+1)*4+8)+"($fp)");
                            }
                            desc.freeSS();
                        }
                        code += line("lw $ra, -8($fp)") + line("lw $fp, -4($fp)") + line("jr $ra\n");
                    }
                    if(!finMain){
                        code += "_etiq"+cont+":\n" + line("li $v0, 10\n\tsyscall\n");
                        cont++;
                        inc = 1;
                        writeFinFunc = true;
                    }

                    currAmbito = amuse.funciones.get(funCont).nombre;
                    maxoffset = amuse.tabla.getMaxOffset(currAmbito);
                    code += "_FUN_"+currAmbito+":\n";
                    code += line("sw $fp, -4($sp)") + line("sw $ra, -8($sp)");
                    for (int i = 0; i < amuse.funciones.get(funCont).params.ids.size(); i++) {
                        //if(i < 4){
                            //primero 4 parametros, se deberia multiplicar i por el tamaño del tipo de param, por ahora se asume que son nums por que nums funcionan bien
                        code += line("sw $s"+i+", -"+((i+1)*4+8)+"($sp)");
                        desc.ss[i] = "";
                    }
                    int contParam = 1;
                    for (int i = 0; i < amuse.funciones.get(funCont).params.ids.size(); i++) {
                        if(i < 4){
                            code += line("move $s"+i+", $a"+i);
                            desc.ss[i] = amuse.funciones.get(funCont).params.ids.get(i);
                            desc.as[i] = "";
                        }else{
                            contParam = amuse.funciones.get(funCont).params.ids.size() - i;
                            code += line("lw $s"+i+", +"+(contParam*4)+"($sp)");
                            desc.ss[i] = amuse.funciones.get(funCont).params.ids.get(i);

                        }
                    }
                    code += line("move $fp, $sp") + line("sub $sp, $sp, "+(maxoffset));
                    

//                    amuse.cuadFuncs.remove(0);
//                    amuse.funciones.remove(0);
                    if(amuse.funciones.size() == 1){
                        lastFunc = true;
                    }
                    funCont++;
                    finMain = true;
                }
            }
            code+= "_etiq"+cont+":\n";
            if(cuad.operator.equals("READ")){
                code += genREAD(cuad);
            }else if(cuad.operator.equals("WRITE")){
                code += genWRITE(cuad);
            }else if(cuad.operator.equals("GOTO")){
                code += line("b _etiq"+(Integer.parseInt(cuad.result)+inc));
            }else if(cuad.operator.contains("IF")){
                code += genIF(cuad);
            }else if(cuad.operator.equals(":=")){
                code += genASIG(cuad);
            }else if(cuad.operator.equals("+")){
                code += genSUMA(cuad);
            }else if(cuad.operator.equals("*")){
                code += genMULT(cuad);
            }else if(cuad.operator.equals("/")){
                code += genDIV(cuad);
            }else if(cuad.operator.equals("-")){
                code += genRESTA(cuad);
            }else if(cuad.operator.equals("RETURN")){
                code += genRET(cuad);
            }
            cont++;
        }
        if(writeFinMain){
            code+= "_etiq"+cont+":\n" + line("li $v0, 10\n\tsyscall\n");
        }
        if(funCont > 1 || lastFunc){
            funCont--;
            code += "_FIN_FUN_"+currAmbito+":\n"+line("add $sp, $sp, "+maxoffset);
            for (int i = 0; i < amuse.funciones.get(funCont).params.ids.size(); i++) {
                if(i < 8){
                    code += line("lw $s"+i+", "+((i+1)*4+8)+"($fp)");
                }
            }
            code += line("lw $ra, -8($fp)") + line("lw $fp, -4($fp)") + line("jr $ra\n");
        }
        code += ".data\n_bufferChars:\t.space\t1\n";
        return code;

    }

    public String line(String code){
        return "\t"+code+"\n";
    }
    
    public String genREAD(Cuadruplo cuad){
        Simbolo sym = amuse.tabla.getSymbol(cuad.result, currAmbito);
        String retVal = "";
        if(sym.tipo.equals("num") || sym.tipo.equals("bool")){
            retVal = line("li $v0, 5") +
                    line("syscall");
            if(!sym.param){
                retVal += line("sw $v0, -"+sym.offset+"($fp)");
            }else{
                int s_ind = desc.getS(sym.id);
                retVal += line("move $s"+s_ind+", $v0");
            }
        }else if(sym.tipo.equals("char")){
            // 
        }
        return retVal;
    }

    public String genWRITE(Cuadruplo cuad){
        Simbolo sym = amuse.tabla.getSymbol(cuad.result, currAmbito);
        String retVal = "";
        if(sym.tipo.equals("num") || sym.tipo.equals("bool")){
            if(sym.param){
                int s_id = desc.getS(sym.id);
                retVal = line("move $a0, $s"+s_id);
            }else{
                retVal = line("lw $a0, -"+sym.offset+"($fp)");
            }
            retVal +=  line("li $v0, 1") + line("syscall");
            retVal += line("addi $a0, $0, 0xA") + line("addi $v0, $0, 0xB") + line("syscall"); //Print new line
        }
        return retVal;
    }

    public String genIF(Cuadruplo cuad){
        String retVal = "";
        boolean isid1 = amuse.isID(cuad.arg1);
        boolean isid2 = amuse.isID(cuad.arg2);
        int temp1 = desc.getFreeT();
        String ifArg1 = "";
        String ifArg2 = "";
        if(temp1 > -1){
            desc.ts[temp1] = " ";
        }else{
            temp1 = 0;
            desc.ts[0] = " ";
        }
        if(isid1){
            Simbolo id1 = amuse.tabla.getSymbol(cuad.arg1, currAmbito);
            if(id1 == null){
                System.out.println("id1 null en cuad: "+amuse.cuadruplos.indexOf(cuad));
            }
            if(id1.tipo.equals("num") || id1.tipo.equals("bool")){
                if(!id1.param){
                    retVal += line("lw $t"+temp1+", -"+id1.offset+"($fp)");
                    ifArg1 = "$t"+temp1;
                }else{
                    int s_ind1 = desc.getS(id1.id);
                    ifArg1 = "$s"+s_ind1;
                }
            }
            //else if tipo.equals("char") cargar asciiz
        }else{
            retVal += line("li $t"+temp1+", "+cuad.arg1); //Se asume que es numero/bool, falta char
            ifArg1 = "$t"+temp1;
        }
        int temp2 = desc.getFreeT();
        if(temp2 > -1){
            desc.ts[temp2] = " ";
        }else{
            temp2 = 1;
            desc.ts[1] = " ";
        }
        if(isid2){
            Simbolo id2 = amuse.tabla.getSymbol(cuad.arg2, currAmbito);
            if(id2.tipo.equals("num") || id2.tipo.equals("bool")){
                if(!id2.param){
                    retVal += line("lw $t"+temp2+", -"+id2.offset+"($fp)");
                    ifArg2 = "$t"+temp2;
                }else{
                    int s_ind2 = desc.getS(id2.id);
                    ifArg2 = "$s"+s_ind2;
                }
            }
            //else if tipo.equals("char") cargar asciiz
        }else{
            retVal += line("li $t"+temp2+", "+cuad.arg2);
            ifArg2 = "$t"+temp2;
        } 
        String tipoIf = cuad.operator.substring(2, cuad.operator.length());
        if(tipoIf.equals("<")){
            retVal += line("blt "+ifArg1+", "+ifArg2+", _etiq"+(Integer.parseInt(cuad.result)+inc));
        }else if(tipoIf.equals("<=")){
            retVal += line("ble "+ifArg1+", "+ifArg2+", _etiq"+(Integer.parseInt(cuad.result)+inc));
        }else if(tipoIf.equals(">")){
            retVal += line("bgt "+ifArg1+", "+ifArg2+", _etiq"+(Integer.parseInt(cuad.result)+inc));
        }else if(tipoIf.equals(">=")){
            retVal += line("bge "+ifArg1+", "+ifArg2+", _etiq"+(Integer.parseInt(cuad.result)+inc));
        }else if(tipoIf.equals("==")||tipoIf.equals(":=")){
            retVal += line("beq "+ifArg1+", "+ifArg2+", _etiq"+(Integer.parseInt(cuad.result)+inc));
        }else if(tipoIf.equals("!=")){
            retVal += line("bne "+ifArg1+", "+ifArg2+", _etiq"+(Integer.parseInt(cuad.result)+inc));
        }
        desc.freeTS();
        return retVal;
    }

    public String genASIG(Cuadruplo cuad){
        String retVal = "";
        Simbolo sym = amuse.isID(cuad.result) ? amuse.tabla.getSymbol(cuad.result, currAmbito) : null;
        if(cuad.arg1.contains("$t") && sym != null){
            // retVal = line("sw "+cuad.arg1+", -"+sym.offset+"($fp)");
        }else if(sym != null){
            if(amuse.isID(cuad.arg1)){
                Simbolo sym2 = amuse.tabla.getSymbol(cuad.arg1, currAmbito);
                if(!sym.param && !sym2.param){
                    retVal = line("lw $t0, -"+sym2.offset+"($fp)") +
                            line("sw $t0, -"+sym.offset+"($fp)");
                }else if(!sym.param && sym2.param){
                    int s_ind = desc.getS(sym2.id);
                    retVal = line("sw $s"+s_ind+", -"+sym.offset+"($fp)");
                }else if(sym.param && sym2.param){
                    int s_ind1 = desc.getS(sym.id);
                    int s_ind2 = desc.getS(sym2.id);
                    retVal = line("move $s"+s_ind1+", $s"+s_ind2);
                }
            }else{
                String arg1 = cuad.arg1.equals("true") ? "1" : (cuad.arg1.equals("false")? "0" : cuad.arg1);
                if(!sym.param){
                retVal = line("li $t0, "+arg1) +
                        line("sw $t0, -"+sym.offset+"($fp)");
                }else{
                    int s_ind = desc.getS(sym.id);
                    retVal = line("li $s"+s_ind+", "+arg1);
                }
            }
            //se asume es numero, falta char
        }
        return retVal;
    }   

    public String genSUMA(Cuadruplo cuad){
        String retVal = "";
        //add $t2, $t2, 1
        return retVal;
    }

    public String genRESTA(Cuadruplo cuad){
        String retVal = "";

        return retVal;
    }

    public String genMULT(Cuadruplo cuad){
        String retVal = "";
    
        return retVal;
    }

    public String genDIV(Cuadruplo cuad){
        String retVal = "";

        return retVal;
    }

    public String genRET(Cuadruplo cuad){
        String retVal = "";
        if(amuse.isID(cuad.result)){
            Simbolo sym = amuse.tabla.getSymbol(cuad.result, currAmbito);
            if(sym.param){
                int s_ind = desc.getS(sym.id);
                retVal = line("move $v0, $s"+s_ind);
            }else{
                retVal = line("lw $v0, -"+sym.offset+"($fp)");
            }
        }else{
            retVal = line("li $v0, "+cuad.result);
        }
        retVal += line("b _FIN_FUN_"+currAmbito);
        return retVal;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jf_codigoIntermedio = new javax.swing.JFrame();
        lb_titulo1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        ta_codigoIntermedio = new javax.swing.JTextArea();
        btn_fileChooser = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ta_codigoFinal = new javax.swing.JTextArea();
        btn_codigoInt = new javax.swing.JButton();
        lb_titulo = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ta_archivo = new javax.swing.JTextArea();
        lb_codigoFinal = new javax.swing.JLabel();
        lb_archivo = new javax.swing.JLabel();
        btn_compilar1 = new javax.swing.JButton();
        lb_background = new javax.swing.JLabel();

        jf_codigoIntermedio.setMinimumSize(new java.awt.Dimension(600, 600));

        lb_titulo1.setBackground(new java.awt.Color(153, 153, 153));
        lb_titulo1.setFont(new java.awt.Font("Rockwell", 1, 60)); // NOI18N
        lb_titulo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_titulo1.setText("Codigo Intermedio");

        ta_codigoIntermedio.setColumns(20);
        ta_codigoIntermedio.setRows(5);
        jScrollPane3.setViewportView(ta_codigoIntermedio);

        javax.swing.GroupLayout jf_codigoIntermedioLayout = new javax.swing.GroupLayout(jf_codigoIntermedio.getContentPane());
        jf_codigoIntermedio.getContentPane().setLayout(jf_codigoIntermedioLayout);
        jf_codigoIntermedioLayout.setHorizontalGroup(
            jf_codigoIntermedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lb_titulo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jf_codigoIntermedioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jf_codigoIntermedioLayout.setVerticalGroup(
            jf_codigoIntermedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jf_codigoIntermedioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lb_titulo1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1300, 800));
        setPreferredSize(new java.awt.Dimension(1300, 800));
        setSize(new java.awt.Dimension(1300, 800));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_fileChooser.setBackground(new java.awt.Color(153, 153, 153));
        btn_fileChooser.setFont(new java.awt.Font("Rockwell Condensed", 0, 28)); // NOI18N
        btn_fileChooser.setText("Ingresar Archivo");
        btn_fileChooser.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btn_fileChooser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_fileChooserMouseClicked(evt);
            }
        });
        getContentPane().add(btn_fileChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 300, 80));

        ta_codigoFinal.setColumns(20);
        ta_codigoFinal.setRows(5);
        jScrollPane1.setViewportView(ta_codigoFinal);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 110, 420, 550));

        btn_codigoInt.setBackground(new java.awt.Color(153, 153, 153));
        btn_codigoInt.setFont(new java.awt.Font("Rockwell Condensed", 0, 28)); // NOI18N
        btn_codigoInt.setText("Codigo Intermedio");
        btn_codigoInt.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btn_codigoInt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_codigoIntMouseClicked(evt);
            }
        });
        getContentPane().add(btn_codigoInt, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 440, 300, 80));

        lb_titulo.setFont(new java.awt.Font("Rockwell", 1, 60)); // NOI18N
        lb_titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_titulo.setText("AMUSE");
        getContentPane().add(lb_titulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 20, 870, 70));

        ta_archivo.setEditable(false);
        ta_archivo.setColumns(20);
        ta_archivo.setRows(5);
        jScrollPane2.setViewportView(ta_archivo);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 110, 420, 550));

        lb_codigoFinal.setFont(new java.awt.Font("Perpetua Titling MT", 0, 18)); // NOI18N
        lb_codigoFinal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lb_codigoFinal.setText("codigo final");
        getContentPane().add(lb_codigoFinal, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 670, 200, -1));

        lb_archivo.setFont(new java.awt.Font("Perpetua Titling MT", 0, 18)); // NOI18N
        lb_archivo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lb_archivo.setText("Archivo");
        getContentPane().add(lb_archivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 670, 200, -1));

        btn_compilar1.setBackground(new java.awt.Color(153, 153, 153));
        btn_compilar1.setFont(new java.awt.Font("Rockwell Condensed", 0, 28)); // NOI18N
        btn_compilar1.setText("Compilar");
        btn_compilar1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btn_compilar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_compilar1MouseClicked(evt);
            }
        });
        getContentPane().add(btn_compilar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 340, 300, 80));

        lb_background.setBackground(new java.awt.Color(124, 167, 167));
        getContentPane().add(lb_background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1470, 840));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_fileChooserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_fileChooserMouseClicked
        ta_archivo.setText("");
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        fileChooser.setFileFilter(filter);
        
        File workingDirectory = new File(System.getProperty("user.dir"));
        fileChooser.setCurrentDirectory(workingDirectory);
        int Okoption;

        Okoption = fileChooser.showOpenDialog(this);
        String texto = "";
        if (Okoption == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
                String line = null;
                int cont = 1;
                while ((line = br.readLine()) != null) {
                    texto+=cont+".     "+line+"\n";
                    cont++;
                }
                ta_archivo.setText(texto);
             }catch(IOException e){
                 e.printStackTrace();
             }
            
        }
    }//GEN-LAST:event_btn_fileChooserMouseClicked

    private void btn_compilar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_compilar1MouseClicked
        try{
            FileReader fr = new FileReader(file);
            scanner lex = new scanner(fr);
            amuse = new Sintactico(lex);
            Symbol simbolo = amuse.parse();
            if(!amuse.hayErrores){
                ta_codigoFinal.setText(CodigoFinal());
            }else{
                ta_codigoFinal.setText(amuse.Errores);
            }
            
            // System.out.println(simbolo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btn_compilar1MouseClicked

    private void btn_codigoIntMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_codigoIntMouseClicked
        jf_codigoIntermedio.pack();
        jf_codigoIntermedio.show(true);
        ta_codigoIntermedio.setText("");
        ta_codigoIntermedio.setText("");
        if(amuse != null){
            ta_codigoIntermedio.setText(amuse.tabla.toString()+"\n"+amuse.printCuadruplos());
        }
    }//GEN-LAST:event_btn_codigoIntMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_codigoInt;
    private javax.swing.JButton btn_compilar1;
    private javax.swing.JButton btn_fileChooser;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JFrame jf_codigoIntermedio;
    private javax.swing.JLabel lb_archivo;
    private javax.swing.JLabel lb_background;
    private javax.swing.JLabel lb_codigoFinal;
    private javax.swing.JLabel lb_titulo;
    private javax.swing.JLabel lb_titulo1;
    private javax.swing.JTextArea ta_archivo;
    private javax.swing.JTextArea ta_codigoFinal;
    private javax.swing.JTextArea ta_codigoIntermedio;
    // End of variables declaration//GEN-END:variables
}
