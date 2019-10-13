package com.resource;

import com.common.ConcurrentSet;
import lombok.extern.slf4j.Slf4j;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jiangfei on 2019/10/13.
 */
@Slf4j
public final class ObjectCleaner {

    private static final int REFERENCE_QUEUE_POLL_TIMEOUT_MS = 5000;

    static final String CLEANER_THREAD_NAME = ObjectCleaner.class.getSimpleName() + "Thread";

    private static final Set<AutomaticCleanerReference> LIVE_SET = new ConcurrentSet<>();
    private static final ReferenceQueue<Object> REFERENCE_QUEUE = new ReferenceQueue<Object>();
    private static final AtomicBoolean CLEANER_RUNNING = new AtomicBoolean(false);
    // clean task runnable thread
    private static final Runnable CLEANER_TASK = new Runnable() {
        @Override
        public void run() {
            boolean interrupted = false;
            for (; ; ) {
                while (!LIVE_SET.isEmpty()) {
                    final AutomaticCleanerReference reference;
                    try {
                        reference = (AutomaticCleanerReference) REFERENCE_QUEUE.remove(REFERENCE_QUEUE_POLL_TIMEOUT_MS);
                    } catch (InterruptedException e) {
                        interrupted = true;
                        continue;
                    }
                    log.info("{}", reference);
                    if (reference != null) {
                        try {
                            reference.cleanup();
                        } catch (Throwable ignored) {
                        }
                        LIVE_SET.remove(reference);
                    }
                }

                CLEANER_RUNNING.set(false);

                if (LIVE_SET.isEmpty() || !CLEANER_RUNNING.compareAndSet(false, true)) {
                    break;
                }

            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    };


    public static int getLiveSetCount() {

        return LIVE_SET.size();
    }

    private ObjectCleaner() {
        // Only contains a static method.
    }

    /**
     * register
     *
     * @param object
     * @param cleanupTask
     */
    public static void register(Object object, Runnable cleanupTask) {
        AutomaticCleanerReference reference = new AutomaticCleanerReference(object, cleanupTask);
        LIVE_SET.add(reference);

        if (CLEANER_RUNNING.compareAndSet(false, true)) {
            final Thread cleanupThread = new Thread(CLEANER_TASK);
            cleanupThread.setPriority(Thread.MIN_PRIORITY);

            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    cleanupThread.setContextClassLoader(null);
                    return null;
                }
            });
            cleanupThread.setName(CLEANER_THREAD_NAME);

            cleanupThread.setDaemon(true);
            cleanupThread.start();
        }
    }

    private static final class AutomaticCleanerReference extends WeakReference<Object> {
        private final Runnable cleanupTask;

        AutomaticCleanerReference(Object referent, Runnable cleanupTask) {
            super(referent, REFERENCE_QUEUE);
            this.cleanupTask = cleanupTask;
        }

        void cleanup() {
            if (cleanupTask != null) {
                cleanupTask.run();
            }
        }

        @Override
        public Thread get() {

            return null;
        }

        @Override
        public void clear() {
            LIVE_SET.remove(this);
            super.clear();
        }
    }

}
