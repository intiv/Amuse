%%

%class Amuse
%unicode
%int
%line
%column
%standalone

%{
  String functionType = "";
  boolean Ret = false;
  boolean recursive = false;
  String functionID = "";
%}

//Basic
digit = [0-9]
letra = [a-zA-Z]
espacio= "\t"|" "
endLine = \r|\n|\r\n
sentence = {letra}*{endLine}?
Expression = ({Asignacion} | {Increment} | {Decrement} | {Print} | {newVar}){endLine}

//Palabras reservadas
begin = "begin"
end = "end"
if = "if"
then = "then"
void = "void"
main = "Main"

//    Tipos
bool = "bool"
num = "num"
char = "char"
array = ({bool}|{num}|{char})"["{digit}*"]"{id}
type = {bool}|{num}|{char}|{array}

//Operators
opRel = "<" | ">" | ">=" | "<=" | "==" | "!="
operador = "+" | "-" | "*" | "/" | "^"
Asig = ":="
id = {letra}({letra}*{digit}*)*
A = {A} "+" {T}
A = {A} "-" {T}
A = {T}
T = {T} "*" {F}
T = {T} "/" {F}
T = {F}
F = "("{F}")"
Not = "!"
Or = "||"
And = "&&"
Condicion = ({Not}?{id}{opRel}{Not}?({id}|{digit}*)({Or}|{And})?)*


//Operations
Increment = {id}"++"
Decrement = {id}"--"
Print = "write("({id} | {sentence})")"
Asignacion = ({id}|{newVar}){Asig}(({id}|{digit}*|"'"{letra}"'"|"'"{digit}"'"){operador}?)*
newVar = ({bool}|{num}|{char})" "{id}

//If
endIf = "endif"
startIf = {if}"("{espacio}*{Condicion}{espacio}*")"{endLine}*{then}
else = "else"

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

//While
while = "while("{espacio}*{Condicion}{espacio}*")"({espacio}|{endLine})*{begin}{endLine}?

//Function
//Con retorno
function = {type}" "{id}"("({parameter}", "?)*")"{espacio}*"{"
return = "return "({id}|{digit}+|"true"|"false")
parameter = {type}" "{id}
functionCall = {id}"("({parameter}", "?)*")"

//Sin retorno
function = {void}" "{main}"("({parameter}", "?)*")"{espacio}*"{"
function = {void}" "{id}"("({parameter}", "?)*")"{espacio}*"{"
parameter = {type}" "{id}
functionCall = {void}"("({parameter}", "?)*")"

//Main


%state IF
%state FOR
%state SELECT
%state WHILE
%state FUNCTION
%state FUNCTIONV

%%

<YYINITIAL> {
  {Comment} {}
  {startIf} {System.out.println("if: "); yybegin(IF);}
  {for} {System.out.print("Ciclo for: \n"); yybegin(FOR);}
  {select} {System.out.print("Condicion select: \n"); yybegin(SELECT);}
  {while} {System.out.println("While: "); yybegin(WHILE);}
  {function}  { String tipoID = yytext().substring(0, yytext().indexOf("("));
                functionType = tipoID.substring(0, tipoID.indexOf(" "));
                functionID = tipoID.substring(tipoID.indexOf(" ")+1, tipoID.length());
                System.out.println("Tipo: "+functionType+", ID: "+functionID+".");
                if(functionType.equals("void")){
                  yybegin(FUNCTIONV);
                }else{
                  yybegin(FUNCTION);}}
  /* {endLine} {System.out.println();} */
}


<IF> {
  {endIf} {yybegin(YYINITIAL);}
  {else}  {System.out.println("encontro else");}
  {Print} {System.out.println("\t"+yytext().substring(yytext().indexOf("(")+1, yytext().indexOf(")")));}
  {Expression} {System.out.print("\tExpression en if: " + yytext());}
  {Comment} {}
  . {}
}

<FOR> {
  {forContinue} {System.out.print("For bien estructurado");}
  {Asignacion} {System.out.println("Asignacion -> "+yytext());}
  {newVar} {System.out.println("newVar -> "+yytext());}
  {endLine} {}
  {end} {yybegin(YYINITIAL);}
  . {}
}

<SELECT> {
  {selectContinue} {System.out.print("Select bien estructurado");}
  {optionContinue1} {System.out.print("Option1 bien estructurado");}
  {optionContinue} {System.out.print("Option bien estructurado");}
  {endLine} {}
  {end} {yybegin(YYINITIAL);}
  . {}
}

<WHILE> {
  {Expression}  {System.out.println("\tExpression en while: "+yytext());}
  {endLine} {}
  {end} {yybegin(YYINITIAL);}
  . {}
}

<FUNCTION> {
  {Expression}  {System.out.println("\tExpression en function: "+yytext());}
  {functionCall}  {
    if(yytext().substring(0, yytext().indexOf("(")).equals(functionID))
    { recursive = true;}else{recursive = false;}
    /* System.out.println("Llamado linea " + yyline+": "+yytext().substring(0, yytext().indexOf("("))+"."); */
  }
  {return}  {Ret = true;}
  "}" { System.out.println("Fin de funcion");
        if(Ret){
          System.out.println("La funcion retorna");
        }else{
          System.out.println("Error: La funcion no retorna");
        }
        if(recursive){
          System.out.println("La funcion "+functionID+" es recursiva");
        }else{
          System.out.println("La funcion "+functionID+" NO es recursiva");

        }
        Ret = false;
        recursive = false;
        yybegin(YYINITIAL);
      }
  {endLine} {}
  . {}
}

<FUNCTIONV> {
  {Expression}  {System.out.println("\tExpression en function: "+yytext());}
  {functionCall}  {
    if(yytext().substring(0, yytext().indexOf("(")).equals(functionID))
    { recursive = true;}else{recursive = false;}
  }
  {main} {System.out.println("Esta es la main function: ");}
  {return}  {Ret = true;}
  "}" { System.out.println("Fin de funcion");
        if(Ret){
          System.out.println("Error: La funcion retorna");
        }
        if(recursive){
          System.out.println("La funcion "+functionID+" es recursiva");
        }else{
          System.out.println("La funcion "+functionID+" NO es recursiva");
        }
        Ret = false;
        recursive = false;
        yybegin(YYINITIAL);
      }
  {endLine} {}
  . {}
}
/* <COMMENT> {
  {letra} {}
  {endLine} {yybegin(YYINITIAL);}
} */
