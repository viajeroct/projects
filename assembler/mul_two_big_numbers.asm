                section         .text
                
; size = const                
size_w:       	equ             129               

                global          _start
_start:
		; Let's allocate memory for two numbers and the answer.
		; total = 3 * 129
                sub             rsp, 3 * size_w * 8
                
                ; FIXED:
                mov             rcx, size_w
                
                ; r9 - pointer to the answer
                mov		r9, rsp
                
                ; Fill the answer memory with zeros so that there is no garbage.
                mov             rdi, rsp
                call		set_zero
                
                ; reading first number
                lea             rdi, [rsp + size_w * 8]
                call            read_long
                
                ; reading second number
                lea             rdi, [rsp + 2 * size_w * 8]
                call            read_long
                
                ; rsi - second number
                mov		rsi, rdi
                
                ; let's return the position of the second number back
                lea             rdi, [rsp + size_w * 8]
                
                ; calculate answer
                call            mul_long_long

                ; r9 - position of the answer
                ; but we need rdi for output
                mov 		rdi, rsp
                mov		rcx, 2 * size_w
                call            write_long

                mov             al, 0x0a
                call            write_char

                jmp             exit
                
; multiplies two long number
;    rdi -- address of number #1 (long number)
;    rsi -- address of number #2 (long number)
;    rcx -- length of long numbers in qwords
; result:
;    product is written to rdi              
mul_long_long:
		; save rcx
		; save address of rsi
		mov			r13, rcx
		mov			r12, rsi

; top level loop - main loop				
.main_loop:
		xor			edx, edx
		; restore r11=r9=answer's position
		mov			r11, r9
		; restore r13=rcx
		mov			r8, r13
		; restore rsi
		mov			rsi, r12
		; reset the transfer flag
		; FIXED:
		mov			rbx, [rdi]
		clc

; bottom level loop - inner loop
.loop:
		; recalculate new carry and move to r10
		adc 			rdx, 0
		mov 			r10, rdx

		; let's multiply some parts
		; rax = [rsi] * [rdi], rdx - overflow
		mov			rax, [rsi]
		mul			rbx

		; add carry from prev step
		add			rax, r10

		; recalculate new carry
		; move product to r11=some pos in answer
		adc			rdx,   0
		add			[r11], rax

		; move position of the second number
		; and position of the r11 - current
		; position for r9
		lea			r11, [r11 + 8]
		lea			rsi, [rsi + 8]

		; next step of the inner loop, r8~rcx
		dec             	r8
		jnz			.loop

		; It is necessary to zero an already unnecessary number (part of the first number from the rdi).
		mov 			rax,   0
		mov			[rdi], rax

		; move address of first number and answer
		lea			rdi, [rdi + 8]
		lea			r9,  [r9 + 8]

		; next step of the main_loop
		dec			rcx
		jnz			.main_loop

		ret                

; adds two long number
;    rdi -- address of summand #1 (long number)
;    rsi -- address of summand #2 (long number)
;    rcx -- length of long numbers in qwords
; result:
;    sum is written to rdi
add_long_long:
                push            rdi
                push            rsi
                push            rcx

                clc
.loop:
                mov             rax, [rsi]
                lea             rsi, [rsi + 8]
                adc             [rdi], rax
                lea             rdi, [rdi + 8]
                dec             rcx
                jnz             .loop

                pop             rcx
                pop             rsi
                pop             rdi
                ret

; adds 64-bit number to long number
;    rdi -- address of summand #1 (long number)
;    rax -- summand #2 (64-bit unsigned)
;    rcx -- length of long number in qwords
; result:
;    sum is written to rdi
add_long_short:
                push            rdi
                push            rcx
                push            rdx

                xor             rdx,rdx
.loop:
                add             [rdi], rax
                adc             rdx, 0
                mov             rax, rdx
                xor             rdx, rdx
                add             rdi, 8
                dec             rcx
                jnz             .loop

                pop             rdx
                pop             rcx
                pop             rdi
                ret

; multiplies long number by a short
;    rdi -- address of multiplier #1 (long number)
;    rbx -- multiplier #2 (64-bit unsigned)
;    rcx -- length of long number in qwords
; result:
;    product is written to rdi
mul_long_short:
                push            rax
                push            rdi
                push            rcx

                xor             rsi, rsi
.loop:
                mov             rax, [rdi]
                mul             rbx
                add             rax, rsi
                adc             rdx, 0
                mov             [rdi], rax
                add             rdi, 8
                mov             rsi, rdx
                dec             rcx
                jnz             .loop

                pop             rcx
                pop             rdi
                pop             rax
                ret

; divides long number by a short
;    rdi -- address of dividend (long number)
;    rbx -- divisor (64-bit unsigned)
;    rcx -- length of long number in qwords
; result:
;    quotient is written to rdi
;    rdx -- remainder
div_long_short:
                push            rdi
                push            rax
                push            rcx

                lea             rdi, [rdi + 8 * rcx - 8]
                xor             rdx, rdx

.loop:
                mov             rax, [rdi]
                div             rbx
                mov             [rdi], rax
                sub             rdi, 8
                dec             rcx
                jnz             .loop

                pop             rcx
                pop             rax
                pop             rdi
                ret

; assigns a zero to long number
;    rdi -- argument (long number)
;    rcx -- length of long number in qwords
set_zero:
                push            rax
                push            rdi
                push            rcx

                xor             rax, rax
                rep stosq

                pop             rcx
                pop             rdi
                pop             rax
                ret

; checks if a long number is a zero
;    rdi -- argument (long number)
;    rcx -- length of long number in qwords
; result:
;    ZF=1 if zero
is_zero:
                push            rax
                push            rdi
                push            rcx

                xor             rax, rax
                rep scasq

                pop             rcx
                pop             rdi
                pop             rax
                ret

; read long number from stdin
;    rdi -- location for output (long number)
;    rcx -- length of long number in qwords
read_long:
                push            rcx
                push            rdi

                call            set_zero
.loop:
                call            read_char
                or              rax, rax
                js              exit
                cmp             rax, 0x0a
                je              .done
                cmp             rax, '0'
                jb              .invalid_char
                cmp             rax, '9'
                ja              .invalid_char

                sub             rax, '0'
                mov             rbx, 10
                call            mul_long_short
                call            add_long_short
                jmp             .loop

.done:
                pop             rdi
                pop             rcx
                ret

.invalid_char:
                mov             rsi, invalid_char_msg
                mov             rdx, invalid_char_msg_size
                call            print_string
                call            write_char
                mov             al, 0x0a
                call            write_char

.skip_loop:
                call            read_char
                or              rax, rax
                js              exit
                cmp             rax, 0x0a
                je              exit
                jmp             .skip_loop

; write long number to stdout
;    rdi -- argument (long number)
;    rcx -- length of long number in qwords
write_long:
                push            rax
                push            rcx

                mov             rax, 20
                mul             rcx
                mov             rbp, rsp
                sub             rsp, rax

                mov             rsi, rbp

.loop:
                mov             rbx, 10
                call            div_long_short
                add             rdx, '0'
                dec             rsi
                mov             [rsi], dl
                call            is_zero
                jnz             .loop

                mov             rdx, rbp
                sub             rdx, rsi
                call            print_string

                mov             rsp, rbp
                pop             rcx
                pop             rax
                ret

; read one char from stdin
; result:
;    rax == -1 if error occurs
;    rax \in [0; 255] if OK
read_char:
                push            rcx
                push            rdi

                sub             rsp, 1
                xor             rax, rax
                xor             rdi, rdi
                mov             rsi, rsp
                mov             rdx, 1
                syscall

                cmp             rax, 1
                jne             .error
                xor             rax, rax
                mov             al, [rsp]
                add             rsp, 1

                pop             rdi
                pop             rcx
                ret
.error:
                mov             rax, -1
                add             rsp, 1
                pop             rdi
                pop             rcx
                ret

; write one char to stdout, errors are ignored
;    al -- char
write_char:
                sub             rsp, 1
                mov             [rsp], al

                mov             rax, 1
                mov             rdi, 1
                mov             rsi, rsp
                mov             rdx, 1
                syscall
                add             rsp, 1
                ret

exit:
                mov             rax, 60
                xor             rdi, rdi
                syscall

; print string to stdout
;    rsi -- string
;    rdx -- size
print_string:
                push            rax

                mov             rax, 1
                mov             rdi, 1
                syscall

                pop             rax
                ret


                section         .rodata
invalid_char_msg:
                db              "Invalid character: "
invalid_char_msg_size: equ             $ - invalid_char_msg
