package amuse;
import java_cup.runtime.Symbol;
%%

%cupsym Amuse
%class scanner
%unicode
%line
%column
%char
%cup
%ignorecase
%public

//Basic

%{
    StringBuffer string = new StringBuffer();
%}

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
boolean = "false" | "true"
//    Tipos
bool = "bool"
num = "num"
char = "char"
array = ({bool}|{num}|{char})

//Operators
parIzq = "("
parDer = ")"
not = "!"
or = "||"
and = "&&"
opRel = "<" | ">" | ">=" | "<=" | "==" | "!="
asig = ":="
coma = ","

//Comments
commentLine = "##"( [^*] | \*+ [^/*] )*
multiComment = "/#"[^*]~"#/" | "/#" "#"+ "/"
Comment = {commentLine} | {multiComment}


id = {letra}({letra}|{digit})*

%state STRING
/* %state CHARACTER */

%%

<YYINITIAL> {
  {Comment} {return new Symbol(Amuse.comment, yychar, yyline);}
  {espacio} {}
  {endLine} {}
  {write} {System.out.println("<WRITE, "+yyline+">");}
  {if}  {return new Symbol(Amuse.ifstart, yychar, yyline);}
  {then} {return new Symbol(Amuse.ifthen, yychar, yyline);}
  {else}  {return new Symbol(Amuse.elseclause, yychar, yyline);}
  {elseif}  {return new Symbol(Amuse.elseif, yychar, yyline);}
  {endIf} {return new Symbol(Amuse.endif, yychar, yyline);}
  {while} {return new Symbol(Amuse.whilestart, yychar, yyline);}
  {begin} {return new Symbol(Amuse.begin, yychar, yyline);}
  {end} {return new Symbol(Amuse.end, yychar, yyline);}
  {for} {return new Symbol(Amuse.forstart, yychar, yyline);}
  {select}  {return new Symbol(Amuse.selectstart, yychar, yyline);}
  {option}  {return new Symbol(Amuse.options, yychar, yyline);}
  {break} {return new Symbol(Amuse.breaks, yychar, yyline);}
  {return}  {return new Symbol(Amuse.ret, yychar, yyline);}
  {opRel}  {return new Symbol(Amuse.opRel, yychar, yyline, yytext());}
  {not} {return new Symbol(Amuse.opNot, yychar, yyline);}
  {and} {return new Symbol(Amuse.opAnd, yychar, yyline);}
  {or}  {return new Symbol(Amuse.opOr, yychar, yyline);}
  {parIzq}  {return new Symbol(Amuse.parIzq, yychar, yyline);}
  {parDer}  {return new Symbol(Amuse.parDer, yychar, yyline);}
  {coma}  {return new Symbol(Amuse.coma, yychar, yyline);}
  ":" {return new Symbol(Amuse.colon, yychar, yyline);}
  {void}  {return new Symbol(Amuse.voidType, yychar, yyline);}
  {main}  {return new Symbol(Amuse.MainProgram, yychar, yyline);}

  //Operadores
  "+" {return new Symbol(Amuse.opSuma, yychar, yyline);}
  "-" {return new Symbol(Amuse.opResta, yychar, yyline);}
  "*" {return new Symbol(Amuse.opMult, yychar, yyline);}
  "/" {return new Symbol(Amuse.opDiv, yychar, yyline);}
  {asig}  {return new Symbol(Amuse.opAsig, yychar, yyline);}

  //tipos
  {bool}  {return new Symbol(Amuse.bool, yychar, yyline);}
  {num} {return new Symbol(Amuse.num, yychar, yyline);}
  {char}  {return new Symbol(Amuse.character, yychar, yyline);}
  {boolean}   {return new Symbol(Amuse.booleano, yychar, yyline, yytext());}
  {number}  {return new Symbol(Amuse.number, yychar, yyline, yytext());}
  {id}  {return new Symbol(Amuse.id, yychar, yyline, yytext());}
  
  "[" {return new Symbol(Amuse.openBrk, yychar, yyline);} 
  "]" {return new Symbol(Amuse.closeBrk, yychar, yyline);}
  "}" {return new Symbol(Amuse.cbClose, yychar, yyline);}
  "{" {return new Symbol(Amuse.cbOpen, yychar, yyline);}
  ";" {return new Symbol(Amuse.pcoma, yychar, yyline);}
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
