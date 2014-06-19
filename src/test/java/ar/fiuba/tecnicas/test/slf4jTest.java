package ar.fiuba.tecnicas.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import ar.fiuba.tecnicas.format.Format;
import ar.fiuba.tecnicas.format.FormatType;
import ar.fiuba.tecnicas.logging.Level;
import ar.fiuba.tecnicas.logging.Log;
import ar.fiuba.tecnicas.logging.LoggerConfig;
import ar.fiuba.tecnicas.output.OutputType;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.impl.StaticLoggerBinder;

public class slf4jTest 
{
	private String nombreLogger = "testLogger";
	private ILoggerFactory factory;
	private ByteArrayOutputStream outputConsola;
	private PrintStream viejaConsola = System.out;
	
	@Before
    public void setUp() throws Exception
    {		
		LoggerConfig loggerConfig = new LoggerConfig();
		loggerConfig.setName(nombreLogger);
		loggerConfig.setLevel(Level.debug);
		loggerConfig.setFormat("%m", FormatType.Format);
		loggerConfig.setSeparator(Format.defaultSeparator);
		loggerConfig.addOutput(null, OutputType.console);
		
		ArrayList<LoggerConfig> loggerConfigList = new ArrayList<>();
		loggerConfigList.add(loggerConfig);
		
		Log.loadConfiguration(loggerConfigList);
		factory = new StaticLoggerBinder().getLoggerFactory();
		
		outputConsola = new ByteArrayOutputStream();
	    PrintStream printStream = new PrintStream(outputConsola, true);
	    System.setOut(printStream);
	}
	
	@After
	public void tearDown()
	{
		 System.setOut(viejaConsola);
	}
	
	
	@Test
	public void log() 
	{
		Logger logger = factory.getLogger(nombreLogger);
		logger.error("Test");
		assertEquals("Test", outputConsola.toString().trim());	
	}
	
	@Test
	public void noLog() 
	{
		Logger logger = factory.getLogger(nombreLogger);
		logger.trace("Test");
		assertEquals("", outputConsola.toString().trim());	
	}
	
	@Test
	public void checkNivel()
	{
		Logger logger = factory.getLogger(nombreLogger);
		assertTrue(logger.isDebugEnabled());
		assertFalse(logger.isTraceEnabled());
	}

}
