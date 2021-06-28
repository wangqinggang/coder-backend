package com.ideaworks.club;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * WebHookController 用于处理WebHook 请求，验证通过后运行部署脚本
 * 
 * @author 王庆港
 * @version 1.0.0
 */
@org.springframework.web.bind.annotation.RestController
public class WebHookController {
	@RequestMapping(value = "/hello", method = RequestMethod.POST)
	public String name(@RequestBody String json) {
		JSONObject jsonObject = JSON.parseObject(json);
		String password = jsonObject.getString("password");
		String result = "";
		if (password.equals("william")) {
			// shell 脚本的路径
			String shell = "./test.sh";// TODO
			Process ps;
			try {
				ps = Runtime.getRuntime().exec(shell);
				ps.waitFor();

				BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\n");
				}
				  result = sb.toString();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return result;
		}

		return "fail";
	}
	
	
}
