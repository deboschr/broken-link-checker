package com.unpar.brokenlinkchecker.graph;

public class GraphDemo {
   public static void main(String[] args) {
      // true = directed graph, false = undirected graph
      Graph graph = new Graph(false);

      Node A = new Node("A");
      Node B = new Node("B");
      Node C = new Node("C");
      Node D = new Node("D");

      graph.addEdge(A, B, 1);
      graph.addEdge(A, C, 1);
      graph.addEdge(B, D, 1);
      graph.addEdge(C, D, 1);

      graph.printGraph();

      graph.dfs(A);
      graph.bfs(A);
   }
}