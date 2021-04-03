# Cache-
The code implements cache mapping sequentially. Code has been implemented in **java**​ assuming that the binary address is of 16 bits.

Input
cSize- ​cache size
cl- ​no of cache lines
bSize- ​block size
After taking these inputs , we need to decide which of the mapping to do we want to print.
1- direct mapping
Or
2- associative mapping
Or
3- K-way associative mapping
Then, we will print the no. of inputs(say n) we want to give and print the inputs in the next n lines.
Then we will print the data at the requested address and finally print if there is a cache miss/ cache hit/ invalid address.
The input will be of the form:-
“Enter the cache size: ”
**cache size**
“Enter the no. of cache lines: ”
**no of cache lines**
“Enter the block size: “
**block size**
“The 3 mappings are:”
**1.Direct mapping** **2.Associative Mapping** **3.K-way Associative mapping**
“Enter the mapping you want:”
**mapping number**
“Enter number of inputs you want to give:”
**no. of inputs**
// if no of inputs is 5, then 5 lines containing inputs
** **
** **
** **
** **
** **


Output
The output will be cache miss, cache hit, invalid address or invalid input.

  
