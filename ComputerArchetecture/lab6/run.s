#
#  Runtime Routines for Altera NIOS
#    author: D. Brian Larkins
#    date:   FA2019
#
#.include "nios_macros.s"
.include "addr.s"
.global _start
.global _size
.global _strcmp
.global _printResult


# ledg = 0x00002090
# ledr = 0x00002000
# h0 = 0x00002010
# ...
# h7 = 0x00002080
# sdram = 0x08000000
# lcd = 0x000020b8

__default_stacksize=8192
__default_heapsize=8192

__long_delay=20000000
__short_delay=5000000
__shift_delay=7000000
#__long_delay=1
#__short_delay=1

__char_lo=0x20
__char_hi=0x8f


_start:
	# NIOS environment setup
	movia sp, STACK
	addi sp, sp, __default_stacksize
	movia r2, HEAP    # save heap ptr into memory
	movia r3, hptr
	stw   r2, 0(r3)   # hptr = &HEAP
	
	# initialize lcd
	movi  r2, 0x01
	movia r3, __lcd_ireg
	stbio r2, 0(r3)

  	# end of main loop
	mov r4, r0       # setup static link 
	call main   # go time.
	br _exit


#
# size(s) - returns length of string s
#
_size:
	ldw   r2,-4(r4)   # return length, stored in word preceeding pointer
	ret


#
# strcmp(s1, s2) - compare two strings, s1, s2
#
_strcmp:
  	# do {
  	#   c1 = (unsigned int) *s1++;
  	#   c2 = (unsigned int) *s2++;
  	# } while (c1 != '\0' && c1 == c2);
  	# return c1 - c2;

  __strcmp_loopbody:
	ldb   r2,0(r4)     # c1 = *s1
	ldb   r3,0(r5)     # c2 = *s2
	addi  r4,r4,1      # s1++
	addi  r5,r5,1      # s2++
	beq   r2,r0,__strcmp_loopdone
	
	beq   r2,r3,__strcmp_loopbody

  __strcmp_loopdone:
	sub   r2,r2,r3	
	ret

#
# not(i) - return 1 if i==0, 1 otherwise
#
_not:
	cmpeq r2,r4,r0
	ret

#
# printResult(a) - print factorial result on LED and LCD display
#
_printResult:
	subi sp, sp, 8
	stw   r16,  4(sp)   # callee save regs
	stw   ra,   0(sp)   # callee save regs

	mov   r16, r4       #  r17 = p = array ptr

	call _clearLine1
	call _clearLine0    # leaves cursor at 0,0

	movia r4, _amsg
	call _printString
	call _setHome_line1

	mov r4, r16

	call _printInt

	movia r4, __short_delay
	call _sleep

	ldw   r16, 4(sp)   # callee save regs
	ldw   ra,  0(sp)   # callee save regs
	addi   sp, sp, 8   # deallocate stack
	ret


#
# printArray(a) - print factorial result on LED and LCD display
#
_printArray:
	subi  sp, sp, 28    # allocate stack
	stw   r16, 24(sp)   # callee save regs
	stw   r17, 20(sp)   # callee save regs
	stw   r18, 16(sp)   # callee save regs
	stw   r19, 12(sp)   # callee save regs
	stw   r20,  8(sp)   # callee save regs
	stw   r21,  4(sp)   # callee save regs
	stw   ra,   0(sp)   # callee save regs


	ldw   r16, -4(r4)   #  r16 = array length
	mov   r17, r4       #  r17 = p = array ptr

	call _clearLine1
	call _clearLine0    # leaves cursor at 0,0

	movia r4, _amsg
	call _printString
	call _setHome_line1
	movi  r18, 8        #  e = 8
	movia r19, __display_hiaddr  # left-most LED address

	bge   r16, r18, __printArray_longArray

	# array is < 8 elements
	mov r18, r16         # e = array.length
	
	movi  r21, 0

  __printArray_longArray:
	slli r18, r18, 2     # e = min(array.length,8)*wordsize
	add  r18, r17, r18   # end = p + e
	
  __printArray_loopbody:
        ldw   r16, 0(r17)    # a = *p
	andi  r16, r16, 0xf  # mask off hi bits
	stbio r16, 0(r19)    # write to LED
	
	mov   r4,  r16
	call  _printInt
	add   r21, r21, r2
	movi  r4, 0x20
	call  _printChar
	addi  r21, r21, 1

	addi  r17, r17, 4    # p++
	subi  r19, r19, 0x10 # LED addr--
	movia r4, __short_delay
	call _sleep

	blt   r17, r18, __printArray_loopbody

	# loop complete
	movia r4, __long_delay
	call _sleep

	# rotate LCD display
	subi  r21, r21, 16
	mov   r20, r21

	bgt   r20, r0, __printArray_shiftloop
	
	movi  r21, 0
	br    __printArray_shiftjoin

  __printArray_shiftloop:
	call _LCD_shift
	movia r4, __shift_delay
	call _sleep
	subi r20,r20,1
	bgt  r20, r0, __printArray_shiftloop

  __printArray_shiftjoin:
	movia r4, __long_delay
	call _sleep
    
	movi r20, 40
	sub  r20, r20, r21
  __printArray_shiftloop2:
	call _LCD_shift
	subi r20,r20,1
	bgt  r20, r0, __printArray_shiftloop2

	ldw   r16, 24(sp)   # callee save regs
	ldw   r17, 20(sp)   # callee save regs
	ldw   r18, 16(sp)   # callee save regs
	ldw   r19, 12(sp)   # callee save regs
	ldw   r20,  8(sp)   # callee save regs
	ldw   r21,  4(sp)   # callee save regs
	ldw   ra,   0(sp)   # callee save regs
	addi   sp, sp, 28   # deallocate stack
	ret

#
# printInt(i) - print an integer value on the LCD
#
_printInt:
	# b = 10^9 (max x: 10^x < 2^32)
	# for i=0 to 9
	#   d = x/b
	#   if d > 0
	#      print d
	#      flag = 1
	#   else if flag
	#      print 0
	#   x = x - d*b
	#   b /= 10
	subi  sp, sp, 24    # allocate stack
	stw   r16, 20(sp)   # callee save regs
	stw   r17, 16(sp)   # callee save regs
	stw   r18, 12(sp)   # callee save regs
	stw   r19,  8(sp)   # callee save regs
	stw   r20,  4(sp)   # callee save regs
	stw   ra,   0(sp)   # callee save regs

	movi  r16, 10000
	muli  r16, r16, 1000
	muli  r16, r16, 100 # one BILLION dollars, muhahaha!
	mov   r18, r4       # copy of x
	movi  r19, 10       # i    = 10
	movi  r20, 0        # flag = 0
	movi  r2,  0        # number of written digits

  __printInt_loop:          # for i = 10 to 1
	div   r17, r18, r16 # d = x / base
	ble   r17, r0, __printInt_elseif

	mov   r4, r17       # printDigit(d)
	call  _printDigit
	movi  r20, 1
	addi  r2, r2, 1
	br    __printInt_join

  __printInt_elseif:
	beq   r20, r0, __printInt_join # if (flag)
	mov   r4, r0
	call  _printDigit
	addi  r2, r2, 1

  __printInt_join:
	mul   r17, r17, r16 # d*b
	sub   r18, r18, r17 # x = x - d*b
	movi  r17, 10 
	div  r16, r16, r17  # b = b/10
	
	subi  r19, r19, 1   # i = i-1
	bgt   r19,r0, __printInt_loop

	ldw   r16, 20(sp)   # callee save regs
	ldw   r17, 16(sp)   # callee save regs
	ldw   r18, 12(sp)   # callee save regs
	ldw   r19,  8(sp)   # callee save regs
	ldw   r20,  4(sp)   # callee save regs
	ldw   ra,   0(sp)   # callee save regs
	addi  sp, sp, 24    # deallocate stack
	ret

#
# printDigit(i) - print a single integer digit to the LCD
#
_printDigit:
	subi  sp, sp, 4   # allocate stack
	stw   ra, 0(sp)   # save return address
	addi  r4,r4,0x30
	call  _printChar
	ldw   ra, 0(sp)   # callee save regs
	addi  sp, sp, 4   # deallocate stack
	ret

#
# printString(s) - print a character string to the LCD
#
_printString:
	# character strings are stored little-endian:
	# "Hello World" == lleH roWo 00ld
	#  pos          == 3210 
	# 
	#  pos  = 0
	#  widx = 0
	#  w    = s[widx]
	#  do {
	#    switch(pos) {
	#    case 0:  ch =  w      & 0xff; pos++; break;
	#    case 1:  ch = (w>>8)  & 0xff; pos++; break;
	#    case 2:  ch = (w>>16) & 0xff; pos++; break;
	#    case 3:  ch = (w>>24) & 0xff; pos=0; widx++;
	#    }
	#    print ch
	#  } while ch != 0
	subi  sp, sp, 28   # allocate stack
	stw   r16,24(sp)   # callee save regs
	stw   r17,20(sp)   # callee save regs
	stw   r18,16(sp)   # callee save regs
	stw   r19,12(sp)   # callee save regs
	stw   r20, 8(sp)   # callee save regs
	stw   r21, 4(sp)   # callee save regs
	stw   ra,  0(sp)   # save return address

	mov   r16, r4      # base word addr
	movi  r18, 0       # little-endian strings...

  __printString_loop:
	ldw   r17, 0(r16)  # load 4 characters
	
  __printString_if0:
	movi  r19,0
	bne   r18, r19, __printString_if1
	andi  r17, r17, 0x000000ff
        br __printString_found

  __printString_if1:
	movi  r19,1
	bne   r18, r19, __printString_if2
	srli  r17, r17, 8
	andi  r17, r17, 0x000000ff
        br __printString_found

  __printString_if2:
	movi  r19,2
	bne   r18, r19, __printString_if3
	srli  r17, r17, 16 
	andi  r17, r17, 0x000000ff
        br __printString_found

  __printString_if3:
	srli  r17, r17, 24
	andi  r17, r17, 0x000000ff

  __printString_found:
	beq   r17, r0, __printString_done
	mov   r4, r17

	call  _printChar
	
	movi  r19, 3
	beq   r18, r19, __printString_nextword
	addi  r18, r18, 1
	br    __printString_loop

  __printString_nextword:
	movi r18, 0
	addi r16, r16, 4
	br    __printString_loop

  __printString_done:
	ldw   r16,24(sp)   # callee save regs
	ldw   r17,20(sp)   # callee save regs
	ldw   r18,16(sp)   # callee save regs
	ldw   r19,12(sp)   # callee save regs
	ldw   r20, 8(sp)   # callee save regs
	ldw   r21, 4(sp)   # callee save regs
	ldw   ra,  0(sp)   # callee save regs
	addi  sp, sp, 28   # deallocate stack
	ret


#
# printChar(c) - print a single character to the LCD
#
_printChar:
	subi  sp, sp, 12   # allocate stack
	stw   r16, 8(sp)   # callee save regs
	stw   r17, 4(sp)   # callee save regs
	stw   ra,  0(sp)   # save return address

	movia r17, __lcd_ireg         # lcd base addr
	movi  r16, __char_lo
	blt   r4, r16, __printChar_illegal_char 
	movi  r16, __char_hi
	bgt   r4, r16, __printChar_illegal_char

	stbio r4, 1(r17)
	br    __printChar_join

  __printChar_illegal_char:
	movi  r16, 0xff
	stbio r16, 1(r17)

  __printChar_join:
	ldw   r16, 8(sp)   # callee save regs
	ldw   r17, 4(sp)   # callee save regs
	ldw   ra,  0(sp)   # callee save regs
	addi  sp, sp, 12   # deallocate stack
	ret


#
# clearLine() - write 40 spaces to the cursor pos
#
_clearLine:
	subi  sp, sp, 12   # allocate stack
	stw   r16, 8(sp)   # callee save regs
	stw   r17, 4(sp)   # callee save regs
	stw   r18, 0(sp)   # callee save regs
	
	movi  r18, 40
	movia r17, __lcd_ireg
	movi  r16, __char_lo  # 0x20 == blank space

  __clearLine_loop:
	stbio r16, 1(r17)     # write space
	subi  r18, r18, 1
	bgt   r18, r0, __clearLine_loop

	ldw   r16, 8(sp)   # callee save regs
	ldw   r17, 4(sp)   # callee save regs
	ldw   r18, 0(sp)   # callee save regs
	addi  sp, sp, 12   # deallocate stack
	ret

#
# setHome_line0() - move the cursor to line0,0
#
_setHome_line0:
	subi   sp, sp, 8  # allocate stack
	stw   r16, 4(sp)  # callee save regs
	stw   r17, 0(sp)  # callee save regs

	movi  r16, 0x80
	movia r17, __lcd_ireg
	stbio r16, 0(r17)

	ldw   r16, 4(sp)   # callee save regs
	ldw   r17, 0(sp)   # callee save regs
	addi   sp, sp, 8   # deallocate stack
	ret

#
# setHome_line1() - move the cursor to line1,0
#
_setHome_line1:
	subi   sp, sp, 8  # allocate stack
	stw   r16, 4(sp)  # callee save regs
	stw   r17, 0(sp)  # callee save regs

	movi  r16, 0xc0
	movia r17, __lcd_ireg
	stbio r16, 0(r17)

	ldw   r16, 4(sp)    # callee save regs
	ldw   r17, 0(sp)    # callee save regs
	addi   sp, sp, 8    # deallocate stack
	ret

#
# clearLine0() - clear the first LCD line
#
_clearLine0:
	subi   sp, sp, 12  # allocate stack
	stw   r16, 8(sp)   # callee save regs
	stw   r17, 4(sp)   # callee save regs
	stw    ra, 0(sp)   # callee save regs

	call _setHome_line0
	call _clearLine
	call _setHome_line0

	ldw   r16, 8(sp)    # callee save regs
	ldw   r17, 4(sp)    # callee save regs
	ldw    ra, 0(sp)    # callee save regs
	addi   sp, sp, 12   # deallocate stack
	ret

#
# clearLine1() - clear the second LCD line
#
_clearLine1:
	subi   sp, sp, 12  # allocate stack
	stw   r16, 8(sp)   # callee save regs
	stw   r17, 4(sp)   # callee save regs
	stw    ra, 0(sp)   # callee save regs

	call _setHome_line1
	call _clearLine
	call _setHome_line1

	ldw   r16, 8(sp)    # callee save regs
	ldw   r17, 4(sp)    # callee save regs
	ldw    ra, 0(sp)    # callee save regs
	addi   sp, sp, 12   # deallocate stack
	ret

#
# LCD_shift() - shift the LCD display to the left one char
#
_LCD_shift:
	subi   sp, sp, 8  # allocate stack
	stw   r16, 4(sp)  # callee save regs
	stw   r17, 0(sp)  # callee save regs

	movi  r16, 0x1a
	movia r17, __lcd_ireg
	stbio r16, 0(r17)

	ldw   r16, 4(sp)   # callee save regs
	ldw   r17, 0(sp)   # callee save regs
	addi   sp, sp, 8   # deallocate stack
	ret


#
# sleep(count) - sleep for count clock cycles
#
_sleep:
  __sleepfor:
        subi r4,r4,1
        bgtu r4,r0, __sleepfor
	ret

_exit:
	br _exit

# static data
.data
empty:
	.word 0	
	.ascii "\000"
	.skip 3

hptr:
	.word 0

_amsg:
	.asciz "fib result:"
.data
.align 2
.skip __default_stacksize
STACK:

HEAP:
.skip __default_heapsize
