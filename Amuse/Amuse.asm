.text
.globl main
main:
	move $fp, $sp
	sub $sp, $sp, 12
_etiq0:
	li $v0, 5
	syscall
	sw $v0, -4($fp)
_etiq1:
	li $v0, 5
	syscall
	sw $v0, -8($fp)
_etiq2:
	li $v0, 5
	syscall
	sw $v0, -12($fp)
_etiq3:
	li $v0, 10
	syscall

_FUN_mayor:
	sw $fp, -4($sp)
	sw $ra, -8($sp)
	sw $s0, -12($sp)
	sw $s1, -16($sp)
	sw $s2, -20($sp)
	move $s0, $a0
	move $s1, $a1
	move $s2, $a2
	move $fp, $sp
	sub $sp, $sp, 0
_etiq4:
	bge $s0, $s1, _etiq6
_etiq5:
	b _etiq10
_etiq6:
	bge $s0, $s2, _etiq8
_etiq7:
	b _etiq10
_etiq8:
	move $v0, $s0
	b _FIN_FUN_mayor
_etiq9:
	b _etiq16
_etiq10:
	bge $s1, $s0, _etiq12
_etiq11:
	b _etiq16
_etiq12:
	bge $s1, $s2, _etiq14
_etiq13:
	b _etiq16
_etiq14:
	move $v0, $s1
	b _FIN_FUN_mayor
_etiq15:
	b _etiq16
_etiq16:
	move $v0, $s2
	b _FIN_FUN_mayor
_FIN_FUN_mayor:
	add $sp, $sp, 0
	lw $s0, 12($fp)
	lw $s1, 16($fp)
	lw $s2, 20($fp)
	lw $ra, -8($fp)
	lw $fp, -4($fp)
	jr $ra

.data
_bufferChars:	.space	1

