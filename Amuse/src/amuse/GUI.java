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

    public String CodigoFinal(){
        int cont = 0;
        int operation = 10;
        int maxoffset = amuse.tabla.getMaxOffset("Main");
        String code = ".text\n.globl main\nmain:\n\tmove $fp, $sp\n\tsub $sp, $sp, "+maxoffset+"\n";
        boolean cambioAmbito = false;
        String ambito = "Main";
        for(Cuadruplo cuad : amuse.cuadruplos){
            
            code+= "_etiq"+cont+":\n";
            if(cuad.operator.equals("READ")){
                code += genREAD(cuad);
            }else if(cuad.operator.equals("WRITE")){
                code += genWRITE(cuad);
            }else if(cuad.operator.equals("GOTO")){
                code += line("b _etiq"+cuad.result);
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
            }
            cont++;
        }
        code += ".data\n_bufferChars:\t.space\t1\n";
        return code;

    }

    public String line(String code){
        return "\t"+code+"\n";
    }
    
    public String genREAD(Cuadruplo cuad){
        Simbolo sym = amuse.tabla.getSymbol(cuad.result, "Main");
        String retVal = "";
        if(sym.tipo.equals("num") || sym.tipo.equals("bool")){
            retVal = line("li $v0, 5") +
                    line("syscall") +
                    line("sw $v0, -"+sym.offset+"($fp)");
        }else if(sym.tipo.equals("char")){
            // 
        }
        return retVal;
    }

    public String genWRITE(Cuadruplo cuad){
        Simbolo sym = amuse.tabla.getSymbol(cuad.result, "Main");
        String retVal = "";
        if(sym.tipo.equals("num") || sym.tipo.equals("bool")){
            retVal = line("lw $a0, -"+sym.offset+"($fp)") +
                    line("li $v0, 1") +
                    line("syscall");
        }
        return retVal;
    }

    public String genIF(Cuadruplo cuad){
        String retVal = "";
        boolean isid1 = amuse.isID(cuad.arg1);
        boolean isid2 = amuse.isID(cuad.arg2);
        int temp1 = desc.getFreeT();
        if(temp1 > -1){
            desc.ts[temp1] = " ";
        }else{
            temp1 = 0;
            desc.ts[0] = " ";
        }
        if(isid1){
            Simbolo id1 = amuse.tabla.getSymbol(cuad.arg1, "Main");
            if(id1.tipo.equals("num") || id1.tipo.equals("bool")){
                retVal += line("lw $t"+temp1+", -"+id1.offset+"($fp)");
            }
            //else if tipo.equals("char") cargar asciiz
        }else{
            retVal += line("li $t"+temp1+", "+cuad.arg1); //Se asume que es numero/bool, falta char
        }
        int temp2 = desc.getFreeT();
        if(temp2 > -1){
            desc.ts[temp2] = " ";
        }else{
            temp2 = 1;
            desc.ts[1] = " ";
        }
        if(isid2){
            Simbolo id2 = amuse.tabla.getSymbol(cuad.arg2, "Main");
            if(id2.tipo.equals("num") || id2.tipo.equals("bool")){
                retVal += line("lw $t"+temp2+", -"+id2.offset+"($fp)");
            }
            //else if tipo.equals("char") cargar asciiz
        }else{
            retVal += line("li $t"+temp2+", "+cuad.arg2);
        } 
        String tipoIf = cuad.operator.substring(2, cuad.operator.length());
        if(tipoIf.equals("<")){
            retVal += line("blt $t"+temp1+", $t"+temp2+", _etiq"+cuad.result);
        }else if(tipoIf.equals("<=")){
            retVal += line("ble $t"+temp1+", $t"+temp2+", _etiq"+cuad.result);
        }else if(tipoIf.equals(">")){
            retVal += line("bgt $t"+temp1+", $t"+temp2+", _etiq"+cuad.result);
        }else if(tipoIf.equals(">=")){
            retVal += line("bge $t"+temp1+", $t"+temp2+", _etiq"+cuad.result);
        }else if(tipoIf.equals(":=")){
            retVal += line("beq $t"+temp1+", $t"+temp2+", _etiq"+cuad.result);
        }else if(tipoIf.equals("!=")){
            retVal += line("bne $t"+temp1+", $t"+temp2+", _etiq"+cuad.result);
        }
        desc.freeTS();
        return retVal;
    }

    public String genASIG(Cuadruplo cuad){
        String retVal = "";
        Simbolo sym = amuse.isID(cuad.result) ? amuse.tabla.getSymbol(cuad.result, "Main") : null;
        if(cuad.arg1.contains("$t") && sym != null){
            retVal = line("sw "+cuad.arg1+", -"+sym.offset+"($fp)");
        }else if(sym != null){
            if(amuse.isID(cuad.arg1)){
                Simbolo sym2 = amuse.tabla.getSymbol(cuad.arg1, "Main");
                retVal = line("lw $t0, -"+sym2.offset+"($fp)") +
                        line("sw $t0, -"+sym.offset+"($fp)");
            }else{
                retVal = line("li $t0, "+cuad.arg1) +
                        line("sw $t0, -"+sym.offset+"($fp)");
            }
            //se asume es numero, falta char
        }
        return retVal;
    }   

    public String genSUMA(Cuadruplo cuad){
        String retVal = "";
        /*
        
        
        boolean isid1 = amuse.isID(cuad.arg1);
        boolean isid2 = amuse.isID(cuad.arg2);
        int temp1 = desc.getFreeT();
        if(temp1 > -1){
            desc.ts[temp1] = " ";
        }else{
            temp1 = 0;
            desc.ts[0] = " ";
        }
        if(isid1){
            Simbolo id1 = amuse.tabla.getSymbol(cuad.arg1, "Main");
            if(id1.tipo.equals("num") || id1.tipo.equals("bool")){
                retVal += line("lw $t"+temp1+", -"+id1.offset+"($fp)");
            }
            //else if tipo.equals("char") cargar asciiz
        }else{
            retVal += line("li $t"+temp1+", "+cuad.arg1); //Se asume que es numero/bool, falta char
        }
        int temp2 = desc.getFreeT();
        if(temp2 > -1){
            desc.ts[temp2] = " ";
        }else{
            temp2 = 1;
            desc.ts[1] = " ";
        }
        if(isid2){
            Simbolo id2 = amuse.tabla.getSymbol(cuad.arg2, "Main");
            if(id2.tipo.equals("num") || id2.tipo.equals("bool")){
                retVal += line("lw $t"+temp2+", -"+id2.offset+"($fp)");
            }
            //else if tipo.equals("char") cargar asciiz
        }else{
            retVal += line("li $t"+temp2+", "+cuad.arg2);
        } 
        String tipoIf = cuad.operator.substring(2, cuad.operator.length());
        if(tipoIf.equals("<")){
            retVal += line("blt $t"+temp1+", $t"+temp2+", _etiq"+cuad.result);
        }else if(tipoIf.equals("<=")){
            retVal += line("ble $t"+temp1+", $t"+temp2+", _etiq"+cuad.result);
        }else if(tipoIf.equals(">")){
            retVal += line("bgt $t"+temp1+", $t"+temp2+", _etiq"+cuad.result);
        }else if(tipoIf.equals(">=")){
            retVal += line("bge $t"+temp1+", $t"+temp2+", _etiq"+cuad.result);
        }else if(tipoIf.equals(":=")){
            retVal += line("beq $t"+temp1+", $t"+temp2+", _etiq"+cuad.result);
        }else if(tipoIf.equals("!=")){
            retVal += line("bne $t"+temp1+", $t"+temp2+", _etiq"+cuad.result);
        }
        desc.freeTS();
        //add i, i, i
        //add i, $t2, 1
        //add $t2, i, 1
        //add $t2, $t2, 1
        // si esto es un temporal?
        // si si entonces agregar sino reviso si es un id del main porque solo eso manejamos
        //null si no es ninguno de los dos
        Simbolo var = amuse.isID(cuad.arg1) ? amuse.tabla.getSymbol(cuad.arg1, "Main") : null;
        Simbolo sym1 = amuse.isID(cuad.arg2) ? amuse.tabla.getSymbol(cuad.arg2, "Main") : null;
        Simbolo sym2 = amuse.isID(cuad.result) ? amuse.tabla.getSymbol(cuad.result, "Main") : null;
        String arg1 ="",arg2="",arg3 ="";
        if(cuad.arg1.contains("$t") && var != null){
            retVal = line("add "+cuad.arg1+", -"+var.offset+"($fp)");
        }else if(var != null){
            if(amuse.isID(cuad.arg1)){
                Simbolo sym = amuse.tabla.getSymbol(cuad.arg1, "Main");
                retVal = line("lw $t0, -"+sym.offset+"($fp)") +
                        line("sw $t0, -"+sym1.offset+"($fp)");
            }else{
                retVal = line("li $t0, "+cuad.arg1) +
                        line("sw $t0, -"+sym1.offset+"($fp)");
            }
            //se asume es numero, falta char
        }*/
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
        btn_compilar = new javax.swing.JButton();
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
        btn_fileChooser.setFont(new java.awt.Font("Rockwell Condensed", 0, 24)); // NOI18N
        btn_fileChooser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/saa.png"))); // NOI18N
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
        btn_codigoInt.setFont(new java.awt.Font("Rockwell Condensed", 0, 24)); // NOI18N
        btn_codigoInt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/dss.png"))); // NOI18N
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

        btn_compilar.setBackground(new java.awt.Color(153, 153, 153));
        btn_compilar.setFont(new java.awt.Font("Rockwell Condensed", 0, 24)); // NOI18N
        btn_compilar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/556.png"))); // NOI18N
        btn_compilar.setText("Build");
        btn_compilar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btn_compilar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_compilarMouseClicked(evt);
            }
        });
        getContentPane().add(btn_compilar, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 300, 80));

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
        btn_compilar1.setFont(new java.awt.Font("Rockwell Condensed", 0, 24)); // NOI18N
        btn_compilar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/556.png"))); // NOI18N
        btn_compilar1.setText("Compilar");
        btn_compilar1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btn_compilar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_compilar1MouseClicked(evt);
            }
        });
        getContentPane().add(btn_compilar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, 300, 80));

        lb_background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/Cadet-Grey-Solid-Color-Background-Wallpaper-5120x2880.png"))); // NOI18N
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

    private void btn_compilarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_compilarMouseClicked
       String params[] = new String[7];

       params[0] = "-destdir";
       params[1] = "src/amuse/";
       params[2] = "-parser";
       params[3] = "Sintactico";
       params[4] = "-symbols";
       params[5] = "Amuse";
       
       params[6] = "src/amuse/AmuseSyntactic.cup";
       try {
           java_cup.Main.main(params);
       } catch (Exception ex) {
           Logger.getLogger(Amuse.class.getName()).log(Level.SEVERE, null, ex);
       }
      
    }//GEN-LAST:event_btn_compilarMouseClicked

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
            Logger.getLogger(AmuseMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_compilar1MouseClicked

    private void btn_codigoIntMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_codigoIntMouseClicked
        jf_codigoIntermedio.pack();
        jf_codigoIntermedio.show(true);
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
    private javax.swing.JButton btn_compilar;
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
