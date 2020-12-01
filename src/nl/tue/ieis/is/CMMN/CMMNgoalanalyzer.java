package nl.tue.ieis.is.CMMN;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import nl.tue.ieis.is.GoalModel.Goal;
import nl.tue.ieis.is.GoalModel.GoalNetwork;

public class CMMNgoalanalyzer {


	
	public static void analyzeMilestoneDependencies(CaseSchema cs, ArrayList<String> goalmilestoneList) {
		Vector<PlanItem> pi_milestones=new Vector();
		GoalNetwork gn=new GoalNetwork();

		for (String s:goalmilestoneList) {
			String [] ss=s.split(","); // each input line in goalmilestoneList is decomposed into a goal and a milestone
			if (ss.length!=2) {
				System.out.println("Input error on line: " + s);
				System.exit(1);
			}
			String goalStr=ss[0].trim();
			Goal g=gn.getGoal(goalStr);
			String milestoneStr=ss[1].trim();
			PlanItem pi=cs.findPlanItemByPlanItemDefName(milestoneStr);
			if (pi==null) {
				System.out.println("Warning: milestone " +milestoneStr + " is not defined." );
				continue;
			}
			else {
				pi_milestones.add(pi);
				pi.addGoal(g);
			}
		}
		for (PlanItem pm1:pi_milestones) {
			for (PlanItem pm2:pi_milestones) {
				if (pm1.equals(pm2)) continue;
				if (cs.isMilestoneSentryConsistent(pm1,pm2)){
					// for each goal corresponding to s1, for each goal corresponding to s2, 
					for (Goal g1:pm1.getGoals()) {
						for (Goal g2:pm2.getGoals()) {
							if (!g1.equals(g2)) gn.setSupports(g1, g2, "milestone-sentry consistent");
						}
					}
				}
				if (cs.isSentryConsistent(pm1,pm2)){
					for (Goal g1:pm1.getGoals()) {
						for (Goal g2:pm2.getGoals()) {
							if (!g1.equals(g2)) gn.setComplements(g1, g2, "sentry consistent");
						}
					}
				}
				if (cs.isHierarchyConsistent(pm1,pm2)){
					for (Goal g1:pm1.getGoals()) {
						for (Goal g2:pm2.getGoals()) {
							if (!g1.equals(g2)) gn.setSupports(g1, g2, "hierarchy consistent");
						}
					}
				}
				if (cs.isStageMilestoneConsistent(pm1,pm2)){
					for (Goal g1:pm1.getGoals()) {
						for (Goal g2:pm2.getGoals()) {
							if (!g1.equals(g2)) gn.setComplements(g1, g2, "stage/task-milestone consistent");
						}
					}
				}
				if (cs.isStageOrTaskOutputConsistent(pm1,pm2)){
					for (Goal g1:pm1.getGoals()) {
						for (Goal g2:pm2.getGoals()) {
							if (!g1.equals(g2)) gn.setExcludes(g1, g2, "stage/task-output-consistent");
						}
					}
				}
			}
		}
		gn.inferDependencies();
		gn.print();
		gn.analyzeInconsistencies();
	}
	
	public static void main(String args[]){
		if (args.length>0){
			try{
				
				String name=args[0].substring(0,args[0].indexOf(".cmmn"));
				CaseSchema cs=CMMNreader.doc2cmmn(CMMNreader.getDocument(args[0]),name);
				
				FileReader fr= new FileReader(args[1]);
				BufferedReader br = new BufferedReader(fr);

				ArrayList<String> milestoneList=new ArrayList<String>();

				String sCurrentLine;

				while ((sCurrentLine = br.readLine()) != null) {
			  		if (!sCurrentLine.equals("")){
			  			milestoneList.add(sCurrentLine);
			  		}
				}	
	  			analyzeMilestoneDependencies(cs,milestoneList);
	
			}
			catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else {
			System.out.println("Usage: CMMNgoalanalyzer <file.cmmn> <goal-milestone-correspondence.txt> ");
		}
	}
}
