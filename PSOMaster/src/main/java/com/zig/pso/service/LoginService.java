/************************************************************************************************************
 * Class Name : LoginService.java
 * Description: This interface contains methods for order related operations.
 * Author : Aniket Limaye 
 * Date : Aug 26, 2016
 ************************************************************************************************************/

package com.zig.pso.service;

import com.zig.pso.rest.bean.BaseResponseBean;
import com.zig.pso.rest.bean.LoginRequestBean;
import com.zig.pso.rest.bean.UpdateOrderRequestBean;


public interface LoginService
{
	
/**
 * @param loginRequest 
 * @param OrderId
 * @return
 */
public BaseResponseBean doLogin(LoginRequestBean loginRequest);
	
}