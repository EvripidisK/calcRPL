public class Vecteur2 extends ObjEmp {
        private final int x; 
        private final int y; 

        public Vecteur2(int x , int y) { 
                this.x = x; 
                this.y = y; 
        }

        public ObjEmp add(ObjEmp autre) { 
                return new Vecteur2(this.x + ((Vecteur2)autre).x,this.y + ((Vecteur2)autre).y ); 
        }

        public ObjEmp sous(ObjEmp autre) {
                return new Vecteur2(((Vecteur2)autre).x - this.x, this.y - ((Vecteur2)autre).y ); 
        }

        public ObjEmp mul(ObjEmp autre)  { 
                return new Vecteur2(this.x * ((Vecteur2)autre).x, this.y * ((Vecteur2)autre).y ); 
        }

        public String toString(){ 
                return ("(" + this.x+ ", " + this.y + ")");
        }
}

