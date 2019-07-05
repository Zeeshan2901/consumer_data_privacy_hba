
## Generate Nonce

Alice - generates a random number, she sends a hash to Bob

Bob - generate sa random number, he sends a hash to Alice

Alice - sends the random number to Bob

Bob - sends the randoom number Alice

Both - they xor the random numbers and that's the nonce.



## Main protocol

Alice - hash her genome, she is going to take the hash values (computed from the genotypes + nonce), she is going to sort that,  then she is going to hash of all that and then she is going to send the hash to Bob

Bob - the same thing

Alice - sends the sorted hash values to Bob

Bob - the same thing


Both can count the number of matches. They can tell where the matches come from in their genomes.
