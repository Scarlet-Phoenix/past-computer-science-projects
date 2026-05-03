.global _start
_start:
.set noat # required to ignore compiler optimization
	movi r1, 0 # C = 0
	movi r11, 10  # n = 10
	movia r2, 0x1000 #assign memory location 1000 to fact
	movi r2, 1 # fact = 1
	movia r3, 0x2000 #assign memory location 2000 to sumoffact
	movi r3, 0 #assign 0 to sumoffact
	movi r11, 1 # n = 1
	movi r20, 10 #upper bound for the for loop = 10 
	loopouter: ble r20, r11, doneouter  # while n <= 10 
		movi r2, 1 # fact = 1 
		movi r1, 1 # c = 1 
		loopinner: ble r1, r11, doneinner
			mul r2, r2, r1
			br loopinner
		doneinner:
		add r3, r3, r2
		br loopouter
	doneouter:
	br done

done:
	br 0x18
		
			