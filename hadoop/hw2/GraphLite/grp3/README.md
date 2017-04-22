## Directed Triangle Count
Directed triangle count, implement by modifying the PageRank example.

Obviously, if you draw some example in your scrap paper, you will find that `#in-triangles = #out-triangles = #through-triangles`, so we can simply count `in & cycle triangles or out & cycle triangles`, since it is easy to determine which is in or out triangles.
Here we take in & cycle triangles for an example. Obviously, we can know the fact that, for one node:
- if the previous of its previous node is its successive node, then they form a cycle triangle;
- while if the previous of its previous node is its successive node is its previous node, then they form a in triangle.

Similarly, for out & cycle triangles, we know that, for one node:
- if the successive of its successive node is its successive node, then they form a out triangle;
- while if the successive of its successive node is its previous node, then they form a cycle triangle.
