sys_exit:       equ             60
buf_size:       equ             8192

                section         .text
                global          _start

_start:
                xor             ebx, ebx
                sub             rsp, buf_size
                mov             rsi, rsp
                xor             ebp, ebp

read_again:
                xor             eax, eax
                xor             edi, edi
                mov             rdx, buf_size
                syscall

                test            rax, rax
                jz              quit
                js              read_error

                xor             ecx, ecx
                jmp             start_checking

skip_whitespace:
                inc             rcx
                xor             ebp, ebp
                
start_checking:
                cmp             rcx, rax
                je              read_again
                
.parse_next:
                cmp             byte [rsi + rcx], 8
                jle             .parse_word
                cmp             byte [rsi + rcx], 13
                jle             skip_whitespace
                cmp             byte [rsi + rcx], 32
                je              skip_whitespace

.parse_word:
                cmp             ebp, 0
                jne             .skip_word
                
                inc             rbx
                mov             ebp, 1
                
.skip_word:
                inc             rcx
                
                cmp             rcx, rax
                je              read_again
                
                jmp             .parse_next

quit:
                mov             rax, rbx
                call            print_int

                mov             rax, sys_exit
                xor             rdi, rdi
                syscall

print_int:
                mov             rsi, rsp
                mov             ebx, 10

                dec             rsi
                mov             byte [rsi], 0x0a

.next_char:
                xor             edx, edx
                div             rbx
                add             dl, '0'
                dec             rsi
                mov             [rsi], dl
                test            rax, rax
                jnz             .next_char

                mov             eax, 1
                mov             edi, 1
                mov             rdx, rsp
                sub             rdx, rsi
                syscall

                ret

read_error:
                mov             eax, 1
                mov             edi, 2
                mov             rsi, read_error_msg
                mov             rdx, read_error_len
                syscall

                mov             rax, sys_exit
                mov             edi, 1
                syscall

                section         .rodata

read_error_msg: db              "read failure", 0x0a
read_error_len: equ             $ - read_error_msg
