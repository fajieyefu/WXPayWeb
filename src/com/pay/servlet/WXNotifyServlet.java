package com.pay.servlet;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmlpull.v1.XmlPullParser;


import com.service.Service;

public class WXNotifyServlet extends HttpServlet {
	String result, info;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		DataInputStream in;
		String wxNotifyXml = "";
		try {
			in = new DataInputStream(req.getInputStream());
			byte[] dataOrigin = new byte[req.getContentLength()];
			in.readFully(dataOrigin); // ���ݳ��ȣ�����Ϣʵ������ݶ����ֽ�����dataOrigin��

			if (null != in)
				in.close(); // �ر�������
			wxNotifyXml = new String(dataOrigin); // ���ֽ������еõ���ʾʵ����ַ���
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String,String> params=decodeXml(wxNotifyXml);
		String return_code=params.get("return_code");
		if (return_code.equals("SUCCESS")) {
			String device_info=params.get("device_info");// ΢���ն��豸��
			String result_code=params.get("result_code");
			if (result_code.equals("SUCCESS")) {
				String transaction_id=params.get("transaction_id");// ΢��֧��������
				String out_trade_no=params.get("out_trade_no");// ��Ʒ������
				String time_end=params.get("time_end");// ��������ʱ��
				Service serv = new Service();
				info = serv.changeOrder(out_trade_no, device_info,
						transaction_id, time_end);
			}
		} else {
			String return_msg=params.get("return_msg");
			System.out.println(return_msg);
		}
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.print("success");
		out.flush();
		out.close();
	}
	public Map<String, String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser =null;
//            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if (!("xml".equals(nodeName)) ) {
                            // ʵ����student����
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            System.out.println("orion:" + e.toString());
        }
        return null;

    }

}
