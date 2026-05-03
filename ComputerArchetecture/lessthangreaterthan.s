.global _start
_start:

	movi r2, 1
	movi r3, 2
	bge r2, r3, else #if x > y BGE returns false if x < y 
	add r2, r2, r3
	br	done
else:
	sub r2, r2, r3
done:
	br 0x18 (0x18: done)