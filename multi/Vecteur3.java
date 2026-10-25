public class Vecteur3 extends ObjEmp {
        private final int x;
        private final int y;
        private final int z;

        public Vecteur3(int x , int y, int z) {
                this.x = x;
                this.y = y;
                this.z = z;
        }

        public ObjEmp add(ObjEmp autre) {
                return new Vecteur3(this.x + ((Vecteur3)autre).x,
				this.y + ((Vecteur3)autre).y, 
				this.z + ((Vecteur3)autre).z);
        }

        public ObjEmp sous(ObjEmp autre) {
                return new Vecteur3(this.x - ((Vecteur3)autre).x,
			       	this.y - ((Vecteur3)autre).y,
			       	this.z - ((Vecteur3)autre).z);
        }

        public ObjEmp mul(ObjEmp autre)  {
                return new Vecteur3(this.x * ((Vecteur3)autre).x,
			       	this.y * ((Vecteur3)autre).y,
			       this.z * ((Vecteur3)autre).z);
        }

        public String toString(){
                return ("(" + this.x+ ", "
			       	+ this.y + ", " 
				+ this.z + ")");
        }
}

