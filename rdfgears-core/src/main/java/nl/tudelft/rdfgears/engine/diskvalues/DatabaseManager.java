package nl.tudelft.rdfgears.engine.diskvalues;

/*
 * #%L
 * RDFGears
 * %%
 * Copyright (C) 2013 WIS group at the TU Delft (http://www.wis.ewi.tudelft.nl/)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.tudelft.rdfgears.engine.Config;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;

public class DatabaseManager {
	private static Environment dbEnvironment;
	private static DatabaseConfig dbConfig;

	private static Set<Database> databases;
	private static Database complexStore;

	public static void initialize() {
		Logger parent = Logger.getLogger("com.sleepycat.je");
		parent.setLevel(Level.FINER);  // Loggers will now publish more 
		                              // detailed messages.   
		databases = new HashSet<Database>();
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setConfigParam(EnvironmentConfig.FILE_LOGGING_LEVEL, 
        "ALL");

		dbEnvironment = new Environment(new File(Config.DEFAULT_DB_PATH),
				envConfig);
		dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setTemporary(true);
		complexStore = (dbEnvironment.openDatabase(null, "complexStore",
				dbConfig));
		dbConfig.setSortedDuplicates(false);
	}

	public static Database openListDatabase() {
		Database ret = dbEnvironment.openDatabase(null, "list"
				+ System.currentTimeMillis(), dbConfig);
		databases.add(ret);
		return ret;
	}

	public static Database getComplexStore() {
		return complexStore;
	}

	public static void cleanUp() {
		complexStore.close();

		for (Database db : databases)
			if (db != null)
				db.close();

		if (dbEnvironment != null)
			dbEnvironment.close();
	}

	public static DatabaseEntry int2entry(int i) {
		DatabaseEntry key = new DatabaseEntry();
		EntryBinding<Integer> myBinding = TupleBinding
				.getPrimitiveBinding(Integer.class);
		myBinding.objectToEntry(i, key);
		return key;
	}

	public static DatabaseEntry long2entry(long i) {
		DatabaseEntry key = new DatabaseEntry();
		EntryBinding<Long> myBinding = TupleBinding
				.getPrimitiveBinding(Long.class);
		myBinding.objectToEntry(i, key);
		return key;
	}
	
	public static DatabaseEntry getComplexEntry(long id) {
		DatabaseEntry key = DatabaseManager.long2entry(id);
		DatabaseEntry data = new DatabaseEntry();
		complexStore.get(null, key, data, LockMode.DEFAULT);
		return data;
	}
}
