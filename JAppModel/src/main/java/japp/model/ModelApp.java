package japp.model;

import japp.util.JAppRuntimeException;

public class ModelApp {
	
	private static ModelAppConfiguration modelAppConfiguration;
	
	public static ModelAppConfiguration getModelAppConfiguration() {
		if (modelAppConfiguration == null) {
			throw new JAppRuntimeException("ModelApp.modelAppConfiguration is not setted yet");
		}
		
		return modelAppConfiguration;
	}
	
	public static void setModelAppConfiguration(final ModelAppConfiguration modelAppConfiguration) {
		if (ModelApp.modelAppConfiguration != null) {
			throw new JAppRuntimeException("ModelApp.modelAppConfiguration is setted already");
		}
		
		ModelApp.modelAppConfiguration = modelAppConfiguration;
	}
}
