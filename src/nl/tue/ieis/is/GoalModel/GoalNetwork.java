package nl.tue.ieis.is.GoalModel;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class GoalNetwork {
	
	private Vector<Goal> goals;
	private Set<GoalRelation> grs;
	
	public GoalNetwork() {
		goals=new Vector<Goal>();
		grs=new HashSet <GoalRelation>();
	}
	
	public Goal getGoal(String n) {
		for (Goal goal:goals) {
			if (goal.getName().equals(n)) {
				return goal;
			}
		}
		// no goal with name n found, so create a new goal
		Goal g=new Goal(n);
		goals.add(g);
		return g;
	}

	public void addGoalRelation(GoalRelation gr) {
		grs.add(gr);
	}
	
	public void inferDependencies(){
		HashSet<ComplementsRelation> complements=new HashSet<ComplementsRelation>();
		HashSet<ExcludesRelation> excludes=new HashSet<ExcludesRelation>();
		HashSet<SupportsRelation> supports=new HashSet<SupportsRelation>();

		// step 1
		for (GoalRelation gr:grs) {
			if (gr.isComplements()) {
				ComplementsRelation cr=new ComplementsRelation(gr.getSecondGoal(),gr.getFirstGoal(), "inferred");
				complements.add(cr);
				cr.setInferred();
			}
			if (gr.isExcludes()) {
				ExcludesRelation er=new ExcludesRelation(gr.getSecondGoal(),gr.getFirstGoal(), "inferred");
				excludes.add(er);
				er.setInferred();
			}
		}
		for (ComplementsRelation cr:complements) {
			this.addComplements(cr);
		}
		complements.clear();
		for (ExcludesRelation er:excludes) {
			this.addExcludes(er);
		}		
		excludes.clear();
		
		
		// step 2
		for (GoalRelation gr1:grs) {
			for (GoalRelation gr2:grs) {
				if (gr1.getSecondGoal().equals(gr2.getFirstGoal())) {
					if (gr1.getFirstGoal().equals(gr2.getSecondGoal())) continue; // relations are irreflexive
					if (gr1.isSupports() && gr2.isSupports()) {
						SupportsRelation sr=new SupportsRelation(gr1.getFirstGoal(), gr2.getSecondGoal(), "inferred");
						sr.setInferred();
						supports.add(sr);
					}
				}					
			}
		}
		for (ComplementsRelation cr:complements) {
			this.addComplements(cr);
		}
		complements.clear();
		for (ExcludesRelation er:excludes) {
			this.addExcludes(er);
		}		
		excludes.clear();
		for (SupportsRelation sr:supports) {
			this.addSupports(sr);
		}		
		supports.clear();

		for (GoalRelation gr1:grs) {
			for (GoalRelation gr2:grs) {
				if (gr1.getSecondGoal().equals(gr2.getFirstGoal())) {
					if (gr1.isSupports() && gr2.isComplements()) {
						ComplementsRelation cr=new ComplementsRelation(gr1.getFirstGoal(),gr2.getSecondGoal(),"inferred");
						cr.setInferred();
						complements.add(cr);
					}
					if (gr1.isSupports() && gr2.isExcludes()) {
						ExcludesRelation er=new ExcludesRelation(gr1.getFirstGoal(),gr2.getSecondGoal(), "inferred");
						er.setInferred();
						excludes.add(er);
					}
				}					
			}
		}
		for (ComplementsRelation cr:complements) {
			this.addComplements(cr);
		}
		complements.clear();
		for (ExcludesRelation er:excludes) {
			this.addExcludes(er);
		}		
		excludes.clear();
		
		// repeat first two steps
		//step 1 repeated
		for (GoalRelation gr:grs) {
			if (gr.isComplements()) {
				ComplementsRelation cr=new ComplementsRelation(gr.getSecondGoal(),gr.getFirstGoal(), "inferred");
				complements.add(cr);
				cr.setInferred();
			}
			if (gr.isExcludes()) {
				ExcludesRelation er=new ExcludesRelation(gr.getSecondGoal(),gr.getFirstGoal(), "inferred");
				excludes.add(er);
				er.setInferred();
			}
		}
		for (ComplementsRelation cr:complements) {
			this.addComplements(cr);
		}
		complements.clear();
		for (ExcludesRelation er:excludes) {
			this.addExcludes(er);
		}		
		excludes.clear();
		
		//step 2 repeated
		for (GoalRelation gr1:grs) {
			for (GoalRelation gr2:grs) {
				if (gr1.getSecondGoal().equals(gr2.getFirstGoal())) {
					if (gr1.getFirstGoal().equals(gr2.getSecondGoal())) continue; // relations are irreflexive
					if (gr1.isSupports() && gr2.isSupports()) {
						SupportsRelation sr=new SupportsRelation(gr1.getFirstGoal(), gr2.getSecondGoal(), "inferred");
						sr.setInferred();
						supports.add(sr);
					}
				}					
			}
		}
		for (ComplementsRelation cr:complements) {
			this.addComplements(cr);
		}
		complements.clear();
		for (ExcludesRelation er:excludes) {
			this.addExcludes(er);
		}		
		excludes.clear();
		for (SupportsRelation sr:supports) {
			this.addSupports(sr);
		}		
		supports.clear();
	}
	
	public void analyzeInconsistencies() {
		for (Goal g1:goals) {
			for (Goal g2:goals) {
				if (this.isComplements(g1, g2) && this.isExcludes(g1, g2)) {
					System.out.println("Inconsistency: goals " +g1.getName() + " and " + g2.getName() + " are both complementary and exclusive!");
				}
				if (this.isSupports(g1, g2) && this.isExcludes(g1, g2)) {
					System.out.println("Inconsistency: goals "+ g1.getName() + " and " + g2.getName() + " are exclusive, yet " +g1.getName() + " supports " + g2.getName()+ "!");
				}
				if (this.isSupports(g2, g1) && this.isExcludes(g1, g2)) {
					System.out.println("Inconsistency: goals "+ g1.getName() + " and " + g2.getName() + " are exclusive, yet " +g2.getName() + " supports " + g1.getName()+ "!");
				}
				if (this.isSupports(g1, g2) && this.isComplements(g1, g2)) {	
					System.out.println("Inconsistency: goals " + g1.getName() + " and " + g2.getName() + " are complementary, yet " +g1.getName() + " supports " + g2.getName()+ "!");
				}
				if (this.isSupports(g2, g1) && this.isComplements(g1, g2)) {	
					System.out.println("Inconsistency: goals " + g1.getName() + " and " + g2.getName() + " are complementary, yet " +g2.getName() + " supports " + g1.getName()+ "!");
				}
			}
		}
	}
	
	
	public boolean isSupports(Goal g1, Goal g2) {
		for (GoalRelation gr:grs) {
			if (gr.covers(g1, g2) && gr.isSupports()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isComplements(Goal g1, Goal g2) {
		for (GoalRelation gr:grs) {
			if (gr.covers(g1, g2) && gr.isComplements()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isExcludes(Goal g1, Goal g2) {
		for (GoalRelation gr:grs) {
			if (gr.covers(g1, g2) && gr.isExcludes()) {
				return true;
			}
		}
		return false;
	}
	
	public void setComplements(Goal g1, Goal g2, String s) {	
		if (!isComplements(g1,g2)) {
			ComplementsRelation gr_new=new ComplementsRelation(g1,g2,s);
			this.addGoalRelation(gr_new);
		}
	}
	
	public void addComplements(ComplementsRelation cr) {
		if (!isComplements(cr.getFirstGoal(),cr.getSecondGoal())) {
			this.addGoalRelation(cr);
		}
	}
	
	
	public void setExcludes(Goal g1, Goal g2, String s) {	
		if (!isExcludes(g1,g2)) {
			System.out.println("Setting excludes " + g1 + "  " + g2 );
			g1.print();
			g2.print();
			ExcludesRelation gr_new=new ExcludesRelation(g1,g2,s);
			this.addGoalRelation(gr_new);
		}
	}
	
	public void addExcludes(ExcludesRelation er) {
		if (!isExcludes(er.getFirstGoal(),er.getSecondGoal())) {
			this.addGoalRelation(er);
		}
	}
	
	
	public void setSupports(Goal g1, Goal g2, String s) {	
		if (!isSupports(g1,g2)) {
			SupportsRelation gr_new=new SupportsRelation(g1,g2,s);
			this.addGoalRelation(gr_new);
		}
	}
	
	public void addSupports(SupportsRelation sr) {
		if (!isSupports(sr.getFirstGoal(),sr.getSecondGoal())) {
			this.addGoalRelation(sr);
		}
	}
	
	
	public void print() {
		System.out.println("There are " + grs.size() + " goal relations:");
		for (Goal g:goals) {
			for (GoalRelation gr:grs) {
				if (gr.getFirstGoal().equals(g)) gr.print();
			}		
		}
	}
}
