# TLA-Projet
projet de Théorie des langages et des Automates
Logo interpreter

Informations :

Grammaire du langage

B -> S
S -> repeat int S’S | procedure ident S’S | AS | call ident S |  Ɛ 
S’ -> [B]
A -> left int | right int | forward int | color int

First  

First(B) = { repeat, procedure, call left, right, forward, color, Ɛ}
First(S) = { repeat, procedure, call left, right, forward, color, Ɛ}
First(S’) = { [ }
First(A) = { left, right, forward, color } 

Follow 

Follow(S) = { ] , Ɛ}
Follow(B) = { ] , Ɛ}
Follow(S’) = { ] , Ɛ}
Follow(A) = { Ɛ } 
