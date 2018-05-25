import inspect
import logging


file_name_ext = __file__.rsplit('\\',1)[1]
file_name = file_name_ext.rsplit('.',1)[0]
logger = logging.getLogger(file_name)

class Executor(object):
    """docstring for Executor"""
    def __init__(self, KnowledgeBase):
        super(Executor, self).__init__()
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.info('{:s} initializing'
                         .format(self.__class__.__name__))

        self.KB = KnowledgeBase

        self.tasks = None

        mList = inspect.getmembers(self, predicate=inspect.ismethod)
        self.methods = {}
        for method in mList:
            self.methods[method[0]] = method[1]
    
    def setTasks(self, tasks):
        self.logger.info('set tasks: {}'
                         .format(tasks))
        self.tasks = tasks
        self.executing()

    def executing(self):
        for task in self.tasks:
            targetTask = self.KB.getGoal(task)
            taskName_full = targetTask['NodeName']
            taskName = taskName_full.split(' ')[0]\
                                    .split('_')[1]
            self.logger.info('execute: [{} - {}]'
                         .format(task, taskName))
            self.methods[taskName]()

    # Auto generated code START
    # =========================
    # FILLINPOINT
    # =========================
    # Auto generated code END

if __name__ == '__main__':
    Executor = Executor(None)