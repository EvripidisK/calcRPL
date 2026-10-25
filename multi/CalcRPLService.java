import java.util.*;
import java.io.*;
import java.net.*;

public class CalcRPLService extends Thread {

	private static int dimensionPartagee = 0;
	private static Object verrou = new Object();
	private static List<PrintStream> clients = new ArrayList<PrintStream>();
	private static PileRPL pileCommune = new PileRPL();

	private Socket socket;
	private PileRPL pile;
	private boolean partage;

	private int dimension = 0;
	private int[] reserve;
	private int compteur = 0;

	private BufferedReader entree;
	private PrintStream sortie;

	public CalcRPLService(Socket socket, boolean partage) {
		this.socket = socket;
		this.partage = partage;
		if (partage == true) {
			this.pile = pileCommune;
		} else {
			this.pile = new PileRPL();
		}
	}

	private void envoyerPile() {
		String etat = pile.toString();
		if (partage == true) {
			for (int i = 0; i < clients.size(); i++) {
				clients.get(i).println(etat);
			}
		} else {
			sortie.println(etat);
		}
	}

	private int demanderDimension() throws IOException {
		if (partage == true) {
			synchronized (verrou) {
				if (dimensionPartagee != 0) {
					sortie.println("Dimension imposée par un autre client : " + dimensionPartagee);
					return dimensionPartagee;
				}
			}
		}

		int d = 0;
		while (d < 1 || d > 3) {
			sortie.print("Choisissez la dimension: 1, 2 ou 3 : ");
			String ligne = entree.readLine();
			if (ligne == null) {
				throw new IOException("client déconnecté");
			}
			try {
				d = Integer.parseInt(ligne.trim());
			} catch (NumberFormatException e) {
				d = 0;
			}
		}

		if (partage == true) {
			synchronized (verrou) {
				if (dimensionPartagee == 0) {
					dimensionPartagee = d;
				} else {
					d = dimensionPartagee;
					sortie.println("Dimension imposée par un autre client entre-temps : " + d);
				}
			}
		}

		return d;
	}

	private void creerEtEmpiler() {
		ObjEmp obj = null;
		if (dimension == 1) {
			obj = new Entier(reserve[0]);
		} else if (dimension == 2) {
			obj = new Vecteur2(reserve[0], reserve[1]);
		} else if (dimension == 3) {
			obj = new Vecteur3(reserve[0], reserve[1], reserve[2]);
		}

		if (obj != null) {
			synchronized (pile) {
				if (pile.isFull()) {
					sortie.println("Erreur : Pile pleine");
				} else {
					pile.push(obj);
					envoyerPile();
				}
			}
		}
	}

	private boolean operandeComplet(String op) {
		if (compteur != 0) {
			sortie.println("Erreur : '" + op + "' invalide, un opérande est incomplet.");
			compteur = 0;
			return false;
		}
		return true;
	}

	private void parser() {
		boolean fini = false;

		while (fini == false) {
			sortie.print("> ");
			String ligne;
			try {
				ligne = entree.readLine();
			} catch (IOException e) {
				break;
			}
			if (ligne == null) {
				break;
			}

			StringTokenizer st = new StringTokenizer(ligne);
			while (st.hasMoreTokens() && fini == false) {
				String jeton = st.nextToken();

				try {
					switch (jeton.toLowerCase()) {
						case "+":
							if (operandeComplet("+")) {
								synchronized (pile) { pile.add(); envoyerPile(); }
							}
							break;
						case "-":
							if (operandeComplet("-")) {
								synchronized (pile) { pile.sous(); envoyerPile(); }
							}
							break;
						case "*":
							if (operandeComplet("*")) {
								synchronized (pile) { pile.mul(); envoyerPile(); }
							}
							break;
						case "swap":
							if (operandeComplet("swap")) {
								synchronized (pile) { pile.swap(); envoyerPile(); }
							}
							break;
						case "clear":
							if (operandeComplet("clear")) {
								synchronized (pile) { pile.clear(); envoyerPile(); }
							}
							break;
						case "drop":
							if (operandeComplet("drop")) {
								synchronized (pile) { pile.drop(); envoyerPile(); }
							}
							break;
						case "quit":
							fini = true;
							break;
						default:
							try {
								int nb = Integer.parseInt(jeton);
								reserve[compteur] = nb;
								compteur++;
								if (compteur == dimension) {
									creerEtEmpiler();
									compteur = 0;
								}
							} catch (NumberFormatException e) {
								sortie.println("Erreur : jeton invalide '" + jeton + "'");
							}
							break;
					}
				} catch (IllegalStateException e) {
					sortie.println("Erreur : " + e.getMessage());
				}
			}

			if (fini == false && compteur != 0) {
				sortie.println("Erreur : Opérande incomplet en fin de ligne.");
				compteur = 0;
			}
		}
	}

	public void run() {
		try {
			entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			sortie = new PrintStream(socket.getOutputStream(), true);

			sortie.println("Commandes disponibles :");
			sortie.println(">> nombre(s)-> empile un nouvel objet. Pour une dimension 2 ou 3,");
			sortie.println(">> taper autant de nombres que la dimension, séparés");
			sortie.println(">> par un espace, puis Entrée (ex : 1 2 3)");
			sortie.println(">>  + - * swap drop clear -> opérations sur la pile");
			sortie.println(">> quit-> quitter");

			dimension = demanderDimension();
			reserve = new int[dimension];

			if (partage == true) {
				synchronized (pile) {
					clients.add(sortie);
					sortie.println(pile);
				}
			}

			parser();

		} catch (IOException e) {
			System.err.println("Session terminée : " + e.getMessage());
		} finally {
			if (partage == true) {
				synchronized (pile) {
					clients.remove(sortie);
				}
			}
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}
}
