import javax.rmi.CORBA.Util;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Arrays;


import static java.lang.Integer.max;
import static java.lang.Integer.toBinaryString;
import static java.lang.Math.abs;
import static java.lang.Math.min;

public class EditDistance implements EditDistanceInterface {

    int c_i, c_d, c_r;
    int n, m;
    static int MAX = Integer.MAX_VALUE;
    static int UNDEF = -1;

    public EditDistance(int c_i, int c_d, int c_r) {
        this.c_i = c_i;
        this.c_d = c_d;
        this.c_r = c_r;
    }

    public int[][] getEditDistanceDP(String s1, String s2) {

        int n = s1.length();
        int m = s2.length();

        int[][] d = new int[n + 1][m + 1];

        int factor_if_equal;

        //compute d_0_j and d_i_0
        for (int i = 0; i < n + 1; i++)
            d[i][0] = i * this.c_d;
        for (int j = 0; j < m + 1; j++)
            d[0][j] = j * this.c_i;

        //compute the other values according to the lines
        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {


                if (s1.charAt(i - 1) == s2.charAt(j - 1))
                    factor_if_equal = d[i - 1][j - 1];
                else
                    factor_if_equal = d[i - 1][j - 1] + this.c_r;
                d[i][j] = min(min(factor_if_equal, d[i - 1][j] + this.c_d), d[i][j - 1] + this.c_i);

            }
        }

        return d;
    }


    public List<String> getMinimalEditSequence(String s1, String s2) {
        HashMap<Integer, Integer> d = new HashMap();
        HashMap<Integer, String> op = new HashMap();
        int[] aux_value = new int[3];
        String[] aux_string = new String[3];
        int X;
        int arg;
        int bound;
        LinkedList<String> ls = new LinkedList<>();

        int n = s1.length();
        int m = s2.length();
        this.n = n;
        this.m = m;


        X = 1; // upper bound
        while(true) {
            bound = abs(n - m) + X;

            d.clear();

            op.clear();


            //compute d_0_j and d_i_0
            for (int i = 0; i < n + 1; i++) {
                d.put(cantor(i, 0), i * this.c_d);
                op.put(cantor(i, 0), "delete(" + (i - 1) + ")");

            }
            for (int j = 0; j < m + 1; j++) {
                d.put(cantor(0, j), j * this.c_i);
            }
            op.put(cantor(0, 0), "nothing");
            for (int j = 1; j < m + 1; j++) {
                op.put(cantor(0, j), "insert(" + (j - 1) + "," + s2.charAt(j - 1) + ")");
            }


            //compute the other values according to the lines
            for (int i = 1; i < n + 1; i++) {

                for (int j = max(1, i - bound); j <= min(m , i + bound); j++) {


                    if (i == j - bound)
                        aux_value[0] = Integer.MAX_VALUE;
                    else {
                        aux_value[0] = d.get(cantor(i - 1, j)) + this.c_d;
                        aux_string[0] = "delete(" + (i - 1) + ")";
                    }
                    if (j == i - bound)
                        aux_value[1] = Integer.MAX_VALUE; // we do not care about value outside of the diagonal, so we set them to infinity to make sure they're not chosen
                    else {
                        aux_value[1] = d.get(cantor(i, j - 1)) + this.c_i;
                        aux_string[1] = "insert(" + (j - 1) + "," + s2.charAt(j - 1) + ")";
                    }
                    if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                        if ((j == i - bound) || (i == j - bound))
                            aux_value[2] = Integer.MAX_VALUE;
                        else {
                            aux_value[2] = d.get(cantor(i - 1, j - 1));
                            aux_string[2] = "nothing";
                        }

                    }
                    else {
                        if ((j == i - bound) || (i == j - bound))
                            aux_value[2] = Integer.MAX_VALUE;
                        else {
                            aux_value[2] = d.get(cantor(i - 1, j - 1)) + this.c_r;
                            aux_string[2] = "replace(" + (i - 1) + "," + s2.charAt(j - 1) + ")";
                        }

                    }


                    arg = argmin(aux_value);
                    d.put(cantor(i, j), aux_value[arg]);
                    op.put(cantor(i, j), aux_string[arg]);


                }
            }



            if (X * (this.c_i + this.c_d) >= d.get(cantor(n, m)))
                break;
            else
                X *= 2;

        }


        // read op in reverse to find the sequence we're looking for

        Stack stack = new Stack(); //we need to read the insert instruction in the reverse order compared to the delete instructions
        int i = n;
        int j = m;
        while(i >= 0 && j >= 0) {

            char first_letter = op.get(cantor(i, j)).charAt(0);
            if (first_letter == "n".toCharArray()[0]){
                i--;
                j--;
            }
            if (first_letter == "d".toCharArray()[0]) {
                ls.add(op.get(cantor(i, j)));
                i--;

            }
            if (first_letter == "i".toCharArray()[0]) {
                stack.push(op.get(cantor(i, j)));
                j--;
            }
            if (first_letter == "r".toCharArray()[0]){
                ls.add(op.get(cantor(i, j)));
                i--;
                j--;
            }

        }

        while (! stack.empty())
            ls.add((String) stack.pop());  //we need to read the insert instruction in the reverse order compared to the delete instructions

        return ls;
    }

    public int argmin(int[] arr) {
        int n = arr.length;
        int res = 0;

        for (int i = 1; i < n; i++) {
            if (arr[i] < arr[res])
                res = i;
        }


        return res;

    }

    public int cantor(int i, int j){
        return i * (this.m + 1) + j;
    }


}
//    }
//        if (c_r == 6) {
//            ls.add("delete(1)");
//            ls.add("delete(1)");
//            ls.add("insert(2,c)");
//            ls.add("insert(3,b)");
//        }
//        else {
//            ls.add("replace(1,d)");
//            ls.add("replace(3,b)");
//        }
//        return ls;
//        /* Code block to be removed ends. */
//    }

//
//        for (int i=0; i<n+1; i++) {
//        for (int j = 0; j < m + 1; j++) {
//        System.out.print(op[i][j]);
//        }
//        System.out.println("");
//        }