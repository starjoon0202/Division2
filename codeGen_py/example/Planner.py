import time
import logging

file_name_ext = __file__.rsplit('\\',1)[1]
file_name = file_name_ext.rsplit('.',1)[0]
logger = logging.getLogger(file_name)

class Planner(object):
    """docstring for Planner"""
    def __init__(self, KnowledgeBase, Executor):
        super(Planner, self).__init__()
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.info('{:s} initializing'
                         .format(self.__class__.__name__))
        
        self.KB = KnowledgeBase
        self.E = Executor

        self.violatedGoal = None

    def alertGoalViolation(self, violatedGoal):
        self.violatedGoal = violatedGoal
        self.logger.info('[{} - {}] has been violated!!'
                         .format(violatedGoal['NodeID'],
                                 violatedGoal['NodeName']))
        self.planning()

    def planning(self):
        self.logger.info('make plan for violated goal [{} - {}]'
                         .format(self.violatedGoal['NodeID'],
                                 self.violatedGoal['NodeName']))
        strategies = self.getStrategies()

        # DO: select strategy and task
        tasks = self.selectTasks(strategies)
        self.E.setTasks(tasks)
        self.violatedGoal = None

    def getStrategies(self):
        strategies = []
        self.logger.info('searching strategies...')

        childGoal = self.violatedGoal['RELATION']['SubNodes'][:]
        while len(childGoal) != 0:
            targetID = childGoal.pop(0)
            targetGoal = self.KB.getGoal(targetID)
            if   targetGoal['NodeType'] == 'hardgoal':
                strategies.append(targetID)
            childGoal += targetGoal['RELATION']['SubNodes']

        self.logger.debug('strategies: {}'.format(strategies))

        return strategies

    def selectTasks(self, strategies):
        tasks = []
        for strategyID in strategies:
            targetGoal = self.KB.getGoal(strategyID)
            if   targetGoal['RELATION']['RelType'] == 'AND':
                tasks += targetGoal['RELATION']['SubNodes']
            elif targetGoal['RELATION']['RelType'] == 'OR':
                rankedTasks = self.evaluateTasks(
                                targetGoal['RELATION']['SubNodes'])
                tasks += [rankedTasks[0]]

        return tasks


    def evaluateTasks(self, subNodes):
        for node in subNodes:
            # DO: evaluation process instead below debug code
            # debug code START
            # ======================
            nodeDict = self.KB.getGoal(node)
            if   nodeDict['NodeID'] == 'G.1.3.1':
                nodeDict['Evaluation'] = 1.
            elif nodeDict['NodeID'] == 'G.1.3.2':
                nodeDict['Evaluation'] = 0.
            # ======================
            # debug code END

        rankedTasks = sorted(subNodes, reverse=True,
                             key=lambda x: self.KB.getGoal(x)['Evaluation'])
        self.logger.debug(rankedTasks)
        return rankedTasks


if __name__ == '__main__':
    Planner = Planner(None)