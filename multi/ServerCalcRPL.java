import java.io.*;
import java.net.*;

public class ServerCalcRPL {

	public static void main(String[] args) throws IOException {

		boolean partage = true;
		String mode = "shared";

		if (args.length > 1) {
			System.err.println("Usage : java ServerCalcRPL [-stack={shared,multiple}]");
			System.err.println("  L'option est facultative ; par défaut la pile est partagée (shared).");
			System.exit(1);
		}
		if (args.length == 1) {
			String arg = args[0];
			if (arg.startsWith("-stack=") == false) {
				System.err.println("Usage : java ServerCalcRPL [-stack={shared,multiple}]");
				System.err.println("  L'option est facultative ; par défaut la pile est partagée (shared).");
				System.exit(1);
			}
			String valeur = arg.substring(arg.indexOf("=") + 1);
			switch (valeur) {
				case "shared":
					partage = true;
					mode = "shared";
					break;
				case "multiple":
					partage = false;
					mode = "multiple";
					break;
				default:
					System.err.println("Usage : java ServerCalcRPL [-stack={shared,multiple}]");
					System.err.println("  L'option est facultative ; par défaut la pile est partagée (shared).");
					System.exit(1);
					break;
			}
		}

		ServerSocket serveur = new ServerSocket(5000);
		System.out.println("Serveur en ecoute sur le port " + serveur.getLocalPort() + " (mode " + mode + ")");

		while (true) {
			Socket client = serveur.accept();
			System.out.println("Nouvelle connexion : " + client.getInetAddress().getHostAddress());

			CalcRPLService service = new CalcRPLService(client, partage);
			service.start();
		}
	}
}
