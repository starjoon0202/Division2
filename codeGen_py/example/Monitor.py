import time
import logging
import threading

file_name_ext = __file__.rsplit('\\',1)[1]
file_name = file_name_ext.rsplit('.',1)[0]
logger = logging.getLogger(file_name)

class Monitor(object):
    """docstring for Monitor"""
    def __init__(self, KnowledgeBase):
        super(Monitor, self).__init__()
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.info('{:s} initializing'
                         .format(self.__class__.__name__))

        self.KB = KnowledgeBase

         # thread
        self.t_monitor = threading.Thread(target=self.monitoring,
                                          daemon = True)
        self.t_monitor.start()

    def monitoring(self):
        while True:
            # Auto generated code START
            # =========================
            self.KB.Distance2Leader = self.getDistance2Leader()
            # =========================
            # Auto generated code END
            time.sleep(self.KB.THREAD_SLEEP_TIME)

    # Auto generated code START
    # =========================

    # G.1.2 getDistance2Leader
    # type   : task
    # argumt : -
    # return : double Distance2Leader
    def getDistance2Leader(self):
        dist = None
        # write your code here
        # to getDistance2Leader
        # your code START
        # =====================
        import random as r
        dist = r.random()
        self.logger.debug(dist)
        # =====================
        # your code END

        return dist

    # =========================
    # Auto generated code END