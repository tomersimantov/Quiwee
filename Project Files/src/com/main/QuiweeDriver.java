package com.main;
import java.sql.SQLException;
import com.GUI.states.StateManager;
import com.GUI.states.StateManager.Substate;
import com.util.data.MysqlConnection;
import com.util.data.MysqlModifier;

public class QuiweeDriver {
	public static void main(String[] args) {
		//connect to database
		String host = "sql7.freemysqlhosting.net";
		String schema = "sql7287323";
		String username = "sql7287323";
		String password = "EQjYc7Nidj";
		try {
			MysqlConnection connection = new MysqlConnection(host, schema, username, password);
			MysqlModifier.connect(connection);
		}
		catch (SQLException e) {
			System.err.println("Unable to establish a connection to the data base.");
			return;
		}
		
		StateManager.setState(Substate.ENTRY);
	}
}