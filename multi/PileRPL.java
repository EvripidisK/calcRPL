import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class PileRPL {
	static final int NBOBJMAX = 5;
	int nbObj = 0;
	public ObjEmp[] tab;
	private final List<PrintStream> abonnes = new ArrayList<>();

	PileRPL() {
		this.tab = new ObjEmp[NBOBJMAX];
	}

	boolean isEmpty() {
		return (nbObj == 0);
	}

	boolean isFull() {
		return (nbObj == NBOBJMAX);
	}

	synchronized void abonner(PrintStream out) {
		abonnes.add(out);
	}

	synchronized void desabonner(PrintStream out) {
		abonnes.remove(out);
	}

	private synchronized void notifierTous() {
		String etat = this.toString();
		for (PrintStream out : abonnes) {
			out.println(etat);
		}
	}

	private synchronized void empilerSansNotifier(ObjEmp obj) {
		if (nbObj == NBOBJMAX) {
			throw new IllegalStateException("Pile pleine");
		}
		tab[nbObj] = obj;
		nbObj++;
	}

	synchronized void push(ObjEmp obj) {
		empilerSansNotifier(obj);
		notifierTous();
	}

	synchronized ObjEmp pop() {
		nbObj--;
		ObjEmp obj = tab[nbObj];
		tab[nbObj] = null;
		return obj;
	}

	synchronized void add() {
		if (nbObj < 2) {
			throw new IllegalStateException("Pile avec trop peu d'éléments pour effectuer une opération");
		}
		ObjEmp o1 = pop();
		ObjEmp o2 = pop();
		empilerSansNotifier(o2.add(o1));
		notifierTous();
	}

	synchronized void sous() {
		if (nbObj < 2) {
			throw new IllegalStateException("Pile avec trop peu d'éléments pour effectuer une opération");
		}
		ObjEmp o1 = pop();
		ObjEmp o2 = pop();
		empilerSansNotifier(o2.sous(o1));
		notifierTous();
	}

	synchronized void mul() {
		if (nbObj < 2) {
			throw new IllegalStateException("Pile avec trop peu d'éléments pour effectuer une opération");
		}
		ObjEmp o1 = pop();
		ObjEmp o2 = pop();
		empilerSansNotifier(o2.mul(o1));
		notifierTous();
	}

	synchronized void swap() {
		if (nbObj < 2) {
			throw new IllegalStateException("Pile avec trop peu d'éléments pour effectuer une opération");
		}
		ObjEmp o1 = pop();
		ObjEmp o2 = pop();
		empilerSansNotifier(o1);
		empilerSansNotifier(o2);
		notifierTous();
	}

	synchronized void drop() {
		if (isEmpty()) {
			throw new IllegalStateException("Pile vide");
		}
		pop();
		notifierTous();
	}

	synchronized void clear() {
		if (isEmpty()) {
			throw new IllegalStateException("Pile déjà vide");
		}
		while (!isEmpty()) {
			pop();
		}
		notifierTous();
	}

	public synchronized String toString() {
		int largeur = 16;
		StringBuilder str = new StringBuilder();
		str.append("    +" + "-".repeat(largeur) + "+\n");
		for (int i = NBOBJMAX - 1; i >= 0; i--) {
			String contenu = (i < nbObj) ? tab[i].toString() : "";
			int gauche = Math.max(0, (largeur - contenu.length()) / 2);
			int droite = Math.max(0, largeur - contenu.length() - gauche);
			str.append(" " + i + "  |" + " ".repeat(gauche) + contenu + " ".repeat(droite) + "|\n");
		}
		str.append("    +" + "-".repeat(largeur) + "+\n");
		return str.toString();
	}
}
