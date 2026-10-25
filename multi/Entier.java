public class Entier extends ObjEmp {
	private final int valeur;

	public Entier(int valeur) { 
		this.valeur = valeur; 
	}

	public ObjEmp add(ObjEmp autre) { 
		return new Entier(this.valeur + ((Entier)autre).valeur); 
	}

	public ObjEmp sous(ObjEmp autre) {
	       	return new Entier(((Entier)autre).valeur - this.valeur); 
	}

	public ObjEmp mul(ObjEmp autre)  { 
		return new Entier(this.valeur * ((Entier)autre).valeur); 
	}

       	public String toString(){ 
	       	return Integer.toString(valeur);
       	}
}
