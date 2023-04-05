# CMMNgoalanalyzer

CMMNgoalanalyzer computes goal relations from a CMMN schema, given a correspondence relation between goals and milestones.

CMMNgoalanalyzer requires the JDOM library: jdom-1.1.3.jar

Usage: java -jar CMMNgoalanalyzer <file.cmmn> <goal-milestone-correspondence.txt>, where <file.CMMN> is a CMMN file and goal-milestone-correspondence.txt is a textfile in which each line contains a goal and a milestone, separated by a comma.

Folder examples contains sample CMMN and correspondence files.

More background can be found in the paper "Consistency Checking of Goal Models and Case Management Schemas" by Eshuis, R. & Ghose, A., 2021, Business Process Management Forum, BPM 2021, Proceedings. Polyvyanyy, A., Wynn, M. T., Van Looy, A. & Reichert, M. (eds.). Cham: Springer, p. 54-70 17 p. (Lecture Notes in Business Information Processing; vol. 427 LNBIP). https://link.springer.com/chapter/10.1007/978-3-030-85440-9_4
