package com.GUI.states;
import java.util.HashMap;
import java.util.Map;
import com.GUI.states.dashboard.Dashboard;
import com.GUI.windows.AuthenticationWindow;
import com.GUI.windows.MainWindow;
import com.util.GUI.swing.containers.Window;
import com.util.GUI.swing.state_management.State;

public class StateManager
{
	public static enum Substate {
		REGISTER(ContainerWindow.CONNECTION_WINDOW, RegisterState.class),
		LOGIN(ContainerWindow.CONNECTION_WINDOW, LoginState.class),
		DASHBOARD(ContainerWindow.MAIN_WINDOW, Dashboard.class),
		ENTRY(ContainerWindow.MAIN_WINDOW, EntryState.class);
		
		private static enum ContainerWindow {
			MAIN_WINDOW(MainWindow.class),
			CONNECTION_WINDOW(AuthenticationWindow.class);
			
			private Window window;
			
			/**
			 * @param cls - The Window's subclass
			 */
			private ContainerWindow(Class<? extends Window> cls) {
				try {
					this.window = cls.asSubclass(Window.class).getConstructor().newInstance();
					window.setVisible(false);
				}
				catch (Exception e) { e.printStackTrace(); }
			}
		}
		
		private Window window;
		private ContainerWindow containerWindow;
		private Class<? extends State> stateClass;
		
		/**
		 * @param container - ContainerWindow window that's meant to contain the state
		 * @param cls - The State's subclass
		 */
		private Substate(ContainerWindow container, Class<? extends State> cls) {
			this.containerWindow = container;
			this.window = container.window;
			this.stateClass = cls;
		}
		
		/**
		 * @param state - The state to set
		 * @param stateMap - Map of the already initialized states
		 */
		public static void setState(Substate state, Map<Class<? extends State>, State> stateMap) {
			if (state == null) return;
			
			ContainerWindow containerWindow = state.containerWindow;
			Window window = containerWindow.window;
			State instance = state.createInstance();
			stateMap.put(state.getStateClass(), instance);
			
			window.applyState(instance);
			window.setVisible(true);
		}
		
		/**
		 * Create an instance of the state.
		 * Every State instance needs a mutable window where it has the room to stretch,
		 * and cannot exist without one.
		 * A state can take place in more than one window simultaneously.
		 * 
		 * @param window - The window the will contain the state instance
		 * @return an instance of the state that fits the size of the argument window.
		 */
		private State createInstance() {
			//create instance
			try { return stateClass.asSubclass(State.class).getConstructor(Window.class).newInstance(window); }
			catch (Exception e) { System.err.println("Cannot create an instance of class " + stateClass.getName()); }
			return null;
		}
		
		/**
		 * @return the State's subclass.
		 */
		public Class<? extends State> getStateClass() { return stateClass; }
	}
	
	private static Map<Class<? extends State>, State> stateMap = new HashMap<>();
	
	/**
	 * Set a state on a window.
	 * 
	 * @param window - The window that needs to contain the state
	 * @param substate - The requested state to set
	 */
	public static void setState(Substate substate) {
		Substate.setState(substate, stateMap);
	}
	
	/**
	 * @param stateClass - The State's subclass
	 * @return the state instance if it was already initialized, or null if it didn't.
	 */
	public static State getAppliedState(Class<? extends State> stateClass) {
		return stateMap.get(stateClass);
	}
}