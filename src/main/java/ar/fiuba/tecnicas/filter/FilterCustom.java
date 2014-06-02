package ar.fiuba.tecnicas.filter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Clase encargada de filtrar por nivel
 * @author Grupo3
 * 
 */
public class FilterCustom implements IFilter{
	protected String className;
	
	public static FilterCustom generateFilterCustom (String className) throws Exception{
		for(Class<?> interfaces: Class.forName(className).getInterfaces()){
			if(interfaces.getClass().isInstance(IFilter.class)){
				System.out.print(className+" implementa interfaz de IFilter\n");
				return new FilterCustom(className);
			}
		}
		throw new Exception("nombre de clase: "+className+" no implementa interfaz IFilter\n");
	}
	
	protected FilterCustom(String className) {
		this.className = className;
	}
	
	@Override
	public boolean hasToLog(FilterData filterData) {
		try {
			try {
				Constructor<?> constructor = Class.forName(this.className).getConstructor();
				try {
					IFilter filterCustom = (IFilter) constructor.newInstance();
					return filterCustom.hasToLog(filterData);					
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
