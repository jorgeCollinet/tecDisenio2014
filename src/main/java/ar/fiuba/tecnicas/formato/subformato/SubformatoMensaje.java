package ar.fiuba.tecnicas.formato.subformato;

import ar.fiuba.tecnicas.formato.ISubformato;
import ar.fiuba.tecnicas.formato.ParametrosSubformato;

/**
 * Subformato que devuelve el mensaje a logear
 */
public class SubformatoMensaje implements ISubformato
{
	@Override
	public String darFormato(ParametrosSubformato parametros)
	{
		return parametros.getMensaje();
	}
}
