%%

%class Amuse
%unicode
%int
%line
%column
%standalone

//Basic
digit = [0-9]
letra = [a-zA-Z]
espacio= "\t"|" "*
endLine = \r|\n|\r\n
sentence = {letra}*{endLine}?
Expression = ({Asignacion} | {Increment} | {Decrement} | {Print} | newVar){endLine}

//Operators
opRel = "<" | ">" | ">=" | "<=" | "==" | "!="
Asig = ":="
id = {letra}({letra}*{digit}*)*
Condicion = {id}{opRel}{id}

//Palabras reservadas
begin = "begin"
end = "end"
if = "if"
then = "then"

//    Tipos
bool = "bool"
num = "num"
char = "char"
array = ({bool}|{num}|{char})"["{digit}*"]"{id}

//Operations
Increment = {id}"++"
Decrement = {id}"--"
Print = "write("({id} | {sentence})")"
Asignacion = ({id}|{newVar}){Asig}({id}|{digit}*|"'"{letra}"'"|"'"{digit}"'")
newVar = ({bool}|{num}|{char})" "{id}

//If
endIf = "endif"
startIf = {if}"("{Condicion}")"{endLine}?{then}

//Comments
commentLine = "##"{sentence}
multiComment = "/#"{sentence}*"#/"{endLine}?
Comment = {commentLine} | {multiComment}

//For
for = "for"
forContinue = "("{id}|{Asignacion}";"{Condicion}";"{Increment}|{Decrement}")"{endLine}?{begin}

//Switch
select = "select"
option = "option"
break = "break"
selectContinue = "("{id}")"{endLine}?{begin}{endLine}
optionContinue1 = {option}" "({digit}*|"'"{letra}"'"|"'"{digit}"'")":"{endLine}?{Expression}*{endLine}{break}
optionContinue = {endLine}?{Expression}*{endLine}{break}

%state IF
%state FOR
%state SELECT

%%

<YYINITIAL> {
  {Comment} {}
  {startIf} {System.out.println("Condicion if: "+yytext().substring(yytext().indexOf("(")+1, yytext().indexOf(")"))); yybegin(IF);}
  {for} {System.out.print("Ciclo for: \n");yybegin(FOR);}
  {select} {System.out.print("Condicion select: \n");yybegin(SELECT);}
  /* {endLine} {System.out.println();} */
}


<IF> {
  {endIf} {yybegin(YYINITIAL);}
  {Print} {System.out.println(yytext().substring(yytext().indexOf("(")+1, yytext().indexOf(")")));}
  {Expression} {System.out.print("Expression en if: " + yytext());}
  {Comment} {}
  . {}
}

<FOR> {
  {forContinue} {System.out.print("For bien estructurado");}
  {Asignacion} {System.out.println("Asignacion -> "+yytext());}
  {newVar} {System.out.println("newVar -> "+yytext());}
  {end} {yybegin(YYINITIAL);}
  . {}
}

<SELECT> {
  {selectContinue} {System.out.print("Select bien estructurado");}
  {optionContinue1} {System.out.print("Option1 bien estructurado");}
  {optionContinue} {System.out.print("Option bien estructurado");}
  {end} {yybegin(YYINITIAL);}
  . {}
}
/* <COMMENT> {
  {letra} {}
  {endLine} {yybegin(YYINITIAL);}
} */
