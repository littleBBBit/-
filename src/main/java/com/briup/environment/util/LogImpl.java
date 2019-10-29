package com.briup.environment.util;

import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.RootLogger;

public class LogImpl implements Log {

	
	// log4j�е������
	Logger logger;
	//logger����·��
	String log;
	
	
	@Override
	public void init(Properties properties) throws Exception {
		
		//<log_properties>src/main/java/com/briup/environment/log4j.properties</log_properties>
		log = properties.getProperty("log_properties");
		
		
/*		// ����log4j�����ļ���·��
		String configPath = "src/main/java/com/briup/environment/log4j.properties";*/
		
		// ��ȡ���������ļ�����ʼ��log4j���
		PropertyConfigurator.configure(log);
		// ��ȡĳ�������
		logger = Logger.getLogger(RootLogger.class);
	}
	
	

	public LogImpl() {

	}

	@Override
	public void debug(String message) {
		logger.debug(message);
	}

	@Override
	public void info(String message) {
		logger.info(message);
	}

	@Override
	public void warn(String message) {
		logger.warn(message);
	}

	@Override
	public void error(String message) {
		logger.error(message);
	}

	@Override
	public void fatal(String message) {
		logger.fatal(message);
	}


}