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
            # FILLINPOINT
            # =========================
            # Auto generated code END

            time.sleep(self.KB.THREAD_SLEEP_TIME)
