import os
import json
import logging

formatter = ('{:s} {:s}:{:s} [{:s}] {:s}'
             .format('%(asctime)s',
                     '%(filename)-16s',
                     '%(lineno)-5s',
                     '%(levelname)-8s',
                     '%(message)s'))

logging.basicConfig(level=logging.DEBUG, format=formatter)
logger = logging.getLogger('iCPSLog')

class generatePythonCode(object):
    """docstring for generatePythonCode"""
    def __init__(self, targetGoalModel):
        super(generatePythonCode, self).__init__()
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.info('{:s} initializing'
                         .format(self.__class__.__name__))

        with open(targetGoalModel) as json_file:
            self.goalmodel = json.load(json_file)

        self.nodeList = {}
        self.resources = []
        self.hardgoals = []
        self.tasks     = []
        self.monitorableParams = None

        self.analyzeGoalModel()
        
    def analyzeGoalModel(self):
        for node in self.goalmodel['goals'][:]:
            self.nodeList[node['NodeID']] = node
            if   node['NodeType'] == 'resource':
                self.resources.append(node)
            elif node['NodeType'] == 'hardgoal':
                self.hardgoals.append(node)
            elif node['NodeType'] == 'task':
                self.tasks.append(node)

    def generateConditions(self, output_dir):
        monitor_target = []

        fillInPoint = ''
        for idx, res in enumerate(self.resources):
            res_name = res['NodeName'].rsplit(' ', 1)[0]

            self.logger.debug(res_name)
            comt = (self.tab(3)
                    + '# {} Condition #{:03d} - {} violation'
                      .format(res['NodeID'],
                              idx+1,
                              res['ParentNode'])
                    + self.tab(3)
                    + '# parent goal: {} {}'
                      .format(res['ParentNode'],
                              self.nodeList[res['ParentNode']]['NodeName'])
                    + self.tab(3)
                    + '# type: resource'
                    + self.tab(3)
                    + '# input: {}'
                      .format(res_name.split()[0])
                   )
            monitor_target.append((res_name.split()[0],comt))

            code = (self.tab(3)
                    + 'if self.KB.' + res_name + ':'
                    + self.tab(4) 
                    + 'self.P.alertGoalViolation(self.KB.{})'
                      .format(res['ParentNode'].replace('.','_'))
                    + self.tab(4)
                   )
            fillInPoint += comt + code + '\n'

        with open(self.analyzer_template) as file:
            content = file.read()

        newContent = content.replace('# FILLINPOINT', fillInPoint)

        with open(output_dir + os.path.sep + 'Analyzer.py', 'w') as file:
            file.write(newContent)

        self.monitorableParams = monitor_target[:]
    
    def generateKB(self, output_dir):
        fillInPoint = ''

        for param in self.monitorableParams:
            comt = param[1].replace(self.tab(3), self.tab(2))
            code = (self.tab(2)
                    + 'self.{} = None'
                      .format(param[0])
                   )
            fillInPoint += comt + code + '\n'

        with open(self.KB_template) as file:
            content = file.read()

        newContent = content.replace('# FILLINPOINT', fillInPoint)

        with open(output_dir + os.path.sep + 'KnowledgeBase.py', 'w') as file:
            file.write(newContent)
    
    def generateMonitors(self, output_dir):
        fillInPoint1 = ''
        fillInPoint2 = ''
        for idx, task in enumerate(self.tasks):
            category = task['NodeName'].split('_')[0]
            if category == 'M':
                fnName = task['NodeName'].split('_')[1]\
                                         .split(' ')[0]

                fillInPoint1 += (
                    self.tab(3)
                    + 'self.KB.{} = self.{}()'
                      .format(fnName.replace('get',''),
                              fnName)
                    )
                comt = (self.tab(1)
                    + '# {} {}'
                      .format(task['NodeID'],
                              task['NodeName'])
                    + self.tab(1)
                    + '# type: task'
                    + self.tab(1)
                    + '# return: {}'
                      .format(fnName.replace('get',''))
                    )
                code = (self.tab(1)
                    + 'def {}(self):'
                      .format(fnName)
                    + self.tab(2)
                    + '{} = None'
                      .format(fnName.replace('get',''))
                    + self.tab(2)
                    + '# write your code here'
                    + self.tab(2)
                    + '# to {}'
                      .format(fnName)
                    + self.tab(2)
                    + '# your code START'
                    + self.tab(2)
                    + '# ====================='
                    + self.tab(2) + self.tab(2)
                    + '# ====================='
                    + self.tab(2)
                    + '# your code END'
                    + self.tab(2)
                    + 'return {}'
                      .format(fnName.replace('get',''))
                    )

                fillInPoint2 += comt + code + '\n'

        with open(self.monitor_template) as file:
            content = file.read()

        newContent = content.replace('# FILLINPOINT1', fillInPoint1)
        newContent = newContent.replace('# FILLINPOINT2', fillInPoint2)

        with open(output_dir + os.path.sep + 'Monitor.py', 'w') as file:
            file.write(newContent)

    def generateExecutor(self, output_dir):
        fillInPoint = ''
        for idx, task in enumerate(self.tasks):
            category = task['NodeName'].split('_')[0]
            if category == 'E':
                fnName = task['NodeName'].split('_')[1]\
                                         .split(' ')[0]
                parantNode = self.nodeList[task['ParentNode']]

                comt = (self.tab(1)
                    + '# {} {}'
                      .format(task['NodeID'],
                              task['NodeName'])
                    + self.tab(1)
                    + '# type: task'
                    + self.tab(1)
                    + '# domain: {}'
                      .format(task['NodeDomain'])
                    )
                code = (self.tab(1)
                    + 'def {}(self):'
                      .format(fnName)
                    + self.tab(2)
                    + '# write your code here'
                    + self.tab(2)
                    + '# to satisfy {} - {}'
                      .format(parantNode['NodeID'],
                              parantNode['NodeName'])
                    + self.tab(2)
                    + '# your code START'
                    + self.tab(2)
                    + '# ====================='
                    + self.tab(2) + self.tab(2)
                    + '# ====================='
                    + self.tab(2)
                    + '# your code END'
                    )

                fillInPoint += comt + code + '\n'

        with open(self.executor_template) as file:
            content = file.read()

        newContent = content.replace('# FILLINPOINT', fillInPoint)

        with open(output_dir + os.path.sep + 'Executor.py', 'w') as file:
            file.write(newContent)

    def generatePlanner(self, output_dir):
        with open(self.planner_template) as file:
            content = file.read()

        with open(output_dir + os.path.sep + 'Planner.py', 'w') as file:
            file.write(content)

    def generateRunner(self, output_dir):
        with open(self.runner_template) as file:
            content = file.read()

        with open(output_dir + os.path.sep + 'run.py', 'w') as file:
            file.write(content)


    def tab(self, times):
        tab = '\n'
        for __ in range(times): 
            tab += '    '
        return tab




if __name__ == '__main__':
    target = os.path.sep.join(['.','car-wheel.json'])
    output_dir = os.path.sep.join(['.','generated'])
    generator = generatePythonCode(target)
    generator.analyzer_template = os.path.sep.join(['template','Analyzer.py'])
    generator.generateConditions(output_dir)
    generator.KB_template       = os.path.sep.join(['template','KnowledgeBase.py'])
    generator.generateKB(output_dir)
    generator.monitor_template  = os.path.sep.join(['template','Monitor.py'])
    generator.generateMonitors(output_dir)
    generator.executor_template = os.path.sep.join(['template','Executor.py'])
    generator.generateExecutor(output_dir)
    generator.planner_template = os.path.sep.join(['template','Planner.py'])
    generator.generatePlanner(output_dir)
    generator.runner_template = os.path.sep.join(['template','run.py'])
    generator.generateRunner(output_dir)