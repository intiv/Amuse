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
array = ({bool}|{num}|{char})"[]"

//Operators
parIzq = "("
parDer = ")"
not = "!"
or = "||"
and = "&&"
opRel = "<" | ">" | ">=" | "<=" | "==" | "!="
opArit = "+" | "-" | "*" | "/" | "^" 
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
  {for} {System.out.println("<FOR, "+yyline+">");}
  {select}  {System.out.println("<SELECT, "+yyline+">");}
  {option}  {System.out.println("<OPTION, "+yyline+">");}
  {break} {System.out.println("<BREAK, "+yyline+">");}
  {return}  {return new Symbol(Amuse.ret, yychar, yyline);}
  {opRel}  {return new Symbol(Amuse.opRel, yychar, yyline, yytext());}
  {not} {return new Symbol(Amuse.opNot, yychar, yyline);}
  {and} {return new Symbol(Amuse.opAnd, yychar, yyline);}
  {or}  {return new Symbol(Amuse.opOr, yychar, yyline);}
  {opArit}  {return new Symbol(Amuse.opArit, yychar, yyline, yytext());}
  {parIzq}  {return new Symbol(Amuse.parIzq, yychar, yyline);}
  {parDer}  {return new Symbol(Amuse.parDer, yychar, yyline);}
  {asig}  {return new Symbol(Amuse.opAsig, yychar, yyline);}
  {coma}  {return new Symbol(Amuse.coma, yychar, yyline);}
  ":" {System.out.println("<COLUMN, "+yyline+">");}
  {void}  {return new Symbol(Amuse.voidType, yychar, yyline);}
  {main}  {return new Symbol(Amuse.MainProgram, yychar, yyline);}
  //tipos
  {bool}  {return new Symbol(Amuse.bool, yychar, yyline);}
  {num} {return new Symbol(Amuse.num, yychar, yyline);}
  {char}  {return new Symbol(Amuse.character, yychar, yyline);}
  {boolean}   {return new Symbol(Amuse.booleano, yychar, yyline, yytext());}
  {number}  {return new Symbol(Amuse.number, yychar, yyline, yytext());}
  {id}  {return new Symbol(Amuse.id, yychar, yyline, yytext());}
  
  
  "}" {return new Symbol(Amuse.cbClose, yychar, yyline);}
  "{" {return new Symbol(Amuse.cbOpen, yychar, yyline);}
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
