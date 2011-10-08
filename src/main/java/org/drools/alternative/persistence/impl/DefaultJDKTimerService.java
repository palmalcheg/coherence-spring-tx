package org.drools.alternative.persistence.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.runtime.CommandExecutor;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.Trigger;
import org.drools.time.impl.JDKTimerService;

/**
 * A default Scheduler implementation that uses the
 * JDK built-in ScheduledThreadPoolExecutor as the
 * scheduler and the system clock as the clock.
 * 
 */
public class DefaultJDKTimerService extends JDKTimerService {
    
	private CommandExecutor commandService;

    public void setCommandExecutor(CommandExecutor commandService) {
    	this.commandService = commandService;
    }
    
    public DefaultJDKTimerService() {
        this(1);
    }

    public DefaultJDKTimerService(int size) {
        super(size);
    }

    protected Callable<Void> createCallableJob(Job job,
									           JobContext ctx,
									           Trigger trigger,
									           JDKJobHandle handle,
									           ScheduledThreadPoolExecutor scheduler) {
    	return new JpaJDKCallableJob( job,
                ctx,
                trigger,
                handle,
                this.scheduler );
    }

	public class JpaJDKCallableJob extends JDKCallableJob {

		public JpaJDKCallableJob(Job job,
					             JobContext ctx,
					             Trigger trigger,
					             JDKJobHandle handle,
					             ScheduledThreadPoolExecutor scheduler) {
			super(job, ctx, trigger, handle, scheduler);
		}

        public Void call() throws Exception {
        	JDKCallableJobCommand command = new JDKCallableJobCommand(this);
        	commandService.execute(command);
        	return null;
        }
        
        private Void internalCall() throws Exception {
        	return super.call();
        }
    }
    
    public static class JDKCallableJobCommand implements GenericCommand<Void> {

		private static final long serialVersionUID = 4L;
		
		private JpaJDKCallableJob job;
    	
    	public JDKCallableJobCommand(JpaJDKCallableJob job) {
    		this.job = job;
    	}
    	
    	public Void execute(Context context) {
    		try {
    			return job.internalCall();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		return null;
    	}
    	
    }

}
