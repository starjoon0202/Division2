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
            # FILLINPOINT1
            # =========================
            # Auto generated code END
            time.sleep(self.KB.THREAD_SLEEP_TIME)

    # Auto generated code START
    # =========================
    # FILLINPOINT2
    # =========================
    # Auto generated code END