package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;

public class UniqueIPAddressCounter {

    private static final int BITSET_SIZE = Integer.MAX_VALUE;
    private static final int TEST_COUNT = 1_000_000;
    private static final Random rand = new Random();


    //Need to improve hashing algorithm to reduce number of collisions.
    //Currently, collisions occurs in ~0.02% cases
    public static void main(String[] args) {
        String filePath = "path/to/your/large/ipfile.txt";
        tests();
        try {
            System.out.println("Count in file: " + countUniqueIPAddresses(filePath));
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private static void tests() {
        countSameIPAddressesTest();
        countUniqueIPAddressesTest();
        countUniqueIPAddressesCloseResultToNaiveTest();
    }

    private static int countUniqueIPAddresses(String filePath) throws IOException {
        BitSet bitSet = new BitSet(BITSET_SIZE);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                bitSet.set(Math.absExact(line.hashCode()));
            }
        }

        return bitSet.cardinality();
    }

    private static void countSameIPAddressesTest() {
        BitSet bitSet = new BitSet(BITSET_SIZE);
        String str= String.join(".",
                rand.nextInt(256) + "",
                rand.nextInt(256) + "",
                rand.nextInt(256) + "",
                rand.nextInt(256) + "");
        for (int i = 0; i < TEST_COUNT; i++) {
            bitSet.set(Math.absExact(str.hashCode()));
        }
        System.out.printf("Test 1. Assume exactly 1, got: %s%n", bitSet.cardinality());
    }

    private static void countUniqueIPAddressesTest() {
        BitSet bitSet = new BitSet(BITSET_SIZE);
        String str;
        for (int i = 0; i < TEST_COUNT; i++) {
            str = String.join(".",
                    rand.nextInt(256) + "",
                    rand.nextInt(256) + "",
                    rand.nextInt(256) + "",
                    rand.nextInt(256) + "");
            bitSet.set(Math.absExact(str.hashCode()));
        }

        System.out.printf("Test 2. Assume at least 1, got: %s%n", bitSet.cardinality());
    }

    private static void countUniqueIPAddressesCloseResultToNaiveTest() {
        System.out.println("Test 3");
        BitSet bitSet = new BitSet(BITSET_SIZE);
        HashSet<String> set = new HashSet<>();
        String str;
        var collisionCount = 0;
        for (int i = 0; i < TEST_COUNT; i++) {
            str = String.join(".",
                    rand.nextInt(256) + "",
                    rand.nextInt(256) + "",
                    rand.nextInt(256) + "",
                    rand.nextInt(256) + "");

            var hash = Math.absExact(str.hashCode());

            if (bitSet.get(hash) && !set.contains(str)) {
                    var collision = set.stream()
                            .filter(el -> Math.absExact(el.hashCode()) == hash)
                            .findFirst()
                            .orElse("Error");
                    collisionCount++;
                    System.out.println("Hash collision on ip addr: " + str + " and " + collision);
            }

            set.add(str);
            bitSet.set(hash);
        }
        System.out.printf("%nHashSet size is: %s" +
                        "%nBitSet size is: %s" +
                        "%nCollision count is: %s%n",
                set.size(),
                bitSet.cardinality(),
                collisionCount
        );
        System.out.printf("Assert that bitSet+collision and hashSet values are equal: %s%n" +
                "Collision rate is: ~%.3f%%%n",
                (collisionCount + bitSet.cardinality()) == set.size(),
                (double)collisionCount / TEST_COUNT * 100
        );
    }
}
