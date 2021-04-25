/**
 * Huffman
 * @author: Jobenpreet Singh, B00849528
 * @date: April 02, 2021
 * @purpose: Assignment 5
 * @description:
 * Applies the Huffman algorithm on the massage contained in the given file.
 * Once the Huffman tree is made for each character codes are generated.
 * The codes are printed in the Huffman.txt.
 * The Codes in the Huffman.txt are used to encode the message. The encoded message is outputted in Encoded.txt.
 * The codes in the Huffman.txt are used to decode the message. The decoded message is outputted in Decoded.txt.
 *
 * Assumption:
 * The class assumes that no whitespace or new line character are not be encoded. Thus, will not be present
 * in the Huffman.txt. The presence of whitespace and new line character will make the method fail.
 * Huffman.txt will not have any code printed where probability is 0.0.
 */

// Import
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.*;

public class Huffman {

  /**
   * Encodes the massage contained in the file inputted by the user. The encoded message and the Huffman codes applied
   * are stored in Encoded.txt and Huffman.txt respectively.
   * @throws IOException
   */
  public static void encode()throws IOException {

    // Accept user input and initialize the file
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter the filename to read from/encode: ");
    String fileName = kb.next();
    File file = new File(fileName);
    Scanner fileReader = new Scanner(file);

    // Store the text in the String Variable
    String text = "";
    while (fileReader.hasNext()) {
      text += fileReader.nextLine();
      text += "\n";
    }

    // Count the number of occurrences of each character
    ArrayList<Pair> pairList = getPairsList(text);

    // Build Huffman tree
    ArrayList<BinaryTree<Pair>> result = new ArrayList<>();
    ArrayList<BinaryTree<Pair>> arr = new ArrayList<>();

    // Sort the pairs based on their probabilities.
    pairList.sort(Pair::compareTo);

    // Huffman Algorithm
    // 1. Construct node binary tree for each char and store them in ascending order.
    for (Pair p: pairList) {
      BinaryTree <Pair> binaryTree = new BinaryTree<>();
      binaryTree.makeRoot(p);
      arr.add(binaryTree);
    }


    //2. Pick the 2 smallest weight trees and make a parent root having
    // the sum of the probabilities of two Pairs
    BinaryTree<Pair> rootPair =  makeTree(result, arr);

    // Find the codes for each character
    String[] codeList = findEncoding(rootPair);

    // Print the output to Huffman.txt. Only probabilities greater than 0.0 are printed
    System.out.println("Printing codes to Huffman.txt");
    PrintWriter output = new PrintWriter("Huffman.txt");
    output.println("Symbol\tProb\tHuffman Code");

    for (int i = 0; i < codeList.length; i++) {

      char c= (char) i;
      double prob = 0;

      // Get the correct probability
      for (Pair pair : pairList) {
        if (pair.getValue() == i) {
          prob = pair.getProb();
        }
      }

      // No need to print character code not used
      if (prob != 0) {
        output.println();
        output.print( c + "\t\t" + prob +
            "\t\t" + codeList[i]);
      }
    }
    output.close();

    // Encode the text and print to Encoded.txt
    System.out.println("Printing encoded text to Encoded.txt");
    String encodedMessage = getEncodedMessage(text);
    PrintWriter printEncoding = new PrintWriter("Encoded.txt");
    printEncoding.println(encodedMessage);
    printEncoding.close();
  }

  /**
   * Decodes the text contained in the file given by the user. The decoding is done employing the code contained in the
   * file given by the user for the codes. The decoded message is outputted in Decoded.txt.
   * @throws IOException
   */
  public static void decode() throws IOException {

    // Accept encoded message file.
    System.out.print("Enter the file to read from/decode: ");
    Scanner kb = new Scanner(System.in);
    String fileName = kb.next();
    File file = new File(fileName);
    Scanner fileReader = new Scanner(file);

    // Store the text in the String Variable.
    String text = "";
    while (fileReader.hasNext()) {
      text += fileReader.nextLine();
      text += "\n";
    }

    // Transform the string in character array
    char[] chars = text.toCharArray();

    // Accept the code file
    HashMap <String, Character> charCodeMap = new HashMap<>();
    System.out.print("Enter the file of document containing Huffman codes: ");
    File codes = new File(kb.next());
    Scanner ls = new Scanner(codes);

    // consume/discard header row and blank line
    ls.nextLine();
    ls.nextLine();
    while(ls.hasNextLine()){
      String s = ls.next();
      char c = s.charAt(0);
      ls.next(); // consume/discard probability
      String str = ls.next();
      // Store code string and character value
      charCodeMap.put(str,c);
    }

    // Decode the encoded file and print to Decoded.txt
    System.out.println("Printing decoded text to Decoded.txt");
    String decodedStr = getDecodedStr(chars, charCodeMap);
    PrintWriter printEncoding = new PrintWriter("Decoded.txt");
    printEncoding.println(decodedStr);
    printEncoding.close();
  }

  /**
   * Returns the decoded string given the encoded string as character array. The Hashmap contains the code to be used
   * to decode.
   * @param chars, the string to decode as character array
   * @param map, the code to be used. map < String code, char character >
   * @return the decoded string
   */
  private static String getDecodedStr(char[] chars, HashMap<String, Character> map) {

    // Initialize variables
    String decodedStr = "";
    String key = "";

    for (char ch : chars) {
      // Take the first char
      String charStr = String.valueOf(ch);

      // If the char is whitespace or new line then just add.
      if (charStr.equals(" ") || charStr.equals("\n")) {
        decodedStr += charStr;
        continue;
      }

      // Store it as String key
      key += charStr;

      // If there is a key for the key, store it in the decodedStr. And remake another key.
      // Else increase the length of the key
      if (map.containsKey(key)) {
        decodedStr += map.get(key);
        key = "";
      }
    }
    return decodedStr;
  }


  /**
   * Makes a tree using Huffman algorithm.
   * @param result, is an empty list used to store roots
   * @param arr, is sorted list to process
   * @return a huffman tree.
   */
  private static BinaryTree<Pair> makeTree(ArrayList<BinaryTree<Pair>> result,
                                           ArrayList<BinaryTree<Pair>> arr) {

    // Initialize variables
    BinaryTree<Pair> root = new BinaryTree<>();
    char value = '⁂';
    double prob;
    double firstProb;
    double secondProb;
    BinaryTree<Pair> first;
    BinaryTree<Pair> second;

    // Deque and make tree
    while (arr.size() > 0) {
      root = new BinaryTree<>();

      // Base case
      if (arr.size() == 1 && result.isEmpty()) {
        makeOneNodeTree(arr, root, value);
      }
      else {

        // If the result list is empty
        if (result.isEmpty()) {
          // Deque the first 2 Pair from arr list
          first = arr.remove(0);
          second = arr.remove(0);
        } else {
          // Take the first element of each list
          firstProb = arr.get(0).getData().getProb();
          secondProb = result.get(0).getData().getProb();

          // Deque the smaller first element
          if (firstProb <= secondProb) {
            // Deque
            first = arr.remove(0);
            // Update probability if there arr is not empty. else make root with second.
            if (arr.isEmpty()) {
              second = result.remove(0);
              // Make tree
              prob = sumProb(first,second);
              makeRoot(root, value, prob, first, second);
              result.add(root);
              break;
            }

            // Arr is not empty
            firstProb = arr.get(0).getData().getProb();

          } else {
            first = result.remove(0);

            if (!result.isEmpty()) {
              secondProb = result.get(0).getData().getProb();
            }
            else {
              // set to the maximum, so that the second is selected from the arr list
              // the prob value will be updated in the next iteration
              secondProb = 1;
            }
          }

          // Deque the smaller second element
          second = getSecond(result, arr, firstProb, secondProb);
        }

        // Make tree
        prob = sumProb(first,second);
        makeRoot(root, value, prob, first, second);
      }
      // Add the root to the result list.
      result.add(root);
    }

    // If the result has more than 1 element make the tree with the nodes in the result list.
    while (result.size()>1) {
      root = new BinaryTree<>();

      // Get the first 2 elements
      first = result.remove(0);
      second = result.remove(0);

      // Make tree
      prob = sumProb(first,second);
      makeRoot(root,value,prob,first,second);
      result.add(root);
    }

    // Return the root of the Huffman tree.
    return root;
  }

  /**
   * Returns the binary tree representing the 2nd smaller probability
   * @param result, stores the roots
   * @param arr, stores the characters in the message
   * @param firstProb, first prob
   * @param secondProb, second prob
   * @return Binary tree with smaller prob
   */
  private static BinaryTree<Pair> getSecond(ArrayList<BinaryTree<Pair>> result,
                                            ArrayList<BinaryTree<Pair>> arr,
                                            double firstProb, double secondProb) {
    BinaryTree<Pair> second;
    if (firstProb <= secondProb) {
      second = arr.remove(0);
    } else {
      second = result.remove(0);
    }
    return second;
  }

  /**
   * Initialized the given root to the value and with the given Binary-tree
   * @param arr, The list of binary tree. It has only one element
   * @param root to be initialized
   * @param value the value of the root
   */
  private static void makeOneNodeTree(ArrayList<BinaryTree<Pair>> arr,
                                      BinaryTree<Pair> root,
                                      char value) {
    Pair newPair;
    BinaryTree<Pair> first;
    double prob;
    double firstProb;
    // Deque
    first = arr.remove(0);
    firstProb = first.getData().getProb();
    prob = firstProb;

    // Make root
    newPair = new Pair(value, prob);
    root.makeRoot(newPair);
    root.setLeft(first);
  }

  /**
   * Initializes a root element and attaches the right and left subtrees.
   * @param root, is the root reference
   * @param value, value of the root
   * @param prob, probability of the root
   * @param first, the left attachment
   * @param second, the right attachment
   */
  private static void makeRoot(BinaryTree<Pair> root,
                               char value, double prob,
                               BinaryTree<Pair> first,
                               BinaryTree<Pair> second) {
    Pair newPair = new Pair(value, prob);
    root.makeRoot(newPair);
    root.setLeft(first);
    root.setRight(second);
  }

  /**
   * Sums two probabilities of the given nodes
   * @param first The first tree node
   * @param second The second tree node
   * @return the sum
   */
  private static double sumProb(BinaryTree<Pair> first, BinaryTree<Pair> second) {
    double secondProb;
    double firstProb;
    firstProb = first.getData().getProb();
    secondProb = second.getData().getProb();
    return firstProb + secondProb;
  }


  /**
   * The method counts the number of occurrences,
   * determines the relative probability of each character.
   * @param text, it is the message which is used to count frequencies
   * @return list of Pair objects with characters and relative probability.
   */
  private static ArrayList<Pair> getPairsList(String text) {

    // Count the number of occurrences of each character
    int[] freq = new int[256];
    char[] chars = text.replaceAll("\\s", "").toCharArray();
    for(char c: chars) {
      freq[c]++;
    }

    // Determine the relative probability and store them with Pair
    ArrayList<Pair> pairList = new ArrayList<>();
    for (int i = 0; i < 256; i++) {
      char value = (char) i;
      double prob = Math.round(freq[i] * 10000d / chars.length) / 10000d;

      Pair charProb = new Pair(value, prob);
      pairList.add(charProb);
    }

    return pairList;
  }


  /**
   * Returns the encoded letters given the Huffman binary tree
   * @param bt, tree
   * @return Encoded code
   */
  private static String[] findEncoding(BinaryTree<Pair> bt){
    String[] result = new String[256];
    findEncoding(bt, result, "");
    return result;
  }

  /**
   * Produces a code from the Huffman tree given. Each letter will have its corresponding code.
   * @param bt, given tree
   * @param a, the string to store the encoded code
   * @param prefix, prefix
   */
  private static void findEncoding(BinaryTree<Pair> bt, String[] a,
                                   String prefix){
    // test is node/tree is a leaf
    if (bt.getLeft()==null && bt.getRight()==null){
      a[bt.getData().getValue()] = prefix;
    }
    // recursive calls
    else{
      findEncoding(bt.getLeft(), a, prefix+"0");
      findEncoding(bt.getRight(), a, prefix+"1");
    }
  }

  /**
   * Encode the given message
   * @param message, the string to be encoded
   * @return Encoded string
   */
  public static String getEncodedMessage(String message) throws  FileNotFoundException{

    // Initialize variables
    String encodedStr = "";
    HashMap<String, String> valueCodeList = new HashMap<>();
    char[] chars = message.toCharArray();

    // Read file and store the char and code
    File codeFiles = new File("Huffman.txt");
    Scanner codeReader = new Scanner(codeFiles);

    // skip headers
    codeReader.nextLine();
    codeReader.nextLine();
    while (codeReader.hasNext()) {
      String value = codeReader.next();
      codeReader.next();
      String code = codeReader.next();
      valueCodeList.put(value, code);
    }

    // Go through each char and compare it to the value
    for (char aChar : chars) {
      String value = String.valueOf(aChar);
      if (value.equals(" ") || value.equals("\n")) {
        encodedStr += value;
        continue;
      }
      String code = valueCodeList.get(value);
      encodedStr += code;
    }
    return encodedStr;
  }
}
