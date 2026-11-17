import java.util.Arrays;

public class Dijkstra {
    static final int UENDELIG = Integer.MAX_VALUE;

    Graf graf;
    int[] avstand;
    int[] forgjenger;
    int antallUttatt;

    Dijkstra(Graf graf) {
        this.graf = graf;
        avstand = new int[graf.antallNoder];
        forgjenger = new int[graf.antallNoder];
    }

    void søk(int start, int mål) {
        Arrays.fill(avstand, UENDELIG);
        Arrays.fill(forgjenger, -1);
        MinHeap kø = new MinHeap(graf.antallNoder, avstand);
        antallUttatt = 0;

        avstand[start] = 0;
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
                        kø.leggInn(nabo);
                    } else {
                        kø.prioritetSenket(nabo);
                    }
                }
            }
        }
    }
}