package chasqui.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chasqui.exceptions.TokenInexistenteException;

public class TokenGenerator {
	
	protected static SecureRandom random = new SecureRandom();
	protected Map<String,Integer> tokensVendedor = new HashMap<String,Integer>();
	
	public String generarTokenParaVendedor(Integer idVendedor){
        long longToken = Math.abs( random.nextLong() );
        String token = Long.toString( longToken, 16 );
        List<String> keys = new ArrayList<>();
        keys.addAll(tokensVendedor.keySet());
        for(int i = 0; i<tokensVendedor.size();i++){
        	String vkey = keys.get(i);
        	Integer idTokenVendedor = tokensVendedor.get(vkey);
        	if(idTokenVendedor == idVendedor){
        		tokensVendedor.remove(vkey);
        	}
        }
        tokensVendedor.put(token, idVendedor);   
        return token;
	}
	
	public Integer getIdDeVendedorConToken(String token) throws TokenInexistenteException{
		Integer response = tokensVendedor.get(token);
		if(response == null){
			throw new TokenInexistenteException("Token invalido");
		}
		return tokensVendedor.get(token);
	}

}
