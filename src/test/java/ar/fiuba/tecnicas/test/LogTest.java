package ar.fiuba.tecnicas.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ar.fiuba.tecnicas.formato.Formato;
import ar.fiuba.tecnicas.logging.Log;
import ar.fiuba.tecnicas.logging.Logger;
import ar.fiuba.tecnicas.logging.Niveles;
import ar.fiuba.tecnicas.output.IOutput;
import ar.fiuba.tecnicas.output.OutputConsole;
import ar.fiuba.tecnicas.output.OutputContainer;
import ar.fiuba.tecnicas.output.OutputFile;

public class LogTest {
	static String NOMBRE_ARCHIVO1_PRUEBA = "propertiesLog.txt";
	protected ByteArrayOutputStream outputConsola;
	protected PrintStream viejaConsola;
	

	protected Properties generateDefaultTestPropertie() {
		Properties properties = new Properties();
		properties.setProperty("Separador", Formato.separadorDefault);
		properties.setProperty("FormatoDefault", Formato.patronDefault);
		properties.setProperty(Niveles.debug.toString(),Logger.DEFAULT_NAME_LOGGER+",Output>console");
		properties.setProperty(Niveles.fatal.toString(),"pepe"+",Output>console,Output>file:log1.txt,Formato>%g%m");
		return properties;
	}

	@Before
	public void setUp() throws Exception {
		// generar aca el "propertiesLog.txt"
		// se ejecuta antes de cada uno de los tests asegurando asi
		// independencia
		Properties properties = generateDefaultTestPropertie();

		File file = new File(NOMBRE_ARCHIVO1_PRUEBA);
		file.createNewFile();

		File fileXml = new File(NOMBRE_ARCHIVO1_PRUEBA + ".xml");
		fileXml.createNewFile();

		OutputStream outXml = new FileOutputStream(fileXml);
		properties.storeToXML(outXml, "comentario");

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
		properties.store(out, NOMBRE_ARCHIVO1_PRUEBA);

		outputConsola = new ByteArrayOutputStream();
		viejaConsola = System.out;
	    PrintStream printStream = new PrintStream(outputConsola, true);
	    System.setOut(printStream);
	}

	@After
	public void tearDown() {
		File file = new File(NOMBRE_ARCHIVO1_PRUEBA);
		if (file.exists()) {
			file.delete();
		}
		File fileXml = new File(NOMBRE_ARCHIVO1_PRUEBA + ".xml");
		if (fileXml.exists()) {
			fileXml.delete();
		}
		File file2 = new File("log1.txt");
		if (file2.exists()) {
			file2.delete();
		}
		System.setOut(viejaConsola);
	}
	@Test
	public void loadConfiguration() throws Exception {
		Properties properties = generateDefaultTestPropertie();

		Log.loadConfiguration(properties);

		ArrayList<Logger> loggers = Log.getLoggers();
		assertEquals(2, loggers.size());

		Logger loggerFatal = loggers.get(1);
		assertEquals(loggerFatal.getNivel(), Niveles.fatal);
		OutputContainer outputContainerFatal = (OutputContainer) loggerFatal.getOutput();
		ArrayList<IOutput> outputsDebbug = outputContainerFatal.getOutputs();
		assertEquals(2, outputsDebbug.size());
		IOutput outputConsoleWarning = outputsDebbug.get(0);
		assertTrue(outputConsoleWarning instanceof OutputConsole);
		IOutput outputFileWarning = outputsDebbug.get(1);
		assertTrue(outputFileWarning instanceof OutputFile);
		
		Logger loggerDebug = loggers.get(0);
		assertEquals(loggerDebug.getNivel(), Niveles.debug);
		OutputContainer outputContainerDebug = (OutputContainer) loggerDebug.getOutput();
		ArrayList<IOutput> outputsInfo = outputContainerDebug.getOutputs();
		assertEquals(1, outputsInfo.size());
		IOutput outputConsoleInfo = outputsInfo.get(0);
		assertTrue(outputConsoleInfo instanceof OutputConsole);
	}
	@Test
	public void logearConfInXml() throws Exception {
		Log.loadConfigurationFromFile(NOMBRE_ARCHIVO1_PRUEBA+".xml");
		auxLogear("logearConfInXml(): mensaje", Niveles.fatal, "pepe", true);
	}

	@Test
	public void logearConfInProperties() throws Exception {
		Log.loadConfigurationFromFile(NOMBRE_ARCHIVO1_PRUEBA);
		auxLogear("logearConfInProperties(): mensaje", Niveles.fatal, "pepe", true);
	}
	
	public void auxLogear(String mensaje, Niveles nivel, String nombreLogger, boolean assertConsole) throws Exception {
		Log.log(nivel, mensaje,nombreLogger);

		File file = new File("log1.txt");
		assertTrue(file.exists());
		assertEquals(mensaje, FileHelper.getLastMessageLogged("log1.txt"));
		file.delete();
		if(assertConsole){
			assertEquals(mensaje, outputConsola.toString().trim());	
		}
	}

	@Test
	public void noLogearConfFromXmlConDistintoNivelMismoNombre() throws Exception {
		Log.loadConfigurationFromFile(NOMBRE_ARCHIVO1_PRUEBA+".xml");
		noLogearArchivoAux(Niveles.debug,"pepe");
	}

	@Test
	public void noLogearConfFromPropertiesConDistintoNivelMismoNombre() throws Exception {
		Log.loadConfigurationFromFile(NOMBRE_ARCHIVO1_PRUEBA);
		noLogearArchivoAux(Niveles.debug,"pepe");
	}
	@Test
	public void noLogearConfFromPropertiesConMismoNivelDistintoNombre() throws Exception {
		Log.loadConfigurationFromFile(NOMBRE_ARCHIVO1_PRUEBA);
		noLogearArchivoAux(Niveles.fatal,"juan el pastor");
	}
	public void noLogearArchivoAux(Niveles nivel, String nombreLogger) throws Exception {
		Log.log(nivel, "public void noLogearArchivoAux: mensaje "+nombreLogger+"|"+nivel.toString(), nombreLogger);
		
		File file = new File("log1.txt");
		assertTrue(!file.exists());
		assertTrue(file.length() == 0);
	}
}
