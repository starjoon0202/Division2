import time
import logging
import threading

file_name_ext = __file__.rsplit('\\',1)[1]
file_name = file_name_ext.rsplit('.',1)[0]
logger = logging.getLogger(file_name)

class Analyzer(object):
    """docstring for Analyzer"""
    def __init__(self, KnowledgeBase, Planner):
        super(Analyzer, self).__init__()
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.info('{:s} initializing'
                         .format(self.__class__.__name__))

        self.KB = KnowledgeBase
        self.P = Planner

        # thread
        self.t_analyzer = threading.Thread(target=self.analyzing,
                                          daemon = True)
        self.t_analyzer.start()

    def analyzing(self):
        time.sleep(1)
        while True:
            
            # Auto generated code START
            # =========================
            # G.1.1 Condition #001 - G.1 violation 
            # parent goal: G.1 Maintain optimal Distance2Leader
            # type : resource
            # input: Distance2Leader, requirement001(0.8)
            self.logger.debug('{:.2f} > {:.2f}'
                             .format(self.KB.Distance2Leader,
                                     self.KB.requirement001))
            if self.KB.Distance2Leader < self.KB.requirement001:
                self.P.alertGoalViolation(self.KB.G_1)
            # =========================
            # Auto generated code END

            time.sleep(self.KB.THREAD_SLEEP_TIME)
