import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Graf {
    int antallNoder;
    int antallKanter;
    double[] breddegrad;
    double[] lengdegrad;

    int[] kantStart;
    int[] kantTil;
    int[] kantTid;

    int[] omvendtStart;
    int[] omvendtTil;
    int[] omvendtTid;

    static String[] felt = new String[10];

    static void hsplit(String linje, int antall) {
        int j = 0;
        int lengde = linje.length();
        for (int i = 0; i < antall; ++i) {
            while (linje.charAt(j) <= ' ') ++j;
            int ordstart = j;
            while (j < lengde && linje.charAt(j) > ' ') ++j;
            felt[i] = linje.substring(ordstart, j);
        }
    }

    void lesNoder(String filnavn) throws IOException {
        BufferedReader inn = new BufferedReader(new FileReader(filnavn));
        antallNoder = Integer.parseInt(inn.readLine().trim());
        breddegrad = new double[antallNoder];
        lengdegrad = new double[antallNoder];

        for (int i = 0; i < antallNoder; i++) {
            hsplit(inn.readLine(), 3);
            int node = Integer.parseInt(felt[0]);
            breddegrad[node] = Double.parseDouble(felt[1]);
            lengdegrad[node] = Double.parseDouble(felt[2]);
        }
        inn.close();
    }

    void lesKanter(String filnavn) throws IOException {
        BufferedReader inn = new BufferedReader(new FileReader(filnavn));
        antallKanter = Integer.parseInt(inn.readLine().trim());
        int[] fra = new int[antallKanter];
        int[] til = new int[antallKanter];
        int[] tid = new int[antallKanter];

        for (int i = 0; i < antallKanter; i++) {
            hsplit(inn.readLine(), 3);
            fra[i] = Integer.parseInt(felt[0]);
            til[i] = Integer.parseInt(felt[1]);
            tid[i] = Integer.parseInt(felt[2]);
        }
        inn.close();

        kantStart = new int[antallNoder + 1];
        kantTil = new int[antallKanter];
        kantTid = new int[antallKanter];
        byggNaboliste(fra, til, tid, kantStart, kantTil, kantTid);

        omvendtStart = new int[antallNoder + 1];
        omvendtTil = new int[antallKanter];
        omvendtTid = new int[antallKanter];
        byggNaboliste(til, fra, tid, omvendtStart, omvendtTil, omvendtTid);
    }

    void byggNaboliste(int[] fra, int[] til, int[] tid, int[] start, int[] ut, int[] utTid) {
        for (int i = 0; i < antallKanter; i++) {
            start[fra[i] + 1]++;
        }
        for (int node = 0; node < antallNoder; node++) {
            start[node + 1] += start[node];
        }
        int[] nesteLedige = new int[antallNoder];
        for (int node = 0; node < antallNoder; node++) {
            nesteLedige[node] = start[node];
        }
        for (int i = 0; i < antallKanter; i++) {
            int plass = nesteLedige[fra[i]];
            ut[plass] = til[i];
            utTid[plass] = tid[i];
            nesteLedige[fra[i]]++;
        }
    }
}