package com.unpar.brokenlinkchecker.graph;


import java.util.Objects;

/**
 * Kelas Node merepresentasikan simpul (vertex) pada graf.
 */
public class Node {
   private final String name;

   public Node(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   // equals & hashCode agar Node bisa digunakan di Set atau Map
   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (!(o instanceof Node))
         return false;
      Node node = (Node) o;
      return Objects.equals(name, node.name);
   }

   @Override
   public int hashCode() {
      return Objects.hash(name);
   }

   @Override
   public String toString() {
      return name;
   }
}