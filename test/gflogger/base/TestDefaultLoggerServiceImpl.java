package gflogger.base;

import static org.junit.Assert.*;

import java.nio.BufferOverflowException;
import java.util.HashMap;
import java.util.Map;

import gflogger.LocalLogEntry;
import gflogger.LogEntry;
import gflogger.LogFactory;
import gflogger.LogLevel;
import gflogger.Logger;
import gflogger.LoggerService;
import gflogger.LoggerServiceView;
import gflogger.appender.ConsoleAppenderFactory;

import org.junit.AfterClass;
import org.junit.Test;

/**
 * TestDefaultLoggerServiceImpl
 *
 * @author Vladimir Dolzhenko, vladimir.dolzhenko@gmail.com
 */
public class TestDefaultLoggerServiceImpl {

	@AfterClass
	public static void shutdown(){
//		LogFactory.stop();
	}

	@Test
	public void testLogLevels() throws Exception {
		final int maxMessageSize = 32;
		final ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
		factory.setLayoutPattern("%m");
		final StringBuffer buffer = new StringBuffer();
		factory.setOutputStream(buffer);
		factory.setLogLevel(LogLevel.INFO);
		final LoggerService loggerService = new DefaultLoggerServiceImpl(4, maxMessageSize, factory);

		LogFactory.init("com.db", loggerService);

		final Logger logger = LogFactory.getLog("com.db.fxpricing.Logger");

		logger.debug().append("debug").commit();
		logger.info().append("info").commit();
		logger.error().append("error").commit();

		LogFactory.stop();

		assertEquals("infoerror", buffer.toString());
	}

	@Test
	public void testLogHierarchyLevelsRootDebugOthersInfo() throws Exception {
		final int maxMessageSize = 32;
		final ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
		factory.setLayoutPattern("%m");
		final StringBuffer buffer = new StringBuffer();
		factory.setOutputStream(buffer);
		factory.setLogLevel(LogLevel.DEBUG);
		final LoggerService loggerService = new DefaultLoggerServiceImpl(4, maxMessageSize, factory);

		final Map<String, LoggerService> services = new HashMap<String, LoggerService>();
		services.put("com.db", new LoggerServiceView(loggerService, LogLevel.INFO));
		services.put(null, loggerService);
		LogFactory.init(services);

		final Logger logger = LogFactory.getLog("com.db.fxpricing.Logger");

		logger.debug().append("com.db.debug").commit();
		logger.info().append("com.db.info").commit();
		logger.error().append("com.db.error").commit();

		final Logger logger2 = LogFactory.getLog("com");

		logger2.debug().append("com.debug").commit();
		logger2.info().append("com.info").commit();
		logger2.error().append("com.error").commit();

		LogFactory.stop();

		assertEquals("com.db.infocom.db.errorcom.debugcom.infocom.error", buffer.toString());
	}

	@Test
	public void testLogHierarchyLevelsRootWarnOtherInfo() throws Exception {
		final int maxMessageSize = 32;
		final ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
		factory.setLayoutPattern("%m");
		final StringBuffer buffer = new StringBuffer();
		factory.setOutputStream(buffer);
		factory.setLogLevel(LogLevel.DEBUG);
		final LoggerService loggerService = new DefaultLoggerServiceImpl(4, maxMessageSize, factory);

		final Map<String, LoggerService> services = new HashMap<String, LoggerService>();
		services.put("com.db", new LoggerServiceView(loggerService, LogLevel.INFO));
		services.put(null, new LoggerServiceView(loggerService, LogLevel.WARN));
		LogFactory.init(services);

		final Logger logger = LogFactory.getLog("com.db.fxpricing.Logger");

		logger.debug().append("com.db.debug").commit();
		logger.info().append("com.db.info").commit();
		logger.error().append("com.db.error").commit();

		final Logger logger2 = LogFactory.getLog("org");

		logger2.debug().append("org.debug").commit();
		logger2.info().append("org.info").commit();
		logger2.error().append("org.error").commit();

		LogFactory.stop();

		assertEquals("com.db.infocom.db.errororg.error", buffer.toString());
	}

	@Test
	public void testRootAppender() throws Exception {
		final int maxMessageSize = 32;

		final ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
		factory.setLayoutPattern("%m");
		final StringBuffer buffer = new StringBuffer();
		factory.setOutputStream(buffer);
		factory.setLogLevel(LogLevel.INFO);
		final LoggerService loggerService = new DefaultLoggerServiceImpl(4, maxMessageSize, factory);

		final ConsoleAppenderFactory factory2 = new ConsoleAppenderFactory();
		factory2.setLayoutPattern("%m");
		final StringBuffer buffer2 = new StringBuffer();
		factory2.setOutputStream(buffer2);
		factory2.setLogLevel(LogLevel.INFO);
		final LoggerService rootLoggerService = new DefaultLoggerServiceImpl(4, maxMessageSize, factory2);

		final Map<String, LoggerService> services = new HashMap<String, LoggerService>();
		services.put("com.db", loggerService);
		services.put(null, rootLoggerService);
		LogFactory.init(services);

		final Logger logger = LogFactory.getLog("com.db.fxpricing.Logger");

		logger.info().append("com.db.fxpricing.Logger.info").commit();

		final Logger logger2 = LogFactory.getLog("com");
		logger2.info().append("com.info").commit();

		LogFactory.stop();

		assertEquals("com.db.fxpricing.Logger.info", buffer.toString());

		assertEquals("com.info", buffer2.toString());
	}

	@Test
    public void testAppendLatinCharsFullMessageSize() throws Exception {
		final int maxMessageSize = 20;
	    final ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
	    factory.setLayoutPattern("%m");
	    final StringBuffer buffer = new StringBuffer();
		factory.setOutputStream(buffer);
	    factory.setLogLevel(LogLevel.INFO);
		final LoggerService loggerService = new DefaultLoggerServiceImpl(4, maxMessageSize, factory);

		LogFactory.init("com.db", loggerService);

		final Logger logger = LogFactory.getLog("com.db.fxpricing.Logger");

		{
			final LogEntry info = logger.info();
			for(int i = 0; i < maxMessageSize; i++){
				// latin 'a'
				char c = (char) ('a' + i);
				info.append(c);
			}

			assertTrue(info instanceof LocalLogEntry);

			final LocalLogEntry localLogEntry = (LocalLogEntry)info;

			assertNull(localLogEntry.getError());
			// there is no enough space for one more latin 'z'
			// it doesn't matter latin, greek or chinese char - it's 2 bytes
			info.append('z');
			{
				assertNotNull(localLogEntry.getError());
				assertEquals(BufferOverflowException.class, localLogEntry.getError().getClass());
			}


			info.commit();
		}

		LogFactory.stop();

		final String string = buffer.toString();
		assertEquals(maxMessageSize, string.length());

		for(int i = 0; i < maxMessageSize; i++){
			char c = (char) ('a' + i);
			assertEquals(c, string.charAt(i));
		}
    }

	@Test
	public void testAppendCyrillicCharsFullMessageSize() throws Exception {
		final int maxMessageSize = 20;
		final ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
		factory.setLayoutPattern("%m");
		final StringBuffer buffer = new StringBuffer();
		factory.setOutputStream(buffer);
		factory.setLogLevel(LogLevel.INFO);
		final LoggerService loggerService = new DefaultLoggerServiceImpl(4, maxMessageSize, factory);

		LogFactory.init("com.db", loggerService);

		final Logger logger = LogFactory.getLog("com.db.fxpricing.Logger");

		{
			final LogEntry info = logger.info();
			for(int i = 0; i < maxMessageSize; i++){
				// Russian 'a'
				char c = (char) ('а' + i);
				info.append(c);
			}

			assertTrue(info instanceof LocalLogEntry);

			final LocalLogEntry localLogEntry = (LocalLogEntry)info;

			assertNull(localLogEntry.getError());
			// there is no enough space for one more Russian 'b'
			info.append('б');
			{
				assertNotNull(localLogEntry.getError());
				assertEquals(BufferOverflowException.class, localLogEntry.getError().getClass());
			}

			info.commit();
		}

		LogFactory.stop();

		final String string = buffer.toString();
		assertEquals(maxMessageSize, string.length());
		// unicode char is two byte char
		assertEquals(maxMessageSize << 1, string.getBytes().length);
		for(int i = 0; i < maxMessageSize; i++){
			// Russian 'a'
			char c = (char) ('а' + i);
			assertEquals(c, string.charAt(i));
		}

	}

	@Test
	public void testAppendFormattedWithWithLastMessage() throws Exception {
		final int maxMessageSize = 64;
		final ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
		factory.setLayoutPattern("%m");
		final StringBuffer buffer = new StringBuffer();
		factory.setOutputStream(buffer);
		factory.setLogLevel(LogLevel.INFO);
		final LoggerService loggerService = new DefaultLoggerServiceImpl(4, maxMessageSize, factory);

		LogFactory.init("com.db", loggerService);

		final Logger logger = LogFactory.getLog("com.db.fxpricing.Logger");
		logger.info("say hello %% %s %").endWith("world");

		LogFactory.stop();

		final String string = buffer.toString();
		assertEquals("say hello % world %", string);
	}

	@Test
	public void testAppendFormattedWithLastMessage() throws Exception {
		final int maxMessageSize = 64;
		final ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
		factory.setLayoutPattern("%m");
		final StringBuffer buffer = new StringBuffer();
		factory.setOutputStream(buffer);
		factory.setLogLevel(LogLevel.INFO);
		final LoggerService loggerService = new DefaultLoggerServiceImpl(4, maxMessageSize, factory);

		LogFactory.init("com.db", loggerService);

		final Logger logger = LogFactory.getLog("com.db.fxpricing.Logger");
		logger.info("say %s hello %% %s %").with("a").endWith("world");

		LogFactory.stop();

		final String string = buffer.toString();
		assertEquals("say a hello % world %", string);
	}

	@Test
	public void testAppendFormattedWithWrongPlaceholder() throws Exception {
		final int maxMessageSize = 64;
		final ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
		factory.setLayoutPattern("%m");
		final StringBuffer buffer = new StringBuffer();
		factory.setOutputStream(buffer);
		factory.setLogLevel(LogLevel.INFO);
		final LoggerService loggerService = new DefaultLoggerServiceImpl(4, maxMessageSize, factory);

		LogFactory.init("com.db", loggerService);

		final Logger logger = LogFactory.getLog("com.db.fxpricing.Logger");
		try {
			logger.info("say hello %d !").endWith("world");
			fail();
		} catch(IllegalArgumentException e){
			// ok
		}
	}

	@Test
	public void testAppendFormattedWithAutoCommit() throws Exception {
		final int maxMessageSize = 64;
		final ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
		factory.setLayoutPattern("%m");
		final StringBuffer buffer = new StringBuffer();
		factory.setOutputStream(buffer);
		factory.setLogLevel(LogLevel.INFO);
		final LoggerService loggerService = new DefaultLoggerServiceImpl(4, maxMessageSize, factory);

		LogFactory.init("com.db", loggerService);

		final Logger logger = LogFactory.getLog("com.db.fxpricing.Logger");
		logger.info("say hello world");

		LogFactory.stop();

		final String string = buffer.toString();
		assertEquals("say hello world", string);
	}
}
