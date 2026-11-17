import java.util.Arrays;

public class MinHeap {
    int[] heap;
    int antall;
    int[] plass;
    int[] prioritet;

    MinHeap(int antallNoder, int[] prioritet) {
        heap = new int[antallNoder];
        plass = new int[antallNoder];
        Arrays.fill(plass, -1);
        this.prioritet = prioritet;
        antall = 0;
    }

    boolean erTom() {
        return antall == 0;
    }

    void leggInn(int node) {
        heap[antall] = node;
        plass[node] = antall;
        antall++;
        flyttOpp(antall - 1);
    }

    int hentMinste() {
        int minste = heap[0];
        antall--;
        plass[minste] = -1;
        if (antall > 0) {
            heap[0] = heap[antall];
            plass[heap[0]] = 0;
            fiksHeap(0);
        }
        return minste;
    }

    void prioritetSenket(int node) {
        flyttOpp(plass[node]);
    }

    void flyttOpp(int i) {
        while (i > 0) {
            int forelder = (i - 1) / 2;
            if (prioritet[heap[i]] < prioritet[heap[forelder]]) {
                bytt(i, forelder);
                i = forelder;
            } else {
                break;
            }
        }
    }

    void fiksHeap(int i) {
        while (true) {
            int venstre = 2 * i + 1;
            int høyre = 2 * i + 2;
            int minst = i;
            if (venstre < antall && prioritet[heap[venstre]] < prioritet[heap[minst]]) minst = venstre;
            if (høyre < antall && prioritet[heap[høyre]] < prioritet[heap[minst]]) minst = høyre;
            if (minst == i) break;
            bytt(i, minst);
            i = minst;
        }
    }

    void bytt(int i, int j) {
        int midlertidig = heap[i];
        heap[i] = heap[j];
        heap[j] = midlertidig;
        plass[heap[i]] = i;
        plass[heap[j]] = j;
    }
}