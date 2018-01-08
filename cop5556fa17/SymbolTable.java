package cop5556fa17;

import java.util.HashMap;

import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.Declaration;
public class SymbolTable {

	HashMap<String,Declaration> Sym=new HashMap<String,Declaration>();
	public Type lookupType(String name){
		System.out.println("-------"+name);
		if(Sym.containsKey(name))
			return  Sym.get(name).Type;
		else  
			return null;  
		
	}
	public void insert(String name,Declaration dec) {
		System.out.println("-------"+name+"---insert---"+dec.getClass());
		Sym.put(name, dec);
	}
	public Declaration lookupDec(String name){
		System.out.println("-------"+name+"pp");
		if(Sym.containsKey(name))
			return Sym.get(name);
		else
		    return null;
		
	}
}
