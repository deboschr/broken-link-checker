package com.unpar.brokenlinkchecker.graph;

/**
 * Kelas Edge merepresentasikan sisi (edge) dari graf.
 */
public class Edge {
   private final Node from;
   private final Node to;
   private final int weight;

   public Edge(Node from, Node to) {
      this(from, to, 1);
   }

   public Edge(Node from, Node to, int weight) {
      this.from = from;
      this.to = to;
      this.weight = weight;
   }

   public Node getFrom() {
      return from;
   }

   public Node getTo() {
      return to;
   }

   public int getWeight() {
      return weight;
   }

   @Override
   public String toString() {
      return from + " -> " + to + " (w=" + weight + ")";
   }
}