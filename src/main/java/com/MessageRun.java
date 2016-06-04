package com;

import com.sr178.game.framework.context.SpringLoad;
import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.framework.plugin.AppPluginFactory;

public class MessageRun {

	public static void main(String[] args) {
		SpringLoad.getApplicationLoad();
		// 启动各个应用插件
		AppPluginFactory.startup();
		LogSystem.info("短信发送服务器启动成功");

	}

}
