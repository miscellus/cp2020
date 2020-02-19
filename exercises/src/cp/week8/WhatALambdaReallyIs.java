/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp.week8;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 *
 * @author jakob
 */
public class WhatALambdaReallyIs {
    /*
    Opt: Define a generic class Pair<K,V> that can store pairs of values of any types.
    Opt: Create a List of Pair<String, Integer> with some values. For each pair containing a string s and an integer n, we say that s is associated to n.
    Opt: For each string (first value of a pair) in the list, print the sum of all integers associated to that string.
    */
    
    public static void main(String args[]) {
        List<Pair<String, Integer>> list = new ArrayList<>();
        list.add(new Pair<>("John", 1));
        list.add(new Pair<>("John", 2));
        list.add(new Pair<>("Paul", 3));
        list.add(new Pair<>("Paul", 2));
        list.add(new Pair<>("Paul", 4));
        list.add(new Pair<>("Ringo", 7));
        list.add(new Pair<>("Ringo", 2));
        list.add(new Pair<>("Ringo", 5));
        list.add(new Pair<>("George", 16));
        
        Map<String, Integer> stringToSum = new Hashtable<>();
        
        for (Pair<String, Integer> p : list) {
            stringToSum.merge(
                    p.key,
                    p.value,
                    new MergeFunction());
        }
        
        for (Map.Entry<String, Integer> e: stringToSum.entrySet()) {
            System.out.println(e.getKey()+ ": " + e.getValue());
        }
    }
    
    public static class MergeFunction implements BiFunction<Integer, Integer, Integer> {
        @Override
        public Integer apply(Integer currentValue, Integer newValue) {
            return currentValue + newValue;
        }
    }
    
    public static class Pair<K, V> {
        public final K key;
        public final V value;
        
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
    }

}
