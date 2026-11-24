
import java.util.*;

public class TextCompressionTool {

    static class Node {
        char ch;
        int freq;
        Node left, right;

        Node(char ch, int freq) {
            this.ch = ch;
            this.freq = freq;
        }

        Node(Node left, Node right) {
            this.ch = '\0';
            this.freq = left.freq + right.freq;
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    static Node root;
    static HashMap<Character, Integer> frequencyMap;
    static HashMap<Character, String> codeMap;

    // Build Huffman Tree
    static void buildTree(String text) {

        frequencyMap = new HashMap<>();
        codeMap = new HashMap<>();

        for (char ch : text.toCharArray()) {
            frequencyMap.put(ch,
                    frequencyMap.getOrDefault(ch, 0) + 1);
        }

        // Deterministic tie-breaking by character value
        PriorityQueue<Node> pq = new PriorityQueue<>(
            (a, b) -> {
                if (a.freq != b.freq)
                    return Integer.compare(a.freq, b.freq);

                if (a.isLeaf() && b.isLeaf())
                    return Character.compare(a.ch, b.ch);

                if (a.isLeaf() != b.isLeaf())
                    return a.isLeaf() ? -1 : 1;

                return 0;
            }
        );

        for (char ch : frequencyMap.keySet()) {
            pq.add(new Node(ch, frequencyMap.get(ch)));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();

            pq.add(new Node(left, right));
        }

        root = pq.poll();

        generateCodes(root, "");
    }

    // Generate Huffman Codes
    static void generateCodes(Node node, String code) {

        if (node == null)
            return;

        if (node.isLeaf()) {
            codeMap.put(node.ch,
                    code.isEmpty() ? "0" : code);
            return;
        }

        generateCodes(node.left, code + "0");
        generateCodes(node.right, code + "1");
    }

    // Display Code Table
    static void showCodeTable() {

        if (codeMap == null || codeMap.isEmpty()) {
            System.out.println("No code table available.");
            return;
        }

        System.out.println("\nHuffman Code Table:");

        ArrayList<Character> keys =
                new ArrayList<>(codeMap.keySet());

        Collections.sort(keys);

        for (char ch : keys) {
            String display = ch == ' ' ? "Space" : String.valueOf(ch);
            System.out.println(display + " = " + codeMap.get(ch));
        }
    }

    // Compress Text
    static void compress(String text) {

        if (text.isEmpty()) {
            System.out.println("Input is empty. Nothing to compress.");
            return;
        }

        buildTree(text);

        StringBuilder encoded = new StringBuilder();

        for (char ch : text.toCharArray()) {
            encoded.append(codeMap.get(ch));
        }

        int originalBits = text.length() * 8;
        int compressedBits = encoded.length();

        double saved = (originalBits - compressedBits)
                * 100.0 / originalBits;

        showCodeTable();

        System.out.println("\nEncoded: " + encoded);
        System.out.println("(" + compressedBits + " bits)");

        System.out.println("Original: " + originalBits
                + " bits | Saved: "
                + String.format(Locale.US, "%.2f", saved) + "%");
    }

    // Decode Binary String
    static void decode(String bits) {

        if (bits.isEmpty()) {
            System.out.println("Input is empty. Nothing to decode.");
            return;
        }

        if (root == null) {
            System.out.println("Please compress text first.");
            return;
        }

        for (char bit : bits.toCharArray()) {
            if (bit != '0' && bit != '1') {
                System.out.println("Invalid input. Only 0 and 1 are allowed.");
                return;
            }
        }

        StringBuilder decoded = new StringBuilder();

        // Special case: only one unique character
        if (root.isLeaf()) {
            for (char bit : bits.toCharArray()) {
                if (bit != '0') {
                    System.out.println("Invalid encoded string.");
                    return;
                }
                decoded.append(root.ch);
            }
        } else {
            Node current = root;

            for (char bit : bits.toCharArray()) {

                current = (bit == '0')
                        ? current.left : current.right;

                if (current == null) {
                    System.out.println("Invalid encoded string.");
                    return;
                }

                if (current.isLeaf()) {
                    decoded.append(current.ch);
                    current = root;
                }
            }

            if (current != root) {
                System.out.println("Invalid encoded string.");
                return;
            }
        }

        System.out.println("Decoded text: " + decoded);
    }

    // Main Method
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== TEXT COMPRESSION TOOL =====");
            System.out.println("1. Compress Text");
            System.out.println("2. Decode Text");
            System.out.println("3. Show Code Table");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            switch (choice) {

                case "1":
                    System.out.print("Enter text to compress: ");
                    String text = sc.nextLine();
                    compress(text);
                    break;

                case "2":
                    System.out.print("Enter binary string: ");
                    String bits = sc.nextLine();
                    decode(bits);
                    break;

                case "3":
                    showCodeTable();
                    break;

                case "4":
                    System.out.println("Thank you for using Text Compression Tool!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
