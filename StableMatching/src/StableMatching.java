public class StableMatching implements StableMatchingInterface {
    @Override
    public int[][] constructStableMatching(int[] menGroupCount, int[] womenGroupCount, int[][] menPrefs, int[][] womenPrefs) {
        int i; //group of man currently matching
        int a; //number of men in this group
        int prefered_group; //prefered woman group of man group i (taking former rejection into account)
        boolean rejected;
        boolean change;
        int b;
        int min;
        int worst_partner_in_prefered_group;
        int m = menGroupCount.length;
        int w = womenGroupCount.length;
        int[] bachelorMen = menGroupCount.clone();
        int[] bachelorWomen = womenGroupCount.clone();
        int sumBachelorMen = sum(bachelorMen);
        int[] counterMen = new int[m]; // counts the number of time the group of man i has been rejected, i.e men of group i can look up directly group of women (prefered + counter)
        int[] rankWorstPartner = new int[w]; //enable fast seach of worst partner for a woman group


        int[][] M = new int[m][w];  //matrix such as M[i][j] contains the number of men of group i married to women of group j

        // let's create a matrix w*m such as A[i][j] = rank of preference of man group j for woman group i
        int[][] womenPrefs_no_order = new int[w][m];
        for (int l =0; l < w; l++) {
            for (int j =0; j < m; j++){
                womenPrefs_no_order[l][womenPrefs[l][j]] = j;
            }
        }

        i = find_first_superior_to_quota(bachelorMen, (double) sumBachelorMen / (double) (2 * m)); // find i such as the number of bachelor in group i is > to S/2m
        while (sumBachelorMen > 0){

            a = bachelorMen[i];
            rejected = true; //has the group i been rejected during this iteration ie do we increment counterMen[i].
            change = true; // do we want to keep the same group of men
            prefered_group = menPrefs[i][counterMen[i]];


            worst_partner_in_prefered_group = womenPrefs[prefered_group][rankWorstPartner[prefered_group]]; // worst partner of the women group prefered_group

            // are there single girls in the prefered_group ?
            if (bachelorWomen[prefered_group] > 0){
                rejected = false;
                b = bachelorWomen[prefered_group];
                min = Math.min(a, b);
                bachelorMen[i] -= min;
                sumBachelorMen -= min;
                bachelorWomen[prefered_group] -= min;
                if (rankWorstPartner[prefered_group] < womenPrefs_no_order[prefered_group][i])
                    rankWorstPartner[prefered_group] = womenPrefs_no_order[prefered_group][i];
                M[i][prefered_group] += min;
            }

            // if there's no single girls, maybe at least girls married to someone they like less than i

            else if (womenPrefs_no_order[prefered_group][i] < womenPrefs_no_order[prefered_group][worst_partner_in_prefered_group]){ // PROBLEM HERE : when i ask again to the same women group !!
                rejected = false;
                b = M[worst_partner_in_prefered_group][prefered_group];
                min = Math.min(a, b);


                bachelorMen[i] -= min;
                bachelorMen[worst_partner_in_prefered_group] += min;
                M[i][prefered_group] += min;
                M[worst_partner_in_prefered_group][prefered_group] -= min;
                if (b <= a) {
                    for (int j = rankWorstPartner[prefered_group] - 1; j >= 0; j--) {
                        if (M[womenPrefs[prefered_group][j]][prefered_group] > 0) {
                            rankWorstPartner[prefered_group] = j;
                            break;
                        }
                    }
                }

                if (bachelorMen[worst_partner_in_prefered_group] >= (double) sumBachelorMen / (double) (2 * m)){
                    i = worst_partner_in_prefered_group;
                    change = false;
                }

            }



            if (bachelorMen[i] >= (double) sumBachelorMen / (double) (2 * m))
                change = false;
            if (rejected)
                counterMen[i] += 1;
            if (change)
                i = find_first_superior_to_quota(bachelorMen, (double) sumBachelorMen / (double) (2 * m));

        }


        return M;

    }
    
    private int find_first_superior_to_quota(int[] array, double quota){
        for (int i = 0; i < array.length; i++){
            if (array[i] >= quota){
                return i;
            }
        }
        return -1;

    }
    
    private int sum(int[] array){
        int res = 0;
        for (int a: array)
            res = res + a;
        return res;
    }


}
