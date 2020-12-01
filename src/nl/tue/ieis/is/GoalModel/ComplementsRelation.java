package nl.tue.ieis.is.GoalModel;

public class ComplementsRelation extends GoalRelation {

	public ComplementsRelation(Goal g1, Goal g2, String s) {
		super(g1, g2, s);
		// TODO Auto-generated constructor stub
	}
	
	public boolean isComplements() {
		return true;
	}

	
	public void print() {
//		System.out.println(this.getFirstGoal().getName()+"   complements   " + this.getSecondGoal().getName()  + (inferred? " (inferred)" : ""));
		System.out.println(this.getFirstGoal().getName()+"   complements   " + this.getSecondGoal().getName()+ " ("+ this.consistency_type +")");

	}
}
