package com.main;
import java.sql.SQLException;
import java.util.List;
import com.data.MysqlPuller;
import com.util.IO.StringVerifier;
import com.util.data.MysqlModifier;

public class User
{
	private static String email, name, password;

	public static boolean login(String mail, String pass) {
		String query = "SELECT * FROM users;";
		List<String> emails, passes;
		
		try {
			emails = MysqlModifier.readAllVARCHAR(query, "email");
			passes = MysqlModifier.readAllVARCHAR(query, "password");
			
			for (int i = 0; i < emails.size(); i++) {
				if (emails.get(i).equals(mail) && passes.get(i).equals(pass)) {
					email = mail;
					password = pass;
					MysqlPuller.init(email);
					return true;
				}
			}
		}
		catch (SQLException e) {}
		
		//failed
		logout();
		return false;
	}

	public static void logout() {
		email = null;
		password = null;
	}
	
	public static boolean changePassword(String newPass) {
		if (!StringVerifier.verifyPassword(newPass, 8, 20)) return false;
		else {
			String query = "UPDATE users SET password = '" + newPass + "'";
			try {
				MysqlModifier.write(query);
				return true;
			}
			catch (SQLException e) { return false; }
		}
	}
	
	public static boolean isLogged() { return email != null; }
	public static String getKey() { return email; }
	public static String getPass() { return password; }
	public static String getName() { return name; }
}