package com.aspire.cmppsgw.startup;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.aspire.cmppsgw.GlobalEnv;

/**
 * main system start up
 * 
 * @author Hu gaoyong
 * 
 */
public class BootStartup {
	private static Logger logger = Logger.getLogger(BootStartup.class);
	private String shutdown;
    private int port;
    
	public BootStartup(String shutdown,int port) {
		this.shutdown = shutdown;
		this.port = port;
	}

	/**
	 * ����������
	 */
	private void startup() throws Exception{
		new Server(shutdown,port).load();
	}

	/**
	 * ��ֹ������
	 */
	private void shutdown() {
		System.out.println("Stoping Server");
		Socket socket = null;
		try {
			socket = new Socket("127.0.0.1", port);
			OutputStream stream = socket.getOutputStream();
			for (int i = 0; i < shutdown.length(); i++) {
				stream.write(shutdown.charAt(i));
			}
			stream.flush();
			stream.close();
			socket.close();
			socket = null;
		} catch (IOException e) {

			e.printStackTrace();
			socket = null;
			System.err.println(e);
			System.exit(1);
		}
	}


	public static void main(String args[]) {

		
		try {
			String port = GlobalEnv.getInstance().getValue("system.shutdown.port");
			logger.debug("port:"+port);
			BootStartup boot = new BootStartup("shutdown",Integer.parseInt(port));
			
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String strDate = dateFormat.format(new Date());

			System.out
					.println("================ sendManager system main start at "
							+ strDate + " =============[port:" + port + "]");

			/**
			 * ���û�������в�������Ϊ�� start ����
			 */
			String command = "startup";
			if (args.length > 0) {
				command = args[args.length - 1]; // ����ж�������в�����ȡ���һ��
			}

			if (command.equals("startup")) {
				// ��������������
				System.out.println("Starting Server ...... ......");
				boot.startup();
			} else if (command.equals("shutdown")) {
				// ֹͣ����������
				System.out.println("Stoping Server ...... ......");
				boot.shutdown();
			}

			strDate = dateFormat.format(new Date());
			System.out
					.println("================ystem main end at "
							+ strDate + " ============[port:" + port + "]");

		} catch (Throwable t) {
			t.printStackTrace();
			System.out
			.println("================system start up fail");
			System.exit(1);
		}
	}

}
