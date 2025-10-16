package com.unpar.brokenlinkchecker.graph;

import java.util.*;

/**
 * Kelas Graph merepresentasikan graf berarah atau tidak berarah.
 * Menyimpan hubungan antar Node menggunakan adjacency list.
 */
public class Graph {
   private final boolean directed;
   private final Map<Node, List<Edge>> adjacencyList;

   public Graph(boolean directed) {
      this.directed = directed;
      this.adjacencyList = new HashMap<>();
   }

   /** Tambahkan node baru ke graf */
   public void addNode(Node node) {
      adjacencyList.putIfAbsent(node, new ArrayList<>());
   }

   /** Tambahkan edge (sisi) antara dua node */
   public void addEdge(Node from, Node to, int weight) {
      addNode(from);
      addNode(to);
      adjacencyList.get(from).add(new Edge(from, to, weight));
      if (!directed) {
         adjacencyList.get(to).add(new Edge(to, from, weight));
      }
   }

   /** Tampilkan isi graf */
   public void printGraph() {
      System.out.println("=== GRAPH STRUCTURE ===");
      for (Node node : adjacencyList.keySet()) {
         System.out.print(node + " -> ");
         List<Edge> edges = adjacencyList.get(node);
         for (Edge edge : edges) {
            System.out.print(edge.getTo() + " ");
         }
         System.out.println();
      }
   }

   /** DFS traversal */
   public void dfs(Node start) {
      System.out.println("DFS traversal mulai dari " + start + ":");
      Set<Node> visited = new HashSet<>();
      dfsHelper(start, visited);
      System.out.println();
   }

   private void dfsHelper(Node node, Set<Node> visited) {
      visited.add(node);
      System.out.print(node + " ");
      for (Edge edge : adjacencyList.getOrDefault(node, List.of())) {
         if (!visited.contains(edge.getTo())) {
            dfsHelper(edge.getTo(), visited);
         }
      }
   }

   /** BFS traversal */
   public void bfs(Node start) {
      System.out.println("BFS traversal mulai dari " + start + ":");
      Set<Node> visited = new HashSet<>();
      Queue<Node> queue = new LinkedList<>();

      visited.add(start);
      queue.add(start);

      while (!queue.isEmpty()) {
         Node current = queue.poll();
         System.out.print(current + " ");
         for (Edge edge : adjacencyList.getOrDefault(current, List.of())) {
            Node neighbor = edge.getTo();
            if (!visited.contains(neighbor)) {
               visited.add(neighbor);
               queue.add(neighbor);
            }
         }
      }
      System.out.println();
   }
}
