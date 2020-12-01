package nl.tue.ieis.is.GoalModel;

public abstract class GoalRelation {
	private Goal g1;
	private Goal g2;
	boolean inferred;
	String consistency_type;
	
	public GoalRelation(Goal g1, Goal g2, String s) {
		this.g1=g1;
		this.g2=g2;
		inferred=false;
		consistency_type=s;
	}
	
	public Goal getFirstGoal() {
		return g1;
	}
	
	public Goal getSecondGoal() {
		return g2;
	}
	
	
	public void setInferred() {
		inferred=true;
	}
	
	public boolean isInferred() {
		return inferred;
	}
	
	public boolean isComplements() {
		return false;
	}
	
	public boolean isSupports() {
		return false;
	}
	
	public boolean isExcludes() {
		return false;
	}
	
	public boolean covers(Goal g1, Goal g2) {
		return this.g1.equals(g1) && this.g2.equals(g2);
	}
	
	public void print() {
	}
}
