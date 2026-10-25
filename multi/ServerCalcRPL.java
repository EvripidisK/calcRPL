import java.io.*;
import java.net.*;

public class ServerCalcRPL {

	public static void main(String[] args) throws IOException {

		boolean partage = true;
		String mode = "shared";

		if (args.length > 1) {
			usage();
			System.exit(1);
		}
		if (args.length == 1) {
			String arg = args[0];
			if (arg.startsWith("-stack=") == false) {
				usage();
				System.exit(1);
			}
			String valeur = arg.substring(arg.indexOf("=") + 1);
			if (valeur.equals("shared")) {
				partage = true;
				mode = "shared";
			} else if (valeur.equals("multiple")) {
				partage = false;
				mode = "multiple";
			} else {
				usage();
				System.exit(1);
			}
		}

		PileRPL pilePartagee = null;
		if (partage == true) {
			pilePartagee = new PileRPL();
		}

		ServerSocket serveur = new ServerSocket(5000);
		System.out.println("Serveur en ecoute sur le port " + serveur.getLocalPort() + " (mode " + mode + ")");

		while (true) {
			Socket client = serveur.accept();
			System.out.println("Nouvelle connexion : " + client.getInetAddress().getHostAddress());

			PileRPL pile;
			if (partage == true) {
				pile = pilePartagee;
			} else {
				pile = new PileRPL();
			}

			CalcRPLService service = new CalcRPLService(client, pile, partage);
			service.start();
		}
	}

	private static void usage() {
		System.err.println("Usage : java ServerCalcRPL [-stack={shared,multiple}]");
		System.err.println("  L'option est facultative ; par défaut la pile est partagée (shared).");
	}
}
