.global _start
_start:
	movi r2, 3  #constant for the BEQ operation
	movi r10, 1 # X <- any arbitrary value you want to have
	movi r11, 2 # Y 
	blt r10, r11, elseif #if x < y
	addi r11, r10, 1 # y = x + 1
	br done
elseif:
	beq r10, r2, else # if X == 3 (r11
	addi r11, r10, 2 # y = x + 2
	br done
else: 
	movi r11, 0 # y = 0
	br done
done: 
	br 0x18 # break 