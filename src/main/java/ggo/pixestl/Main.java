package ggo.pixestl;

import ggo.pixestl.arg.CommandArgsParser;
import ggo.pixestl.generator.GenInstruction;
import ggo.pixestl.generator.PlateGenerator;

public class Main {
	
	public static void main(String[] args) throws Exception
	{		
		GenInstruction genInstruction = CommandArgsParser.argsToGenInstruction(args);         
		new PlateGenerator().process(genInstruction);
	}	
}
