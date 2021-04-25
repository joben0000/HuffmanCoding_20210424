/**
 * HuffmanDemo
 * @author: Jobenpreet Singh, B00849528
 * @date: April 5, 2021
 * @purpose: Assignment 5
 * @description: The method calls the encode and decode method of the Huffman class.
 */

import java.io.*;
import java.util.PriorityQueue;

public class HuffmanDemo {
  public static void main(String args []) throws IOException {
    Huffman.encode();
    System.out.println("\n* * * * *\n");
    Huffman.decode();
  }
}
