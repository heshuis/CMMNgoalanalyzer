package nl.tue.ieis.is.GoalModel;

public class Goal {
	String name;
	
	public Goal(String n) {
		name=n;
	}
	
	public String getName() {
		return name;
	}
	
	public void print() {
		System.out.println("Goal: " + name);
	}
}
