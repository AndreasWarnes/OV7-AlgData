import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Landemerker {

    static final int[] LANDEMERKER = {2531818, 2842056, 7021334, 1162204};

    static void behandle(Graf graf, String filnavn) throws IOException {
        Graf omvendtGraf = new Graf();
        omvendtGraf.antallNoder = graf.antallNoder;
        omvendtGraf.kantStart = graf.omvendtStart;
        omvendtGraf.kantTil = graf.omvendtTil;
        omvendtGraf.kantTid = graf.omvendtTid;

        Dijkstra framover = new Dijkstra(graf);
        Dijkstra bakover = new Dijkstra(omvendtGraf);

        DataOutputStream ut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filnavn)));
        ut.writeInt(LANDEMERKER.length);
        ut.writeInt(graf.antallNoder);

        for (int landemerke : LANDEMERKER) {
            framover.søk(landemerke, -1);
            bakover.søk(landemerke, -1);
            System.out.println("Landemerke " + landemerke + ": nådde " + framover.antallUttatt
                    + " kryss framover og " + bakover.antallUttatt + " bakover");

            for (int kryss = 0; kryss < graf.antallNoder; kryss++) {
                ut.writeInt(framover.avstand[kryss]);
            }
            for (int kryss = 0; kryss < graf.antallNoder; kryss++) {
                ut.writeInt(bakover.avstand[kryss]);
            }
        }
        ut.close();
    }
}