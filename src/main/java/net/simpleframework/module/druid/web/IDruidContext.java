package net.simpleframework.module.druid.web;

import net.simpleframework.ctx.IModuleContext;
import net.simpleframework.mvc.IMVCContextVar;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IDruidContext extends IModuleContext, IMVCContextVar {

	static String MODULE_NAME = "simple-module-druid";
}
