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
    Character charac = null;
%}

digit = [0-9]
letra = [a-zA-Z]
espacio= "\t"|" "
endLine = \r|\n|\r\n
whitespace = {espacio} | {endLine}
number = {digit}+
//Palabras reservadas

if = "if"
else = "else"
elseif = "elseif"
endIf = "endif"
while = "while"
then = "then"
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
charval = "\'"{letra}?"\'"
//    Tipos
bool = "bool"
num = "num"
char = "char"

//Operators
parIzq = "("
parDer = ")"
not = "!"
or = "||"
and = "&&"
opRel = "<" | ">" | ">=" | "<=" | "==" | "!="
asig = ":="
coma = ","
cont = "++"|"--"

//Comments
commentLine = "##"[^\r\n]*{endLine}?
multiComment = "/#"[^]~"#/"
Comment = {commentLine} | {multiComment}


id = {letra}({letra}|{digit})*

%state CHARACTER 

%%

<YYINITIAL> {
  {Comment} {return new Symbol(Amuse.comment, yyline, yycolumn);}
  {whitespace}  {}
  {cont} {return new Symbol(Amuse.contador, yyline, yycolumn);}
  {write} {return new Symbol(Amuse.writestart, yyline, yycolumn);}
  {if}  {return new Symbol(Amuse.ifstart, yyline, yycolumn);}
  {then} {return new Symbol(Amuse.ifthen, yyline, yycolumn);}
  {else}  {return new Symbol(Amuse.elseclause, yyline, yycolumn);}
  {elseif}  {return new Symbol(Amuse.elseif, yyline, yycolumn);}
  {endIf} {return new Symbol(Amuse.endif, yyline, yycolumn);}
  {while} {return new Symbol(Amuse.whilestart, yyline, yycolumn);}
  {end} {return new Symbol(Amuse.end, yyline, yycolumn);}
  {for} {return new Symbol(Amuse.forstart, yyline, yycolumn);}
  {select}  {return new Symbol(Amuse.selectstart, yyline, yycolumn);}
  {option}  {return new Symbol(Amuse.options, yyline, yycolumn);}
  {break} {return new Symbol(Amuse.breaks, yyline, yycolumn);}
  {return}  {return new Symbol(Amuse.ret, yyline, yycolumn);}
  {opRel}  {return new Symbol(Amuse.opRel, yyline, yycolumn, yytext());}
  {not} {return new Symbol(Amuse.opNot, yyline, yycolumn);}
  {and} {return new Symbol(Amuse.opAnd, yyline, yycolumn);}
  {or}  {return new Symbol(Amuse.opOr, yyline, yycolumn);}
  {parIzq}  {return new Symbol(Amuse.parIzq, yyline, yycolumn);}
  {parDer}  {return new Symbol(Amuse.parDer, yyline, yycolumn);}
  {coma}  {return new Symbol(Amuse.coma, yyline, yycolumn);}
  ":" {return new Symbol(Amuse.colon, yyline, yycolumn);}
  {void}  {return new Symbol(Amuse.voidType, yyline, yycolumn);}
  {main}  {return new Symbol(Amuse.MainProgram, yyline, yycolumn);}

  //Operadores
  "+" {return new Symbol(Amuse.opSuma, yyline, yycolumn);}
  "-" {return new Symbol(Amuse.opResta, yyline, yycolumn);}
  "*" {return new Symbol(Amuse.opMult, yyline, yycolumn);}
  "/" {return new Symbol(Amuse.opDiv, yyline, yycolumn);}
  {asig}  {return new Symbol(Amuse.opAsig, yyline, yycolumn);}

  //tipos
  {bool}  {return new Symbol(Amuse.bool, yyline, yycolumn);}
  {num} {return new Symbol(Amuse.num, yyline, yycolumn);}
  {char}  {return new Symbol(Amuse.character, yyline, yycolumn);}
  {charval} {return new Symbol(Amuse.charval, yyline, yycolumn, yytext());}
  {boolean}   {return new Symbol(Amuse.booleano, yyline, yycolumn, yytext());}
  {number}  {return new Symbol(Amuse.number, yyline, yycolumn, yytext());}
  {id}  {return new Symbol(Amuse.id, yyline, yycolumn, yytext());}
  
  "[" {return new Symbol(Amuse.openBrk, yyline, yycolumn);} 
  "]" {return new Symbol(Amuse.closeBrk, yyline, yycolumn);}
  "}" {return new Symbol(Amuse.cbClose, yyline, yycolumn);}
  "{" {return new Symbol(Amuse.cbOpen, yyline, yycolumn);}
  ";" {return new Symbol(Amuse.pcoma, yyline, yycolumn);}
  // "\'"  {charac = null; yybegin(CHARACTER);}

  . {System.err.println("Error lexico: Caracter invalido ("+yytext()+") en linea "+(yyline+1)+", columna: "+(yycolumn+1));}
  /* \'  {string.setLength(0); yybegin(CHARACTER);} */
}

<CHARACTER> {
  "\'"  {yybegin(YYINITIAL); return new Symbol(Amuse.charval, yyline, yycolumn, charac.charValue()+""); }
  [^\n\r\"\\]  {
    if(charac != null){
        System.err.println("Error: Caracter solo puede tener length 1");
    }else{
      charac = new Character(yytext().charAt(0));
    }
  }
} 
