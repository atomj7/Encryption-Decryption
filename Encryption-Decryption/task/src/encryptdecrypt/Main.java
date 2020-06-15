package encryptdecrypt;

import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        String operation = "enc";
        String data = "";
        String fileOutName = "";
        String fileInName = "";
        String algorithm = "shift";
        boolean isData = false;
        boolean isFileIn = false;
        boolean isFileOut = false;
        int key = 0;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-mode" :
                    operation = args[i+1];
                    break;
                case "-key" :
                    key = Integer.parseInt(args[i+1]);
                    break;
                case "-data" :
                    data = args[i+1];
                    isData = true;
                    break;
                case "-out" :
                    fileOutName = args[i+1];
                    isFileOut = true;
                    break;
                case "-in" :
                    fileInName = args[i+1];
                    isFileIn= true;
                    break;
                case "-alg" :
                    algorithm = args[i+1];
                    break;
            }
        }

        File fileOut = new File(fileOutName);
        File fileIn = new File(fileInName);

        data = getDataFromFile(isData, isFileIn, fileIn);

        SelectionContext selectionContext = new SelectionContext();
        switch (algorithm) {
            case "shift":
                selectionContext.setAlgorithm(new Shift(data, key, isFileOut, fileOut, operation));
                selectionContext.applyAlgorithm();
                break;
            case "unicode":
                selectionContext.setAlgorithm(new Unicode(data, key, isFileOut, fileOut, operation));
                selectionContext.applyAlgorithm();
                break;
            default:
                System.out.println("Unknown operation");
                break;
        }

    }

    public static String getDataFromFile(boolean isData, boolean isFileIn, File fileIn) {
        String data = "";
        if(!isData && isFileIn) {
            try (Scanner fileScanner = new Scanner(fileIn)) {
                data = fileScanner.nextLine();
            } catch (FileNotFoundException e) {
                System.out.println("Error");
            }
        }
        return  data;
    }
}

class DecryptionAndEncryption {

    private static int size = 26;

    public static void getEncryptionByUnicode(String chars, int shift, boolean isFileOut, File fileOut) {
        try (FileWriter fileWriter = new FileWriter(fileOut)) {
            for (int i = 0 ; i < chars.length(); i++) {
                char shiftItem = (char) (chars.charAt(i) + shift);
                if(!isFileOut) {
                    System.out.print(shiftItem);
                } else {
                    fileWriter.write(shiftItem);
                }
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    public static void getDecryptionByUnicode(String chars, int shift, boolean isFileOut, File fileOut) {
        try (FileWriter fileWriter = new FileWriter(fileOut)) {
            for (int i = 0 ; i < chars.length(); i++) {
                char shiftItem = (char) (chars.charAt(i) - shift);
                if (!isFileOut) {
                    System.out.print(shiftItem);
                } else {
                    fileWriter.write(shiftItem);
                }
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    public static void getEncryptionByShift(String chars, int shift, boolean isFileOut, File fileOut ) {
        try (FileWriter fileWriter = new FileWriter(fileOut)) {
            for (char item : chars.toCharArray()) {
                char shiftItem;
                if (item >= 'a' && item <= 'z') {
                    char originalPos = (char) (item - 'a');
                    char newPos = (char) ((originalPos + shift) % size);
                    shiftItem = (char) ('a' + newPos );
                } else {
                    shiftItem = item;
                }
                if(!isFileOut) {
                    System.out.print(shiftItem);
                } else {
                    fileWriter.write(shiftItem);
                }
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    public static void getDecryptionByShift(String chars, int shift, boolean isFileOut, File fileOut ) {
        getEncryptionByShift(chars, size - (shift % size), isFileOut, fileOut);
    }
}

interface DecryptionSelectionAlgorithm {

    public void decryption();
}

class  SelectionContext {

    private DecryptionSelectionAlgorithm algorithm;

    public void setAlgorithm(DecryptionSelectionAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void applyAlgorithm() {
        this.algorithm.decryption();
    }
}

class Shift implements DecryptionSelectionAlgorithm {

    private String chars;
    private int shift;
    private boolean isFileOut;
    private String operation;
    private File fileOut;

    Shift(String chars, int shift, boolean isFileOut, File fileOut, String operation) {
        this.chars = chars;
        this.fileOut = fileOut;
        this.isFileOut = isFileOut;
        this.shift = shift;
        this.operation = operation;
    }

    @Override
    public void decryption() {
        if( operation.equals("dec")) {
            DecryptionAndEncryption.getDecryptionByShift(chars, shift, isFileOut, fileOut);
        } else {
            DecryptionAndEncryption.getEncryptionByShift(chars, shift, isFileOut, fileOut);
        }
    }
}

class Unicode implements DecryptionSelectionAlgorithm {
    private String chars;
    private int shift;
    private boolean isFileOut;
    private String operation;
    private File fileOut;

    Unicode(String chars, int shift, boolean isFileOut, File fileOut, String operation) {
        this.chars = chars;
        this.fileOut = fileOut;
        this.isFileOut = isFileOut;
        this.shift = shift;
        this.operation = operation;
    }

    @Override
    public void decryption() {
        if( operation.equals("dec")) {
            DecryptionAndEncryption.getDecryptionByUnicode(chars, shift, isFileOut, fileOut);
        } else {
            DecryptionAndEncryption.getEncryptionByUnicode(chars, shift, isFileOut, fileOut);
        }
    }
}