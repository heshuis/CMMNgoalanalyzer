package nl.tue.ieis.is.CMMN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;


public class CaseSchema {

	private String name;
	private String id;
	private Stage planModel;
	private Set<Sentry> sentries; // copy of sentries stored in each stage
	private Set<PlanItem> planitems;
	private Set<PlanItemDefinition> planitemdefs;

	
	public CaseSchema(String n){
		name=n;
		sentries=new HashSet<Sentry>();
		planitems=new HashSet<PlanItem>();
		planitemdefs=new HashSet<PlanItemDefinition>();
	}
	
	public void setName(String n){
		name=n;
	}
	
	public String getName(){
		return name;
	}
	
	public void SetID(String ids){
		id=ids;
	}
	
	public void generateID() {
		id="_"+UUID.randomUUID().toString().replace("-","");

	}
	
	public void setPlanModel(Stage s){
		planModel=s;
	}
	
	public Stage getPlanModel(){
		return planModel;
	}
	
	public Set<Sentry> getSentries(){
		return sentries;
	}
	
	public Set<PlanItem> getPlanItems(){
		return planitems;
	}
	

	public Set<PlanItem> getPlanItems(PlanItemDefinition pid){
		Set <PlanItem> pis=new HashSet<PlanItem>();
		for (PlanItem pi:planitems){
			if (pi.getPlanItemDefinition().equals(pid)){
				pis.add(pi);
			}
		}
		return pis;
	}

	public void addPlanItem(PlanItem pi){
		if (!planitems.contains(pi)) planitems.add(pi);
	}
	

	public void addPlanItemDefinition(PlanItemDefinition pi){
		if (!planitemdefs.contains(pi)) planitemdefs.add(pi);
	}

	public Set<PlanItemDefinition> getPlanItemDefinitions(){
		return planitemdefs;
	}
	
	public void printPlanItems(){
		System.out.println("FINAL PLANITEMS"+planitems.size());
	}
	
	public Task findTask(String name){
		for (PlanItemDefinition pi:planitemdefs){
			if ((pi instanceof Task) && pi.getName()!=null &&pi.getName().equals(name)){
				return (Task)pi;
			}
		}		
		return null;
	}
	
	
	public void addSentry(Sentry s){
		sentries.add(s);
	}
	
	public void addSentries(Set<Sentry> ss){
		sentries.addAll(ss);
	}
	
	public void removeSentries(Set<Sentry> ss){
		sentries.removeAll(ss);
	}
	
	public void removeSentry(Sentry s) {
		sentries.remove(s);
	}
	
	
	public Sentry getSentry(String id){
		for (Sentry s:sentries){
			if (s.getId().equals(id)){
				return s;
			}
		}
		return null;
	}
	
	
	public void resolveSourceRefs(){
		for (Sentry s:sentries){
			Set<onPart> es=s.getOnParts();
			for (onPart e:es){
				String sr=e.getSourceRef();
				if (sr!=null){
					PlanItem pi=findPlanItemByID(sr);
					if (pi!=null){
						PlanItemDefinition pid=pi.getPlanItemDefinition();
						e.setSource(pi,pid.getName());
						if (e.isMilestone()){
							e.setMilestoneSource();
						}
						if (e.isStage()){
							e.setStageSource();
						}
					}
					else{
						System.out.println("Source event with ID " + sr+ " not defined");
					}
				}
			}
		}
	}
	
	public boolean findPlanItemDefinition(PlanItemDefinition pid){
		return this.planitemdefs.contains(pid);
	}

	public PlanItemDefinition findPlanItemDefinitionByID(String id){
		for (PlanItemDefinition pi:planitemdefs){
			if (pi.getId()!=null &&pi.getId().equals(id)){
				return pi;
			}
		}
		return null;
	}
	
	public PlanItemDefinition findPlanItemDefinitionByName(String n){
		for (PlanItemDefinition pi:planitemdefs){
			if (pi.getName()!=null &&pi.getName().equals(n)){
				return pi;
			}
		}
		return null;
	}
	
	public boolean findPlanItem(PlanItem pi){
		return this.planitems.contains(pi);
	}
	
	public PlanItem findPlanItemByPlanItemDefName(String name){
		for (PlanItem pi:planitems){
			PlanItemDefinition pid=this.findPlanItemDefinitionByID(pi.getDefinitionRef());
			String pidname=pid.getName();
			if (pidname==null) pidname="null";

			if (pid.getName()!=null &&pid.getName().equals(name)){
				return pi;
			}
		}
		return null;
	}
	
	

	public PlanItem findPlanItemByID(String id){
		for (PlanItem pi:planitems){
			if (pi.getId()!=null &&pi.getId().equals(id)){
				return pi;
			}
		}
		return null;
	}
	
	
	

	
	public boolean owns(PlanItem ps, PlanItem pm) {
		Set<Criterion> ec=pm.getEntryRefs();
		for (Criterion c:ec) {
			Sentry s=this.getSentry(c.getSentryRef());
			if (s.hasTrigger(ps)) return true;
		}
		return false;
	}
	
	public boolean isMilestoneSentryConsistent(PlanItem pm1, PlanItem pm2) {
		Set<Criterion> ec=pm2.getEntryRefs();
		for (Criterion c:ec) {
			Sentry s=this.getSentry(c.getSentryRef());
			if (s.hasTrigger(pm1)) return true;
		}
		return false;	
	}
	
	
	
	public boolean isSentryConsistent(PlanItem pm1, PlanItem pm2) {
		for (Sentry s:sentries) {
			if (s.hasTrigger(pm1)&&s.hasTrigger(pm2)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isHierarchyConsistent(PlanItem pm1, PlanItem pm2) {
		for (PlanItem pi:planitems) {
			if (pi.getPlanItemDefinition().isStage()&&this.owns(pi, pm2)) {
				Stage s=(Stage)pi.getPlanItemDefinition();
				if (s.hasPlanItem(pm1))	return true;
			}
		}
		return false;
	}
	
	public boolean isStageMilestoneConsistent(PlanItem pm1, PlanItem pm2) {
		for (PlanItem pi:planitems) {
			if ((pi.getPlanItemDefinition().isStage()||pi.getPlanItemDefinition().isTask())&&this.owns(pi, pm2)) {
				Set<Criterion> ec=pi.getEntryRefs();
				for (Criterion c:ec) {
					Sentry se=this.getSentry(c.getSentryRef());
					if (se.hasTrigger(pm1)) return true;
				}		
			}
		}
		return false;	
	}
	
	public boolean isStageOrTaskOutputConsistent(PlanItem pm1, PlanItem pm2) {
		for (PlanItem pi:planitems) {
			if ((pi.getPlanItemDefinition().isStage()||pi.getPlanItemDefinition().isTask())&&this.owns(pi,pm1)&&this.owns(pi, pm2)) {
				return true;
			}
		}
		return false;
	}
	
	
	

	

	public int countMilestones(){
		int count=0;
		for (PlanItemDefinition pid:planitemdefs){
			if (pid.isMilestone()){
				count++;
			}
		}
		return count;
	}
	
	public int countStages(){
		int count=0;
		for (PlanItemDefinition pid:planitemdefs){
			if (pid.isStage()){
				count++;
			}
		}
		return count;
	}
	
	public int countTasks(){
		int count=0;
		for (PlanItemDefinition pid:planitemdefs){
			if (pid.isTask()){
				count++;
			}
		}
		return count;
	}
	
	
	
	
	public void printStatistics(){
		System.out.println("Case schema: " + this.getName());
		System.out.println("There are " + this.planitemdefs.size() + "  plan item definitions." );
		System.out.println("                   " + this.countStages() + "  stages." );
		System.out.println("                   " + this.countTasks() + "  tasks." );
		System.out.println("                   " + this.countMilestones() + "  milestones." );


		System.out.println("There are " + this.sentries.size() + "  sentries." );
	}

	
	
	public PlanItem findSimilarPlanItem(PlanItem piOther) {
		String otherName=piOther.getPlanItemDefinition().getName();

		for (PlanItem pi:planitems){ 
			PlanItemDefinition pid=this.findPlanItemDefinitionByID(pi.getDefinitionRef());
			if (pid.getName().equals(otherName)) return pi;
		}
		return null;
	}
	
	

	
	
	public Document printCMMN(){
		Namespace ns = Namespace.getNamespace("url");
        Namespace ns2 = Namespace.getNamespace("dc","http://www.omg.org/spec/CMMN/20151109/DC");
        Namespace ns3 = Namespace.getNamespace("di","http://www.omg.org/spec/CMMN/20151109/DI");
        Namespace ns4 = Namespace.getNamespace("cmmndi","http://www.omg.org/spec/CMMN/20151109/CMMNDI");
        Namespace ns5 = Namespace.getNamespace("cmmn","http://www.omg.org/spec/CMMN/20151109/MODEL");
        Namespace ns6 = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
        
        Document doc = new Document();
        Element def=new Element("definitions",ns5);
        def.setAttribute("id",id+"_definitions");
        def.addNamespaceDeclaration(ns2);
        def.addNamespaceDeclaration(ns3);
        def.addNamespaceDeclaration(ns4);
        def.addNamespaceDeclaration(ns5);
        def.addNamespaceDeclaration(ns6);
        doc.setRootElement(def);
        
        Element caseE=new Element("case",ns5);
        caseE.setAttribute("name",name);
        caseE.setAttribute("id",id);
        def.addContent(caseE);
        
        caseE.addContent(planModel.printElement(ns5));

        return doc;
	}
	
}
