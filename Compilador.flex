%%

%class Lexer
%unicode
%line
%column
%standalone

%{
  StringBuffer string = new StringBuffer();
%}

letra = [a-zA-Z0-9]|" "
endLine = \r|\n|\r\n
/* startif = "if"
endif = "endif"
startCondition = "("
endCondition = ")"
opRel = "==" | ">" | "<" */
commentLine = "##"{sentence}
sentence = {letra}*{endLine}?
multiComment = "/#"{sentence}*"#/"{endLine}?
Comment = {commentLine} | {multiComment}

%state IF
%state IFCONDITION
%state COMMENT

%%

<YYINITIAL> {
  /* {startif} {string.setLength(0); yybegin(IF);} */

  {Comment} {}
  {letra} {System.out.print(yytext());}
  /* {endLine} {System.out.println();} */
}

/* <IF> {
  {startCondition}  {string.append(yytext()); yybegin(IFCONDITION);}
  {endif} {if(string.indexOf(")")!= -1){ System.out.println("Termino el if bien: "+string.toString()); yybegin(YYINITIAL); }else{ System.out.println("Error:" +string.toString()); yybegin(YYINITIAL); }}
  {endLine} {}
  . {}
} */

/* <IFCONDITION> {
  {letra} {string.append(yytext());}
  {opRel} {System.out.println("El operador es: "+yytext()); string.append(yytext());}
  {endCondition}  {string.append(yytext()); yybegin(IF);}
  {endLine} {if(string.indexOf(")")==-1){System.out.println("Condicion sin cerrar");} }
} */

/* <COMMENT> {
  {letra} {}
  {endLine} {yybegin(YYINITIAL);}
} */
