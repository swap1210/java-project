import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Arrays;

class Inventory implements Comparable<Inventory> {
    String model;
    int price;
    int size;
    Inventory(String x, int y, int z){
        this.model = x;
        this.price = y;
        this.size = z;
    }

    @Override
    public int compareTo(Inventory o) {
        if(this.price == o.price && this.size == o.size && this.model.equals(o.model)){
            return 0;
        }else if( this.price > o.price){
            return 1;
        }else if(this.price == o.price && this.model.charAt(0)<o.model.charAt(0)){
            return 1;
        }else if(this.price == o.price && this.model.charAt(0) == o.model.charAt(0) && this.model.charAt(0) == 'D' && this.size > o.size){
            return 1;
        }else if(this.price == o.price && this.model.charAt(0) == o.model.charAt(0) && this.model.charAt(0) == 'L' && this.size < o.size){
            return 1;
        }
        else{
            return -1;
        }
    }
}



public class Computer {
 
    public static void main(String[] args) {
        Sortsolution(new String[] { "L01", "L02", "D01" }, new int[] { 900, 900, 850 }, new int[] { 13, 15, 1 });
    }

    public static String[] Sortsolution(String[] A, int[] B, int[] C) {

        // Map<String, int[]> map = new TreeMap<String, int[]>();
        int sizeN = A.length;
        Inventory[] inv = new Inventory[sizeN];

        for (int i = 0; i < sizeN; i++) {
            inv[i] = new Inventory(A[i], B[i], C[i]);
        }

        // Inventory[] invSample = new Inventory[2];
        // Inventory y = new Inventory("L02", 800, 0);
        // Inventory x = new Inventory("L01", 800, 1);
        // invSample[0]=x;
        // invSample[1]=y;
        
        for(Inventory i : inv){
            System.out.println("Before: "+i.model + " " + i.price + " " + i.size);
        }
        System.out.println();
        Arrays.sort(inv);

        for(Inventory i : inv){
            System.out.println("After: "+i.model + " " + i.price + " " + i.size);
        }


        return null;
    }
}