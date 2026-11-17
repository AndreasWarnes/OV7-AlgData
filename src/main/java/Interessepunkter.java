import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Interessepunkter {

    static final int UENDELIG = Integer.MAX_VALUE;
    static final int STEDSNAVN = 1;
    static final int BENSINSTASJON = 2;
    static final int LADESTASJON = 4;
    static final int SPISESTED = 8;
    static final int DRIKKESTED = 16;
    static final int OVERNATTING = 32;

    Graf graf;
    int antallPunkter;
    int[] punktKryss;
    int[] punktKode;
    String[] punktNavn;
    int[] førstePunkt;
    int[] nestePunkt;
    int[] avstand;
    int[] funnet;
    int antallFunnet;

    Interessepunkter(Graf graf, String filnavn) throws IOException {
        this.graf = graf;
        avstand = new int[graf.antallNoder];
        les(filnavn);
    }

    void les(String filnavn) throws IOException {
        BufferedReader inn = new BufferedReader(new FileReader(filnavn));
        antallPunkter = Integer.parseInt(inn.readLine().trim());
        punktKryss = new int[antallPunkter];
        punktKode = new int[antallPunkter];
        punktNavn = new String[antallPunkter];
        nestePunkt = new int[antallPunkter];
        førstePunkt = new int[graf.antallNoder];
        Arrays.fill(førstePunkt, -1);

        for (int punkt = 0; punkt < antallPunkter; punkt++) {
            String linje = inn.readLine();
            Graf.hsplit(linje, 2);
            int kryss = Integer.parseInt(Graf.felt[0]);
            punktKryss[punkt] = kryss;
            punktKode[punkt] = Integer.parseInt(Graf.felt[1]);
            punktNavn[punkt] = linje.substring(linje.indexOf('"') + 1, linje.lastIndexOf('"'));
            nestePunkt[punkt] = førstePunkt[kryss];
            førstePunkt[kryss] = punkt;
        }
        inn.close();
    }

    void finnNærmeste(int start, int type, int antall) {
        Arrays.fill(avstand, UENDELIG);
        MinHeap kø = new MinHeap(graf.antallNoder, avstand);
        funnet = new int[antall];
        antallFunnet = 0;

        avstand[start] = 0;
        kø.leggInn(start);

        while (!kø.erTom()) {
            int node = kø.hentMinste();

            for (int punkt = førstePunkt[node]; punkt != -1; punkt = nestePunkt[punkt]) {
                if ((punktKode[punkt] & type) != 0) {
                    funnet[antallFunnet] = punkt;
                    antallFunnet++;
                    if (antallFunnet == antall) return;
                }
            }

            for (int k = graf.kantStart[node]; k < graf.kantStart[node + 1]; k++) {
                int nabo = graf.kantTil[k];
                int nyAvstand = avstand[node] + graf.kantTid[k];
                if (nyAvstand < avstand[nabo]) {
                    boolean varUoppdaget = avstand[nabo] == UENDELIG;
                    avstand[nabo] = nyAvstand;
                    if (varUoppdaget) {
                        kø.leggInn(nabo);
                    } else {
                        kø.prioritetSenket(nabo);
                    }
                }
            }
        }
    }
}