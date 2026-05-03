.equ	switches,0x00002010
.equ	leds, 0x00002000
.equ	hexa, 0x00002030
.equ	hexb, 0x00002040
.global _start
_start: #void method, takes no paramaters, returns 0. 

	#startup sequence: 
	
	subi sp, sp, 32 #assuming the stack pointer has already been initialized
	stw ra, 0(sp)
	stw r26, 4(sp) #the global pointer will be modified during this program to act as a base case.
	#to ensure that it can remain in use elsewhere, it will be restored later 
	
	#prologue 
	
	movi r26, 1 
	 
	loop: 
		call read
		ldw r2, 8(sp) 
		mov r8, r2 #move result of read into r8. 
	
		ldw r8, 12(sp) #store r8 in sp + 8
		mov r4, r8 #store r8 in paramater r4 
		call fib 
	
		mov r8, r2
		call printHex
		
		br loop
	
	
	#cleanup sequence
	ldw ra, 0(sp) 
	ldw r26, 4(sp) 
	addi sp, sp, 32
	




fib: 

# i have no fucking clue if this works, but it's my best recreation of whatever 
# hallucination chatgpt spat back out. 
# can't wake up wake me up inside can't wake up wake me up inside save me

	#takes a single paramater, r4. recursive function. it has 1 return value, r2.

	
	#startup sequence. 
	subi sp, sp, 20
	stw ra, 0(sp) 
	stw r4, 4(sp) 
	

	#prologue 
	
	bge r26, r4, base_case
		#recursive case
		
		subi r4, r4, 1 #fib(n-1) 
		call fib
		mov r5, r2 #moves base case into r5
		
		ldw r4, 4(sp) #restores r4
		subi r4, r4, 2
		call fib #fib(n-2)
		
		#epilogue 
		add r2, r2, r5 #add fib(n-1), fib(n-2)
		ldw r4, 4(sp) #restore original paramater
		
		#cleanup sequence
		ldw ra, 0(sp) #restore stack paramater
		addi sp, sp, 20
		ret 
		
	
	base_case: 
		#epilogue
	
		mov r2, r4 #return value stored in r4
	
		#cleanup sequence
		ldw ra, 0(sp) 
		ldw r4, 4(sp) 
		addi sp, sp, 20
		ret 
			
	
printHex: 
	
	subi sp, sp, 40
	stw ra, 0(sp) 
	
	
	
	



read: 
	#startup sequence 
	
	subi sp, sp, 4
	stw ra, 0(sp) 
	
	#prologue
	
	ldw r2, switches #load 8-bit value from switches into return register. 
	
	#epilogue
	
	#cleanup sequence
	
	ldw ra, 0(sp) 
	addi sp, sp, 4
	ret 


.data
a:
.word 0x40 # 0
.word 0x79 # 1
.word 0x24 # 2
.word 0x30 # 3
.word 0x19 # 4
.word 0x12 # 5
.word 0x02 # 6
.word 0x78 # 7
.word 0x00 # 8
.word 0x10 # 9
.word 0x08 # 10 A
.word 0x03 # 11 B
.word 0x46 # 12 C
.word 0x21 # 13 D
.word 0x06 # 14 E
.word 0x0e # 15 F
.end