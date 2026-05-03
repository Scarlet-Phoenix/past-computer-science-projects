	.global _start
_start:
	#startup sequence
	movia sp, stack
	subi sp, sp, 12
	
	movia r26, a #global pointer
	
	#prologue
	
	movi r10, 5 #lower bound of array slice
	movi r16, 10 #upper bound of array slice
	
	stw r10, 4(sp) # store r10 before calling f
	
	mov r4, r10
	mov r5, r16 #save values to parameter

	call f 
	
	#epilogue
	
	ldw r10, 4(sp) 
	add r14, r10, r16
	
	mov r22, r2
	
	add r23, r22, r14
	
	#cleanup sequence
	addi sp, sp, 12
	
f:
	
	#startup sequence
	subi sp, sp, 20
	stw ra, 0(sp)
	
	stw r16, 4(sp) #save callee register to the stack
	mov r17, r4
	mov r18, r5
	# prologue
	
	mov r20, r26
	
	movi r10, 0
	movi r16, 1
	
	loop: bge r18, r17, else: 
		
		muli r19, r17, 4
		add r19, r19, r20
		ldw r21, a(r19) 
		
		add r10, r10, r21
		mul r16, r16, r21
		
		addi r17, r17, 1
		
		br loop
		
	else:
	#epilogue
	add r2, r10, r16
		
	#cleanup sequence
	ldw r16, 4(sp)
	ldw sp, 0(sp)
	
	addi sp, sp, 20
	
	ret	
		
	
	

	


.skip 500

stack:
a:
.end