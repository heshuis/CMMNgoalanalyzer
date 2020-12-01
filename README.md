# CMMNgoalanalyzer

CMMNgoalanalyzer computes goal relations from a CMMN schema, given a correspondence relation between goals and milestones.

CMMNgoalanalyzer requires the JDOM library: jdom-1.1.3.jar

Usage: java -jar CMMNgoalanalyzer <file.cmmn> <goal-milestone-correspondence.txt>, where <file.CMMN> is a CMMN file and goal-milestone-correspondence.txt is a textfile in which each line contains a goal and a milestone, separated by a comma.

Folder examples contains sample CMMN and correspondence files.
