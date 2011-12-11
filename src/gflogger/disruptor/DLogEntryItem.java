package gflogger.disruptor;

import gflogger.LogEntryItem;
import gflogger.LogLevel;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * LogEntryItem
 * 
 * @author Vladimir Dolzhenko, vladimir.dolzhenko@gmail.com
 */
public final class DLogEntryItem implements LogEntryItem {

	private final CharBuffer buffer;
	
	private String name;
	private LogLevel logLevel;
	private long timestamp;
	private String threadName;
	private String className;

	private long sequenceId;

	public DLogEntryItem(final int size) {
		this(ByteBuffer.allocateDirect(size).asCharBuffer());
	}

	public DLogEntryItem(final CharBuffer buffer) {
		this.buffer = buffer;
	}
	
	@Override
	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(final LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getThreadName() {
		return threadName;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public CharBuffer getBuffer() {
		return buffer;
	}

	public void setSequenceId(long sequenceId) {
		this.sequenceId = sequenceId;
	}
	
	public long getSequenceId() {
		return this.sequenceId;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	
	@Override
	public String toString() {
		return "[" 
			+ " pos:" + buffer.position() 
			+ " limit:" + buffer.limit() 
			+ " capacity:" + buffer.capacity() + "]";
	}

}
