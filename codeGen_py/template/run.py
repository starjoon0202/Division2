import logging

import Monitor
import Analyzer
import Planner
import Executor
import KnowledgeBase

formatter = ('{:s} {:s}:{:s} [{:s}] {:s}'
             .format('%(asctime)s',
                     '%(filename)-16s',
                     '%(lineno)-5s',
                     '%(levelname)-8s',
                     '%(message)s'))

logging.basicConfig(level=logging.DEBUG, format=formatter)
logger = logging.getLogger('iCPSLog')


class main(object):
    """docstring for main"""
    def __init__(self):
        super(main, self).__init__()
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.info('initializing Self-Adaptive System')
        
        KB = KnowledgeBase.KnowledgeBase()
        KB.loadGoalModel('car-wheel.json')

        E = Executor.Executor(KB)
        P = Planner.Planner(KB, E)
        A = Analyzer.Analyzer(KB, P)
        M = Monitor.Monitor(KB)

    def run(self):
        try:
            while True:
                pass
        except KeyboardInterrupt:
            self.logger.info('force-stop SAS')

if __name__ == '__main__':
    main = main()
    main.run()