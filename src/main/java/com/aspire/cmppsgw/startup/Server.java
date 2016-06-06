package com.aspire.cmppsgw.startup;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AccessControlException;
import java.util.Random;

import org.slf4j.Logger;

import com.aspire.cmppsgw.MonitorJob;
import com.aspire.cmppsgw.util.LogAgent;


/**
 * main server
 */
public class Server {
	Logger logger = LogAgent.systemInfoLogger;
	/**
	 * 随机数生成器
	 */
	private Random random = null;

	/**
	 * shutdown命令
	 */
	private String shutdown = null;

	/**
	 * 等待 shutdown 命令的端口号
	 */
	private int port;

	/**
	 * flag
	 */
	private boolean running = true;

	
	/**
	 * 构造函数
	 * 
	 * @param shutdown
	 *            服务器终止指令
	 * @param port
	 *            服务器终止指令监听端口
	 * @param scanner
	 *            黑名单扫描器
	 */
	public Server(String shutdown, int port) {
		this.shutdown = shutdown;
		this.port = port;
	
	}

	public Random getRandom() {
		return random;
	}

	public String getShutdown() {
		return shutdown;
	}

	public int getPort() {
		return port;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void load() throws Exception {
		start(); // 启动服务
		await(); // 等待终止服务命令
		stop(); // 终止服务
	}

	private void start() throws Exception {

		if (checkStarted()) {
			logger.info("Server has been started at [port:" + port
					+ "] ,System exit!!! !!!");

			// if server has been start then exit;
			System.exit(1);
		}
		MonitorJob.getInstance().start();
		logger.info("Starting Server success ");

	}

	/**
	 * check if the system started,if started return true;
	 * 
	 * @return
	 */
	private boolean checkStarted() {
		boolean bRet = false;

		Socket socket = null;
		try {
			socket = new Socket("127.0.0.1", port);
			// if socket is not null then the system has been started
			if (null != socket) {
				bRet = true;
				return bRet;
			}
		} catch (IOException ioe)// not started
		{
			bRet = false;
		}

		return bRet;
	}

	/**
	 * 等待shutdown命令，然后return
	 */
	public void await() {

		// 设立一个 Server Socket 来等待消息
		ServerSocket serverSocket = null;

		// 保证一个服务器启动一个进程
		try {

			serverSocket = new ServerSocket(port, 1,
					InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			System.err.println("Server.start: create[" + port + "]: \n " + e);
			serverSocket = null;
			System.exit(1);
		}

		// 循环等待一个连接，并带有合法的命令
		while (true) {

			// 等待下一次连接
			Socket socket = null;
			InputStream stream = null;
			try {
				socket = serverSocket.accept();
				socket.setSoTimeout(10 * 1000); // 10秒
				stream = socket.getInputStream();
			} catch (AccessControlException ace) {
				System.err.println("Server.accept security exception: "
						+ ace.getMessage() + "\n" + ace);
				continue;
			} catch (IOException e) {
				System.err.println("Server.await: accept: \n" + e);
				System.exit(1);
			}

			// 从Socket中读取一组字符
			StringBuffer command = new StringBuffer();
			int expected = 1024; // Cut off to avoid DoS attack
			while (expected < shutdown.length()) {
				if (random == null)
					random = new Random(System.currentTimeMillis());
				expected += (random.nextInt() % 1024);
			}
			while (expected > 0) {
				int ch = -1;
				try {
					ch = stream.read();
				} catch (IOException e) {
					System.err.println("Server.await: read: \n" + e);
					ch = -1;
				}
				if (ch < 32) // Control character or EOF terminates loop
					break;
				command.append((char) ch);
				expected--;
			}

			// Close the socket now that we are done with it
			try {
				socket.close();
			} catch (IOException e) {
				;
			}

			// Match against our command string
			boolean match = command.toString().equals(shutdown);
			if (match) { // 是否匹配
				break; // 如果匹配则退出循环
			} else {
				logger.error("Server.await: Invalid command '"
						+ command.toString() + "' received");
			}
		}

		// 关闭 server socket 并且return
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void stop() {
		MonitorJob.getInstance().stopJob();
		logger.info("Stoping Server sucess");
		System.exit(1);
	}
}
