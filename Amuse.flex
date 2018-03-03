%%

%class Amuse
%unicode
%int
%line
%column
%standalone

%{
  StringBuffer string = new StringBuffer();
  StringBuffer character = new StringBuffer();
%}

//Basic
digit = [0-9]
letra = [a-zA-Z]
espacio= "\t"|" "
endLine = \r|\n|\r\n
number = {digit}+
//Palabras reservadas

if = "if"
else = "else"
elseif = "elseif"
endIf = "endif"
while = "while"
then = "then"
begin = "begin"
end = "end"
void = "void"
main = "Main"
write = "write"
for = "for"
select = "select"
option = "option"
break = "break"
return = "return"

//    Tipos
bool = "bool"
num = "num"
char = "char"
array = ({bool}|{num}|{char})"[]"
tipo = {bool}|{num}|{char}|{array}

//Operators
parIzq = "("
parDer = ")"
not = "!"
or = "||"
and = "&&"
opRel = "<" | ">" | ">=" | "<=" | "==" | "!="
operador = "+" | "-" | "*" | "/" | "^" | {parIzq} | {parDer}
opComp = {not}|{or}|{and}
asig = ":="

//Comments
commentLine = "##"({letra}|{digit}|{espacio})*
multiComment = "/#"({letra}|{digit}|{espacio}|{endLine})*"#/"
Comment = {commentLine} | {multiComment}


id = {letra}({letra}|{digit})*

%state STRING
/* %state CHARACTER */

%%

<YYINITIAL> {
  {Comment} {}
  {espacio} {}
  {endLine} {}
  {write} {System.out.println("<WRITE, "+yyline+">");}
  {tipo}  {System.out.println("<TIPO, "+yytext()+", "+yyline+">");}
  {if}  {System.out.println("<IF, "+yyline+">");}
  {else}  {System.out.println("<ELSE, "+yyline+">");}
  {elseif}  {System.out.println("<ELSEIF, "+yyline+">");}
  {endIf} {System.out.println("<ENDIF, "+yyline+">");}
  {while} {System.out.println("<WHILE, "+yyline+">");}
  {then}  {System.out.println("<THEN, "+yyline+">");}
  {begin} {System.out.println("<BEGIN, "+yyline+">");}
  {end} {System.out.println("<END, "+yyline+">");}
  {for} {System.out.println("<FOR, "+yyline+">");}
  {select}  {System.out.println("<SELECT, "+yyline+">");}
  {option}  {System.out.println("<OPTION, "+yyline+">");}
  {break} {System.out.println("<BREAK, "+yyline+">");}
  {return}  {System.out.println("<RETURN, "+yyline+">");}
  {opRel}  {System.out.println("<OPREL, "+yytext()+", "+yyline+">");}
  {opComp}  {System.out.println("<OPCOMP, "+yytext()+", "+yyline+">");}
  {operador}  {System.out.println("<OPERADOR, "+yytext()+", "+yyline+">");}
  {asig}  {System.out.println("<ASIG, "+yyline+">");}
  ":" {System.out.println("<COLUMN, "+yyline+">");}
  {void}  {System.out.println("<VOID, "+yyline+">");}
  {main}  {System.out.println("<MAIN, "+yyline+">");}
  {id}  {System.out.println("<ID, "+yytext()+", "+yyline+">");}
  {number}  {System.out.println("<NUMBER, "+yytext()+", "+yyline+">");}
  "}" {System.out.println("<CLOSEBRACES, "+yyline+">");}
  "{" {System.out.println("<OPENBRACES, "+yyline+">");}
  ";" {System.out.println("<PCOMA, "+yyline+">");}
  \"  {string.setLength(0); yybegin(STRING);}
  /* \'  {string.setLength(0); yybegin(CHARACTER);} */
}

<STRING> {
  \"  {System.out.println("<STRING, "+string+", "+yyline+">"); yybegin(YYINITIAL);}
  [^\n\r\"\\]+ {string.append(yytext());}
}
/*
<CHARACTER> {
  \'  {System.out.println("<CHARACTER, "+string+", "+yyline+">"); yybegin(YYINITIAL);}
  [^\n\r\"\\]+  {string.append(yytext());}
} */
