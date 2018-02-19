%%

%class Amuse
%unicode
%int
%line
%column
%standalone

//Basic
digit = [0-9]
letra = [a-zA-Z]|" "
endLine = \r|\n|\r\n
sentence = {letra}*{endLine}?
Expression = {Asignacion} | {Increment} | {Decrement} | {Print}

//Operators
opRel = "<" | ">" | ">=" | "<=" | "=" | "!="
Asig = ":="
Condicion = {id}{opRel}({id}|[0-9]*)
id = [a-zA-Z]{letra}*

//Operations
Asignacion = {id}" "*{Asig}" "*{id}
Increment = {id}"++"
Decrement = {id}"--"
Print = "write("({id} | {sentence})")"

//Control blocks
endIf = "endif"
startIf = "if ("{Condicion}") then"

//Comments
commentLine = "##"{sentence}
multiComment = "/#"{sentence}*"#/"{endLine}?
Comment = {commentLine} | {multiComment}

%state IF

%%

<YYINITIAL> {
  {Comment} {}
  {startIf} {System.out.println("Condicion if: "+yytext().substring(yytext().indexOf("(")+1, yytext().indexOf(")"))); yybegin(IF);}
  /* {endLine} {System.out.println();} */
}


<IF> {
  {endIf} {yybegin(YYINITIAL);}
  {Print} {System.out.println(yytext().substring(yytext().indexOf("(")+1, yytext().indexOf(")")));}
  {Expression} {System.out.print("Expression en if: " + yytext());}
  {Comment} {}
  . {}
}

/* <COMMENT> {
  {letra} {}
  {endLine} {yybegin(YYINITIAL);}
} */
