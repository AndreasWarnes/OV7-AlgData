import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class ALT {

    static final int UENDELIG = Integer.MAX_VALUE;

    Graf graf;
    int antallLandemerker;
    int[][] fraLandemerke;
    int[][] tilLandemerke;
    int[] avstand;
    int[] forgjenger;
    int[] anslag;
    int[] prioritet;
    int antallUttatt;

    ALT(Graf graf, String filnavn) throws IOException {
        this.graf = graf;
        lesLandemerker(filnavn);
        avstand = new int[graf.antallNoder];
        forgjenger = new int[graf.antallNoder];
        anslag = new int[graf.antallNoder];
        prioritet = new int[graf.antallNoder];
    }

    void lesLandemerker(String filnavn) throws IOException {
        DataInputStream inn = new DataInputStream(new BufferedInputStream(new FileInputStream(filnavn)));
        antallLandemerker = inn.readInt();
        int antallNoder = inn.readInt();
        fraLandemerke = new int[antallLandemerker][antallNoder];
        tilLandemerke = new int[antallLandemerker][antallNoder];

        for (int landemerke = 0; landemerke < antallLandemerker; landemerke++) {
            for (int kryss = 0; kryss < antallNoder; kryss++) {
                fraLandemerke[landemerke][kryss] = inn.readInt();
            }
            for (int kryss = 0; kryss < antallNoder; kryss++) {
                tilLandemerke[landemerke][kryss] = inn.readInt();
            }
        }
        inn.close();
    }

    int beregnAnslag(int kryss, int mål) {
        int best = 0;
        for (int landemerke = 0; landemerke < antallLandemerker; landemerke++) {
            int landemerkeTilMål = fraLandemerke[landemerke][mål];
            int landemerkeTilKryss = fraLandemerke[landemerke][kryss];
            if (landemerkeTilMål != UENDELIG && landemerkeTilKryss != UENDELIG) {
                int forskjell = landemerkeTilMål - landemerkeTilKryss;
                if (forskjell > best) best = forskjell;
            }

            int kryssTilLandemerke = tilLandemerke[landemerke][kryss];
            int målTilLandemerke = tilLandemerke[landemerke][mål];
            if (kryssTilLandemerke != UENDELIG && målTilLandemerke != UENDELIG) {
                int forskjell = kryssTilLandemerke - målTilLandemerke;
                if (forskjell > best) best = forskjell;
            }
        }
        return best;
    }

    void søk(int start, int mål) {
        Arrays.fill(avstand, UENDELIG);
        Arrays.fill(forgjenger, -1);
        MinHeap kø = new MinHeap(graf.antallNoder, prioritet);
        antallUttatt = 0;

        avstand[start] = 0;
        anslag[start] = beregnAnslag(start, mål);
        prioritet[start] = anslag[start];
        kø.leggInn(start);

        while (!kø.erTom()) {
            int node = kø.hentMinste();
            antallUttatt++;
            if (node == mål) break;

            for (int k = graf.kantStart[node]; k < graf.kantStart[node + 1]; k++) {
                int nabo = graf.kantTil[k];
                int nyAvstand = avstand[node] + graf.kantTid[k];
                if (nyAvstand < avstand[nabo]) {
                    boolean varUoppdaget = avstand[nabo] == UENDELIG;
                    avstand[nabo] = nyAvstand;
                    forgjenger[nabo] = node;
                    if (varUoppdaget) {
                        anslag[nabo] = beregnAnslag(nabo, mål);
                    }
                    prioritet[nabo] = nyAvstand + anslag[nabo];
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