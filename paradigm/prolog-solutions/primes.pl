init(MAX_N) :- \+ fill(2, MAX_N).

next_prime(I, NEXT) :- not sieve(I), NEXT is I, !.
next_prime(I, NEXT) :- J is I + 1, next_prime(J, NEXT).

fill(I, MAX_N) :-
        assert(primes(I)),
		S is I * I, S =< MAX_N, \+ set(S, I, MAX_N),
		J is I + 1, next_prime(J, K), K =< MAX_N, fill(K, MAX_N).

set(I, STEP, MAX_N) :-
		assert(sieve(I)), J is I + STEP, J =< MAX_N, set(J, STEP, MAX_N).

prime(X)     :- \+ sieve(X).
composite(X) :- sieve(X).

is_sorted(X, [X])     :- integer(X), !.
is_sorted(X, [X, Y])  :- integer(X), integer(Y), X =< Y, !.
is_sorted(H, [H | T]) :- is_sorted(R, T), H =< R.

prime_divisor(X, R, D) :- integer(X), primes(R), 0 is mod(X, R), D is div(X, R), !.

multiply([], 1)      :- !.
multiply([H | T], R) :- multiply(T, S), R is H * S.

prime_divisors(1, [])      :- !.
prime_divisors(X, [X])     :- prime(X), !.
prime_divisors(X, [H | T]) :- prime_divisor(X, H, D), prime_divisors(D, T), !.
prime_divisors(X, D)       :- multiply(D, X), is_sorted(R, D).

lcm_(X, [], R)                :- multiply(X, R), !.
lcm_([H1 | T1], [H2 | T2], R) :- H1 = H2, lcm_(T1, T2, R1), R is R1 * H1, !.
lcm_([H1 | T1], [H2 | T2], R) :- H1 < H2, lcm_(T1, [H2 | T2], R1), R is R1 * H1. !.
lcm_(X, Y, R)                 :- lcm_(Y, X, R).

lcm(A, B, R) :- prime_divisors(A, S), prime_divisors(B, T), lcm_(S, T, R), !.
