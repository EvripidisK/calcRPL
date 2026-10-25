public class PileRPL {
	static final int NBOBJMAX = 5;
	int nbObj = 0;
	public ObjEmp[] tab;

	PileRPL() {
		this.tab = new ObjEmp[NBOBJMAX];
	}

	boolean isEmpty() {
		return (nbObj == 0);
	}

	boolean isFull() {
		return (nbObj == NBOBJMAX);
	}

	void push(ObjEmp obj) {
		tab[nbObj] = obj;
		nbObj++;
	}

	ObjEmp pop() {
		nbObj--;
		ObjEmp obj = tab[nbObj];
		tab[nbObj] = null;
		return obj;
	}

	void add() {
		if (nbObj < 2) {
			throw new IllegalStateException("Pile avec trop peu d'éléments pour effectuer une opération");
		}
		ObjEmp o1 = pop();
		ObjEmp o2 = pop();
		push(o2.add(o1));
	}

	void sous() {
		if (nbObj < 2) {
			throw new IllegalStateException("Pile avec trop peu d'éléments pour effectuer une opération");
		}
		ObjEmp o1 = pop();
		ObjEmp o2 = pop();
		push(o2.sous(o1));
	}

	void mul() {
		if (nbObj < 2) {
			throw new IllegalStateException("Pile avec trop peu d'éléments pour effectuer une opération");
		}
		ObjEmp o1 = pop();
		ObjEmp o2 = pop();
		push(o2.mul(o1));
	}

	void swap() {
		if (nbObj < 2) {
			throw new IllegalStateException("Pile avec trop peu d'éléments pour effectuer une opération");
		}
		ObjEmp o1 = pop();
		ObjEmp o2 = pop();
		push(o1);
		push(o2);
	}

	void drop() {
		if (isEmpty()) {
			throw new IllegalStateException("Pile vide");
		}
		pop();
	}

	void clear() {
		if (isEmpty()) {
			throw new IllegalStateException("Pile déjà vide");
		}
		while (!isEmpty()) {
			pop();
		}
	}

	public String toString() {
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
