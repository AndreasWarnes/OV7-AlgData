import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Navigasjon {

    static final String MAPPE = "";
    static final String LANDEMERKEFIL = MAPPE + "landemerker.dat";
    static final int MAKS_PUNKTER = 1000;

    static String formaterTid(int hundredeler) {
        int sekunder = hundredeler / 100;
        int timer = sekunder / 3600;
        int minutter = (sekunder % 3600) / 60;
        int sek = sekunder % 60;
        return String.format("%d:%02d:%02d", timer, minutter, sek);
    }

    static void kjørDijkstra(Dijkstra dijkstra, int start, int mål) {
        long søkStart = System.nanoTime();
        dijkstra.søk(start, mål);
        long søkSlutt = System.nanoTime();

        System.out.println("Dijkstra fra " + start + " til " + mål);
        System.out.println("  Reisetid:      " + formaterTid(dijkstra.avstand[mål]));
        System.out.println("  Noder uttatt:  " + dijkstra.antallUttatt);
        System.out.println("  Beregningstid: " + (søkSlutt - søkStart) / 1_000_000 + " ms");
    }

    static void kjørALT(ALT alt, int start, int mål) {
        long søkStart = System.nanoTime();
        alt.søk(start, mål);
        long søkSlutt = System.nanoTime();

        System.out.println("ALT fra " + start + " til " + mål);
        System.out.println("  Reisetid:      " + formaterTid(alt.avstand[mål]));
        System.out.println("  Noder uttatt:  " + alt.antallUttatt);
        System.out.println("  Beregningstid: " + (søkSlutt - søkStart) / 1_000_000 + " ms");
    }

    static void skrivRute(Graf graf, int[] forgjenger, int mål, String filnavn) throws IOException {
        int antallPåRuta = 0;
        for (int kryss = mål; kryss != -1; kryss = forgjenger[kryss]) {
            antallPåRuta++;
        }
        int steg = Math.max(1, (antallPåRuta + MAKS_PUNKTER - 1) / MAKS_PUNKTER);

        PrintWriter ut = new PrintWriter(new FileWriter(filnavn));
        int nummer = 0;
        int antallSkrevet = 0;
        for (int kryss = mål; kryss != -1; kryss = forgjenger[kryss]) {
            if (nummer % steg == 0 || forgjenger[kryss] == -1) {
                ut.println(graf.breddegrad[kryss] + "," + graf.lengdegrad[kryss]);
                antallSkrevet++;
            }
            nummer++;
        }
        ut.close();
        System.out.println("  Rute: " + antallPåRuta + " kryss, " + antallSkrevet + " punkter skrevet til " + filnavn);
    }

    static void skrivNærmeste(Interessepunkter punkter, int start, int type, String beskrivelse, String filnavn) throws IOException {
        punkter.finnNærmeste(start, type, 4);
        System.out.println("4 nærmeste " + beskrivelse + " fra kryss " + start);

        PrintWriter ut = new PrintWriter(new FileWriter(filnavn));
        for (int i = 0; i < punkter.antallFunnet; i++) {
            int punkt = punkter.funnet[i];
            int kryss = punkter.punktKryss[punkt];
            System.out.println("  " + punkter.punktNavn[punkt] + " (kryss " + kryss + "), "
                    + formaterTid(punkter.avstand[kryss]) + ", "
                    + punkter.graf.breddegrad[kryss] + "," + punkter.graf.lengdegrad[kryss]);
            ut.println(punkter.graf.breddegrad[kryss] + "," + punkter.graf.lengdegrad[kryss]);
        }
        ut.close();
    }

    public static void main(String[] args) throws IOException {
        Graf graf = new Graf();
        long lesStart = System.nanoTime();
        graf.lesNoder(MAPPE + "noder.txt");
        graf.lesKanter(MAPPE + "kanter.txt");
        long lesSlutt = System.nanoTime();
        System.out.println("Innlesing: " + (lesSlutt - lesStart) / 1_000_000 + " ms");

        if (!new File(LANDEMERKEFIL).exists()) {
            long forStart = System.nanoTime();
            Landemerker.behandle(graf, LANDEMERKEFIL);
            long forSlutt = System.nanoTime();
            System.out.println("Forbehandling: " + (forSlutt - forStart) / 1_000_000 + " ms");
        }

        long altLesStart = System.nanoTime();
        ALT alt = new ALT(graf, LANDEMERKEFIL);
        long altLesSlutt = System.nanoTime();
        System.out.println("Innlesing landemerker: " + (altLesSlutt - altLesStart) / 1_000_000 + " ms");

        Dijkstra dijkstra = new Dijkstra(graf);

        kjørDijkstra(dijkstra, 7826348, 2948202);
        kjørALT(alt, 7826348, 2948202);

        kjørDijkstra(dijkstra, 2001238, 1987066);
        skrivRute(graf, dijkstra.forgjenger, 1987066, "rute_otilienborg_dijkstra.txt");
        kjørALT(alt, 2001238, 1987066);
        skrivRute(graf, alt.forgjenger, 1987066, "rute_otilienborg_alt.txt");

        kjørDijkstra(dijkstra, 2001238, 2763740);
        skrivRute(graf, dijkstra.forgjenger, 2763740, "rute_odense_dijkstra.txt");
        kjørALT(alt, 2001238, 2763740);
        skrivRute(graf, alt.forgjenger, 2763740, "rute_odense_alt.txt");

        Interessepunkter punkter = new Interessepunkter(graf, MAPPE + "interessepkt.txt");
        skrivNærmeste(punkter, 2763740, Interessepunkter.LADESTASJON, "ladestasjoner", "ladestasjoner_odense.txt");
        skrivNærmeste(punkter, 2001238, Interessepunkter.SPISESTED | Interessepunkter.DRIKKESTED, "spise- og drikkesteder", "spisesteder_gloshaugen.txt");
    }
}