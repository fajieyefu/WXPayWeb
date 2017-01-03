package com.pay.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.pay.bean.Constants;
import com.pay.bean.MD5;
import com.pay.bean.MD5Util;
import com.pay.bean.Util;
import com.service.Service;

public class PayServlet extends HttpServlet {
	Map<String, String> resultunifiedorder;
	String model_name, model_price, model_times, card_code;
	String out_trade_no;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest requset, HttpServletResponse resp)
			throws ServletException, IOException {
		model_name = requset.getParameter("model_name");
		model_name = new String(model_name.getBytes("ISO-8859-1"), "UTF-8");
		model_price = requset.getParameter("model_price");
		model_price = new String(model_price.getBytes("ISO-8859-1"), "UTF-8");
		model_times = requset.getParameter("model_times");
		model_times = new String(model_times.getBytes("ISO-8859-1"), "UTF-8");
		card_code = requset.getParameter("card_code");
		card_code = new String(card_code.getBytes("ISO-8859-1"), "UTF-8");
		// 获取PrepayId
		String url = String
				.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
		String entity = genProductArgs();
		byte[] buf = Util.httpPost(url, entity);
		String content = new String(buf, "UTF-8");
		Service serv = new Service();
		String info = serv.makeOder(card_code, model_name, model_price,
				model_times, out_trade_no);
		System.out.println(info);
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		System.out.println(content);
		out.print(content);
		out.flush();
		out.close();

	}

	private String genProductArgs() {
		StringBuffer xml = new StringBuffer();

		try {
			String nonceStr = genNonceStr();

			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams
					.add(new BasicNameValuePair("appid", Constants.APP_ID));
			packageParams.add(new BasicNameValuePair("body",
					"linyichengcaikeji"));
			packageParams
					.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url",
					"http://123.132.252.2:9891/WXPayWeb/WXNotifyServlet"));
			out_trade_no = genOutTradNo();
			packageParams.add(new BasicNameValuePair("out_trade_no",
					out_trade_no));
			packageParams.add(new BasicNameValuePair("spbill_create_ip",
					"123.132.252.2"));
			packageParams.add(new BasicNameValuePair("total_fee", Integer
					.parseInt(model_price) * 100 + ""));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);

			return xmlstring;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 生产交易单号
	 */
	private String genOutTradNo() {
		Random random = new Random();
		String outTradNo = MD5.getMessageDigest(String.valueOf(
				random.nextInt(10000)).getBytes());
		System.out.println(outTradNo);
		return outTradNo;
	}

	/**
	 * 生成签名
	 */
	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);
		System.out.println(sb.toString());

		String packageSign = "";
		try {
			packageSign = MD5.getMessageDigest(sb.toString().getBytes("utf-8"))
					.toUpperCase();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String packageSign =MD5Util.MD5Encode(sb.toString(),
		// "UTF-8").toUpperCase();
		System.out.println("packageSign:" + packageSign);
		return packageSign;
	}

	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		System.out.println("orion:" + sb.toString());
		return sb.toString();
	}

	private String genNonceStr() {
		Random random = new Random();
		String result = MD5.getMessageDigest(String.valueOf(
				random.nextInt(10000)).getBytes());
		System.out.println("noncestr:" + result);
		return result;
	}

}
