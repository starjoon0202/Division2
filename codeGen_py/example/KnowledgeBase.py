import json
import logging

file_name_ext = __file__.rsplit('\\',1)[1]
file_name = file_name_ext.rsplit('.',1)[0]
logger = logging.getLogger(file_name)



class KnowledgeBase(object):
    """docstring for KnowledgeBase"""
    def __init__(self):
        super(KnowledgeBase, self).__init__()
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.info('{:s} initializing'
                         .format(self.__class__.__name__))

        # basic parameters
        self.THREAD_SLEEP_TIME = 0.5

        # Auto generated code START
        # =========================
        # G.1.1 Condition #001 - G.1 violation 
        # parent goal: G.1 Maintain optimal Distance2Leader
        # type : resource
        # input: Distance2Leader, requirement001(0.8)
        self.Distance2Leader = None
        self.requirement001 = 0.8
        # P,G -> M
        # C -> A
        # =========================
        # Auto generated code END


    def loadGoalModel(self, goalModel_file):
        with open(goalModel_file) as json_file:    
            self.goalmodel = json.load(json_file)

        self.extractGoals()

    def extractGoals(self):
        for node in self.goalmodel['goals']:
            attrName = node['NodeID'].replace('.','_')
            setattr(self, attrName, node)

    def getGoal(self, nodeID_dot):
        goal = None
        
        nodeID_uline = nodeID_dot.replace('.','_')
        for key in self.__dict__.keys():
            if key == nodeID_uline:
                goal = self.__dict__[key]
                break

        return goal
        
