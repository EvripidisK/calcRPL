import java.util.StringTokenizer;
import java.io.*;
import java.net.*;

public class CalcRPL {
	enum ModeUser { 
		LOCAL,
		REMOTE 
	}
	enum ModeLog  { 
		NONE, 
		REC, 
		REPLAY 
	}

	private PileRPL pile;
	private logRPL journal;
	private int dimension = 0;
	private int[] reserve;
	private int compteur = 0;
	private BufferedReader inputUser;
	private PrintStream outputStream;
	private PrintStream outputLog;
	private ModeUser modeUser;
	private ModeLog modeLog;
	private ServerSocket serverSocket;
	private Socket clientSocket;

	public CalcRPL(ModeUser modeUser, ModeLog modeLog) {
		this.modeUser = modeUser;
		this.modeLog  = modeLog;

		setLocal();
		if (modeUser == ModeUser.REMOTE) {
			setRemote();
		}
		try {
			if (modeLog == ModeLog.REC) {
				journal = new logRPL(true);
			} else if (modeLog == ModeLog.REPLAY) {
				journal = new logRPL(false);
			}
		} catch (IOException e) {
			System.err.println("Erreur : impossible d'ouvrir le fichier de log.");
			System.exit(1);
		}
		outputStream.println("Commandes disponibles :");
		outputStream.println(">> nombre(s)-> empile un nouvel objet. Pour une dimension 2 ou 3,");
		outputStream.println(">> taper autant de nombres que la dimension, séparés");
		outputStream.println(">> par un espace, puis Entrée (ex : 1 2 3)");
		outputStream.println(">>  + - * swap drop clear -> opérations sur la pile");
		outputStream.println(">> quit-> quitter");
		outputStream.println(">> setlocal / setremote -> changer de mode de connexion");
		this.pile      = new PileRPL();
		this.dimension = demanderDimension();
		this.reserve   = new int[this.dimension];
		mainLoop();
	}

	private String lireLigneUtilisateur() throws IOException {
		String ligne;
		if (modeLog == ModeLog.REPLAY) {
			ligne = journal.lireLigne();
			if (ligne != null) {
				outputStream.println(ligne);
			}
		} else {
			ligne = inputUser.readLine();
			if (modeLog == ModeLog.REC && ligne != null) {
				journal.enregistrer(ligne);
			}
		}
		return ligne;
	}

	private void setLocal() {
		try {
			if (clientSocket != null) { clientSocket.close(); }
			if (serverSocket != null) { serverSocket.close(); }
		} catch (IOException e) {
			// on repart en local de toute façon, rien à faire de l'erreur de fermeture
		}
		clientSocket = null;
		serverSocket = null;
		inputUser    = new BufferedReader(new InputStreamReader(System.in));
		outputStream = System.out;
	}

	private void setRemote() {
		try {
			serverSocket = new ServerSocket(5000);
			System.out.println("En attente de connexion sur le port " + serverSocket.getLocalPort() + "...");
			clientSocket = serverSocket.accept();
			inputUser    = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outputStream = new PrintStream(clientSocket.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("Erreur réseau, retour en mode local.");
			setLocal();
		}
	}

	private int demanderDimension() {
		int d = 0;
		while (d < 1 || d > 3) {
			outputStream.print("Choisissez la dimension: 1, 2 ou 3 : ");
			try {
				String ligne = lireLigneUtilisateur();
				if (ligne == null) {
					System.exit(0);
				}
				d = Integer.parseInt(ligne.trim());
			} catch (NumberFormatException e) {
				d = 0;
			} catch (IOException e) {
				System.exit(0);
			}
		}
		return d;
	}

	private void creerEtEmpilerObjet() {
		ObjEmp nouvelObjet = null;

		if (dimension == 1) {
			nouvelObjet = new Entier(reserve[0]);
		} else if (dimension == 2) {
			nouvelObjet = new Vecteur2(reserve[0], reserve[1]);
		} else if (dimension == 3) {
			nouvelObjet = new Vecteur3(reserve[0], reserve[1], reserve[2]);
		}

		if (nouvelObjet != null) {
			if (pile.isFull()) {
				outputStream.println("Erreur : Pile pleine");
			} else {
				pile.push(nouvelObjet);
			}
		}
	}

	private boolean operandeComplet(String op) {
		if (compteur != 0) {
			outputStream.println("Erreur : '" + op + "' invalide, un opérande est incomplet.");
			compteur = 0;
			return false;
		}
		return true;
	}

	private void parser() {
		boolean fini = false;

		while (!fini) {
			outputStream.print("> ");
			String ligne;
			try {
				ligne = lireLigneUtilisateur();
			} catch (IOException e) {
				break;
			}
			if (ligne == null) {
				break;
			}

			StringTokenizer st = new StringTokenizer(ligne);

			while (st.hasMoreTokens() && !fini) {
				String jeton = st.nextToken();

				try {
					switch (jeton.toLowerCase()) {
						case "+":
							if (operandeComplet("+")) { pile.add(); }
							break;

						case "-":
							if (operandeComplet("-")) { pile.sous(); }
							break;

						case "*":
							if (operandeComplet("*")) { pile.mul(); }
							break;

						case "swap":
							if (operandeComplet("swap")) { pile.swap(); }
							break;

						case "clear":
							if (operandeComplet("clear")) { pile.clear(); }
							break;

						case "drop":
							if (operandeComplet("drop")) { pile.drop(); }
							break;

						case "setlocal":
							setLocal();
							break;

						case "setremote":
							setRemote();
							break;

						case "quit":
							fini = true;
							break;

						default:
							try {
								int nombre = Integer.parseInt(jeton);
								reserve[compteur] = nombre;
								compteur++;
								if (compteur == dimension) {
									creerEtEmpilerObjet();
									compteur = 0;
								}
							} catch (NumberFormatException e) {
								outputStream.println("Erreur : jeton invalide '" + jeton + "'");
							}
							break;
					}
				} catch (IllegalStateException e) {
					outputStream.println("Erreur : " + e.getMessage());
				}
				if (!fini) {
					outputStream.println(pile);
					if (modeLog == ModeLog.REPLAY) {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
						}
					}
				}
			}

			if (!fini) {
				if (compteur != 0) {
					outputStream.println("Erreur : Opérande incomplet en fin de ligne.");
					compteur = 0;
				}
			}
		}
	}

	void mainLoop() {
		parser();
	}

	private static void usage() {
		System.err.println("Usage : java CalcRPL [-user={local,remote}] [-log={none,rec,replay}]");
		System.err.println("  -user doit venir en premier, -log en second. Les deux sont optionnels.");
	}

	public static void main(String[] args) {
		if (args.length > 2) {
			usage();
			System.exit(1);
		}

		ModeUser modeUser = ModeUser.LOCAL;
		ModeLog  modeLog  = ModeLog.NONE;

		if (args.length >= 1) {
			if (!args[0].startsWith("-user=")) {
				usage();
				System.exit(1);
			}
			String valeur = args[0].substring("-user=".length());
			if (valeur.equals("local")) {
				modeUser = ModeUser.LOCAL;
			} else if (valeur.equals("remote")) {
				modeUser = ModeUser.REMOTE;
			} else {
				usage();
				System.exit(1);
			}
		}

		if (args.length == 2) {
			if (!args[1].startsWith("-log=")) {
				usage();
				System.exit(1);
			}
			String valeur = args[1].substring("-log=".length());
			if (valeur.equals("none")) {
				modeLog = ModeLog.NONE;
			} else if (valeur.equals("rec")) {
				modeLog = ModeLog.REC;
			} else if (valeur.equals("replay")) {
				modeLog = ModeLog.REPLAY;
			} else {
				usage();
				System.exit(1);
			}
		}

		new CalcRPL(modeUser, modeLog);
	}
}
