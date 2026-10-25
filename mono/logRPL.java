import java.io.*;

public class logRPL {
	private static final String fichier_log = "log.txt";
	private PrintStream ecriture;
	private BufferedReader replay;

	logRPL(boolean enEcriture) throws IOException {
		if (enEcriture) {
			ecriture = new PrintStream(new FileOutputStream(fichier_log), true);
		} else {
			replay = new BufferedReader(new FileReader(fichier_log));
		}
	}

	void enregistrer(String ligne) {
		if (ecriture != null) {
			ecriture.println(ligne);
		}
	}

	String lireLigne() throws IOException {
		return (replay != null) ? replay.readLine() : null;
	}

	void fermer() throws IOException {
		if (ecriture != null) { ecriture.close(); }
		if (replay  != null) { replay.close(); }
	}
}
